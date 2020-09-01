package com.net;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class capturePckages {

    public static void main(String[] args) {
        try {
            // Will be filled with NICs
            List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs

            // For any error msgs
            StringBuilder errbuf = new StringBuilder();

            //Getting a list of devices
            int r = Pcap.findAllDevs(alldevs, errbuf);
            System.out.println(r);
            if (r != Pcap.OK) {
                System.err.printf("Can't read list of devices, error is %s", errbuf
                        .toString());
                return;
            }

            System.out.println("Network devices found:");
            int i = 0;
            for (PcapIf device : alldevs) {
                String description =
                        (device.getDescription() != null) ? device.getDescription()
                                : "No description available";
                System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
            }
            System.out.println("choose the one device from above list of devices");
            int ch = new Scanner(System.in).nextInt();
            PcapIf device = alldevs.get(ch);

            int snaplen = 64 * 1024;           // Capture all packets, no trucation
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis

            //Open the selected device to capture packets
            Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

            if (pcap == null) {
                System.err.printf("Error while opening device for capture: " + errbuf.toString());
                return;
            }
            System.out.println("device opened");

            //Create packet handler which will receive packets
            PcapPacketHandler jpacketHandler = new PcapPacketHandler()
            {
                public void nextPacket(PcapPacket packet, Object user)
                {
                    Ip4 ip = new Ip4();
                    if (packet.hasHeader(ip) == false)
                    {
                        System.out.println("Non Ip for Packet");
                        return; // Not IP packet
                    }
                    /* Use jNetPcap format utilities */
                    String sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
                    String destinationIP = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
                    PcapHeader header = packet.getCaptureHeader();
                    System.out.println("IP_source       " + sourceIP + "       dstIP=       " + destinationIP);
                }
            };

            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
            pcap.loop(100, jpacketHandler, "jNetPcap");
            //Close the pcap
            pcap.close();
        } catch (Exception ex) {
            System.out.println("Erore is :" + ex);
        }
    }

}
