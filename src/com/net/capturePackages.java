package com.net;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

import javax.rmi.PortableRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class capturePackages {
    static int packetIndex = 0;
    static int packetNumber = 0;
    static int indexSelected = -1;
    static Pcap pcap;
    static  PcapPacketHandler packetHandler;
    static ArrayList<PacketInfo> allPacketObjet = new ArrayList<PacketInfo>();

    public static void capture(int capture_repeat, String filter) {
        try {
            List<PcapIf> allDevs = new ArrayList<>(); // Will be filled with NICs
            // For any error msgs
            StringBuilder errorString = new StringBuilder();
            //Getting a list of devices
            int r = Pcap.findAllDevs(allDevs, errorString);

            if (capture_repeat == 0)
            {
                //firstly we are getting list of interfaces
                // Will be filled with NICs

                System.out.println(r);
                if (r != 0) {
                    System.err.printf("Can't read list of devices, error is %s", errorString.toString());
                    return;
                }
                System.out.println("Network devices found:");

                int i = 0;
                for (PcapIf device : allDevs)
                {
                    String description = (device.getDescription() != null) ? device.getDescription() : "No description available";
                    out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
                }
                System.out.println("choose one device from above list of devices (enter index number)");
                indexSelected = new Scanner(System.in).nextInt();
                //The interface that user has selected

                System.out.println("enter number of packet you want to capture (-1 for infinity)");
                System.out.println("you can stop capturing anytime on pressing < q > Key");
                packetNumber = new Scanner(in).nextInt();
            }
            PcapIf device = allDevs.get(indexSelected);
            int size = 64 * 1024;           // Capture all packets,
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis
            //Open the selected device to capture packets
             pcap = Pcap.openLive(device.getName(), size, flags, timeout, errorString);
             //Her where you should add a filter
            PcapBpfProgram prg = new PcapBpfProgram();
            if (pcap.compile(prg, "dst host 192.168.1.5 and src host 192.168.1.1", 0, 0) != Pcap.OK)
            {
                System.err.println("Error while setting filter: " + pcap.getErr());
                return;
            }
            pcap.setFilter(prg);
            out.println("filter OK ");
            //////////////////////////////////////////
            //PcapBpfProgram program = new PcapBpfProgram();
            //pcap.compile(program, "dst port 443", )
            ///////////////////////////////////////
            if (pcap == null)
            {
                System.err.print("Error while opening device for capture: " + errorString.toString());
                return;
            }
            System.out.println("device opened");
            //Create packet handler which will receive packets
             packetHandler = (packet, user) ->
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
            pcap.loop(packetNumber, packetHandler, "Pcap");

            //Close the pcap
            pcap.close();
        } catch (Exception exception) {
            out.println(exception);
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(in);
        capture(0, null);
        String cmd;
        //read all command from the user
        while (true)
        {
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
                    if (index == -1) {
                        for (PacketInfo packetInfo : allPacketObjet)
                            out.println(packetInfo.packet.toString());
                    } else
                        out.println(allPacketObjet.get(index).packet.toString());
                    break;
                case "all":
                    printAllPacket();
                    break;
                case "capture":
                    capture(1, null);
                    break;
                case "exit":
                    return 0;
                default:
                    out.println("Command not found");
                    break;
            }

            return 1;
        }
        catch (Exception exception) {
            out.println("Error in Command : " + exception);
        }
        return -1;
    }

    private static void printAllPacket() {
        for (PacketInfo packet : allPacketObjet) {
            System.out.printf("%s  %s  Packet From Ip  %s  to Ip  %s  from Port  %s  to Port  %s \n",
                    String.format("%-5s", packet.packetIndex), String.format("%-6s", packet.headerType), String.format("%-15s", packet.sourceIp), String.format("%-15s", packet.destinationIp), String.format("%-6s", packet.sourcePort), String.format("%-6s", (packet.destinationPort)));
        }
    }
}
