package com.net;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

class deviceInfo
{
    String deviceName;
    String deviceIp;
    String DeviceMacAddress;
}

//This class well display all packet information
class PacketInfo
{
    PcapPacket packet;
    String sourceIp;
    String destinationIp;
    int sourcePort = -1;
    int destinationPort = -1;
    String sourceMac;
    String destinationMac;
    String headerType;
    int packetIndex;
    //Add function to every protocol to show it
    public PacketInfo(PcapPacket packet, int i)
    {
        this.packet = packet;
        this.packetIndex = i;
        Ip4 ip = new Ip4();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        Http http = new Http();
        packet.hasHeader(ip);
        sourceIp = FormatUtils.ip(ip.source());
        destinationIp = FormatUtils.ip(ip.destination());
        Ethernet ethernet = new Ethernet();
        if (packet.hasHeader(ethernet))
        {
            sourceMac = FormatUtils.mac(ethernet.source());
            destinationMac = FormatUtils.mac(ethernet.destination());
        }
        if (packet.hasHeader(tcp))
        {
            sourcePort = tcp.source();
            destinationPort = tcp.destination();
            headerType = "Tcp";
            if (packet.hasHeader(http))
                headerType = "http";

        }
        else if (packet.hasHeader(udp))
        {
            sourcePort = udp.source();
            destinationPort = udp.destination();
            headerType = "Udp";

        }
        System.out.printf("%s  %s  Packet From Ip  %s  to Ip  %s  from Port  %s  to Port  %s \n",
                String.format("%-5s", packetIndex), String.format("%-6s", headerType), String.format("%-15s",sourceIp), String.format("%-15s",destinationIp), String.format("%-6s", sourcePort), String.format("%-6s",(destinationPort)));

    }

}
