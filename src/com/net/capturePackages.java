package com.net;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.winpcap.WinPcap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;
import static  java.lang.System.out;

public class capturePackages
{
    static int packetIndex = 0;
    static int packetnumber = 0;
    static ArrayList<PacketInfo> allPacketObjet = new ArrayList<PacketInfo>();

    public static void main(String[] args)
    {
        try {
            if (!WinPcap.isSupported())
            {
                out.println("Can't use WinPcap extensions");
                return;
            }

            out.println(1);
            //firstly we are getting list of interfaces
            // Will be filled with NICs
            List<PcapIf> allDevs = new ArrayList<>(); // Will be filled with NICs
            // For any error msgs
            StringBuilder errorString = new StringBuilder();
            //Getting a list of devices
            int r = Pcap.findAllDevs(allDevs, errorString);
            System.out.println(r);
            if (r != 0)
            {
                System.err.printf("Can't read list of devices, error is %s", errorString.toString());
                return;
            }
            System.out.println("Network devices found:");
            int i = 0;
            for (PcapIf device : allDevs)
            {
                String description = (device.getDescription() != null) ? device.getDescription(): "No description available";
                out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
            }
            System.out.println("choose one device from above list of devices (enter index number)");
            int indexSelected = new Scanner(System.in).nextInt();
            //The interface that user has selected

            PcapIf device = allDevs.get(indexSelected);
            int size = 64 * 1024;           // Capture all packets,
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis
            //Open the selected device to capture packets
            Pcap pcap = Pcap.openLive(device.getName(),size, flags, timeout, errorString);
            if (pcap == null)
            {
                System.err.print("Error while opening device for capture: " + errorString.toString());
                return;
            }

            System.out.println("device opened");
            System.out.println("enter number of packet you want to capture (-1 for infinity)");
            System.out.println("you can stop capturing anytime on pressing < q > Key");
            packetnumber = new Scanner(in).nextInt();

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
                //If key is pressed
                try {
                    String enter = String.valueOf(System.in.read());
                    out.println("you pressed " + enter);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ///////////////////

            };

            //Show detail of packet by taken his index from user
            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
            pcap.loop(packetnumber, packetHandler, 5);

            //Close the pcap
            pcap.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error is :" + ex);
        }
    }

}
