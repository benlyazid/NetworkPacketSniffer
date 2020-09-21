package com.net;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static java.lang.System.in;
import static java.lang.System.out;

public class capturePackages
{
    static int packetIndex = 0;
    static Pcap pcap;
    static PcapIf device;
    static ArrayList<PacketInfo> allPacketObjet = new ArrayList<>();

    public static void capture(int capture_repeat, String filter)
    {
        try
        {
            // For any error msgs
            StringBuilder errorString = new StringBuilder();
            //Getting a list of devices
            int size = 64 * 1024;           // Capture all packets,
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis
            //Open the selected device to capture packets
             pcap = Pcap.openLive(device.getName(), size, flags, timeout, errorString);

             //Her where you should add a filter
            if (filter != null)
            {
                PcapBpfProgram prg = new PcapBpfProgram();
                if (pcap.compile(prg, filter, 0, 0) != Pcap.OK)
                {
                    System.err.println("Error while setting filter: " + pcap.getErr());
                    return;
                }
                pcap.setFilter(prg);
                out.println("filter OK ");
            }
            if (pcap == null)
            {
                System.err.print("Error while opening device for capture: " + errorString.toString());
                return;
            }
            System.out.println("device opened");
            //Create packet handler which will receive packets
             PcapPacketHandler packetHandler = (packet, user) ->
            {
                //Capture Just packet with IPV4
                Ip4 ip = new Ip4();
                if (!packet.hasHeader(ip))
                    return; // Not IP packet
                PacketInfo packetInfo = new PacketInfo(packet, packetIndex);
                allPacketObjet.add(packetInfo);
                packetIndex++;
            };

            //Show detail of packet by taken his index from user
            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
            pcap.loop(capture_repeat, packetHandler, "Pcap");

            //Close the pcap
            pcap.close();
        }
        catch (Exception exception)
        {
            out.println("Error in capture Traffics : " + exception);
        }
    }

    public static void main(String[] args)
    {
        choseInterface();
        out.println("Enter your command");
        Scanner scanner = new Scanner(in);
        //capture(0, null);
        String cmd;
        //read all command from the user
        while (true)
        {
            out.print("Script : $ ");
            cmd = scanner.nextLine();
            if (executeCommand(cmd) == 0)
                return;
        }
    }

    private static int executeCommand(String cmd)
    {
        try {
            String[] data = cmd.split("\\s+");
            int index;
            switch (data[0]) {
                case "get":
                    index = Integer.parseInt(data[1]);
                    if (index == -1)
                    {
                        for (PacketInfo packetInfo : allPacketObjet)
                            out.println(packetInfo.packet.toString());
                    }
                    else
                        out.println(allPacketObjet.get(index).packet.toString());
                    break;
                case "all":
                    printAllPacket();
                    break;
                case "capture":
                    if (data.length > 2 )
                    {
                        if (!data[2].equals("-filter"))
                        {
                            out.println("Command not found");
                            return 1;
                        }
                        StringBuilder filter = new StringBuilder();
                        for (int i = 3; i < data.length; i++)
                            filter.append(" ").append(data[i]);
                        capture(Integer.parseInt(data[1]), String.valueOf(filter));
                    }
                    else
                        capture(Integer.parseInt(data[1]), null);
                    break;
                case "exit":
                    spoofing.stopAllThred();
                    return 0;
                case "scan":
                    scan.getNetworkDevices();
                    break;
                case "kickOff":
                    spoofing.attack(data[1], device.getName());
                    break;
                case "connect":
                    spoofing.connectDevice(Integer.parseInt(data[1]));
                    break;
                case"devicesOff":
                    spoofing.showAllKickedDevice();
                    break;
                case "help":
                    spoofing.help();
                default:
                    out.println("Command not found");
                    break;
            }
            return 1;
        }
        catch (Exception exception)
        {
            out.println("Error in Command : " + exception);
        }
        return -1;
    }

    private static void printAllPacket()
    {
        for (PacketInfo packet : allPacketObjet)
        {
            System.out.printf("%s  %s  Packet From Ip  %s  to Ip  %s  from Port  %s  to Port  %s \n",
                    String.format("%-5s", packet.packetIndex), String.format("%-6s", packet.headerType), String.format("%-15s", packet.sourceIp), String.format("%-15s", packet.destinationIp), String.format("%-6s", packet.sourcePort), String.format("%-6s", (packet.destinationPort)));
        }
    }

    public static void choseInterface()
    {
        try
        {
            List<PcapIf> allDevs = new ArrayList<>(); // Will be filled with NICs
            // For any error msgs
            StringBuilder errorString = new StringBuilder();
            //Getting a list of devices
            int r = Pcap.findAllDevs(allDevs, errorString);
            //firstly we are getting list of interfaces
            System.out.println(r);
            if (r != 0)
            {
                System.err.printf("Can't read list of devices, error is %s", errorString.toString());
                return;
            }
            System.out.println("Network devices found:");
            int i = 0;
            for (PcapIf device : allDevs) {
                String description = (device.getDescription() != null) ? device.getDescription() : "No description available";
                out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
            }
            System.out.println("choose one device from above list of devices (enter index number)");
            int indexSelected = new Scanner(System.in).nextInt();
            //The interface that user has selected
            device = allDevs.get(indexSelected);
        }
        catch (Exception e)
        {
            out.println("Error in :" + e);
        }
    }
}

