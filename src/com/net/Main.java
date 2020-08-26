package com.net;
import java.net.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.out;

public class Main {
    static String myIpAddress()
    {
        try
        {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            String ip = socket.getLocalAddress().getHostAddress();
            out.println(socket.getLocalAddress().getHostAddress());
            return ip;
        }
        catch (Exception e)
        {
            System.out.println("Error in getting my ip  is : " + e);
        }
        return null;
    }


static boolean sendRequestToIp(String ip)
{
    try {
        if (InetAddress.getByName(ip).isReachable(200))
            return true;
    }
    catch (Exception exception)
    {
        out.println("Error in sending request to ip :" + exception);
    }
    return false;
}


    static void findAllIp(String ip, short subnet) {
        //get Binary Subnet Mask
        StringBuilder subnetMask = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i <= subnet)
                subnetMask.append("1");
            else
                subnetMask.append("0");
            if (i % 8 == 0 && i != 0)
                subnetMask.append(".");
        }
        // transform Ip to Binary
        String[] dataIp = ip.split("\\.", 4);
        StringBuilder binaryIp = new StringBuilder();
        for (String str : dataIp) {
            StringBuilder res = new StringBuilder(Integer.toBinaryString(Integer.parseInt(str)));
            while (res.length() < 8)
                res.insert(0, "0");
            binaryIp.append(res);
            //for not adding point in the last 32 + 3 point
            if (binaryIp.length() != 35)
                binaryIp.append(".");
        }

        //get The NetworkIp and the broadcastIp in Binary
        StringBuilder BroadcastIpBinary = new StringBuilder();
        StringBuilder networkIpBinary = new StringBuilder();
        for (int i = 0; i < binaryIp.length(); i++)
        {
            if (binaryIp.charAt(i) == '.')
            {
                networkIpBinary.append(".");
                BroadcastIpBinary.append(".");
            }
            else
            {
                char IpChar = binaryIp.charAt(i);
                char subnetChar = subnetMask.charAt(i);

                if (IpChar == '0' || subnetChar == '0')
                    networkIpBinary.append("0");
                else
                    networkIpBinary.append("1");

                if (i  > subnet)
                    BroadcastIpBinary.append("1");
                else
                    BroadcastIpBinary.append(binaryIp.charAt(i));
            }
        }

        // transform all Ip to Decimal (NetworkIp and BroadcastIp)
        String networkIp = "";
        String broadcastIp = "";
        dataIp = String.valueOf(networkIpBinary).split("\\.", 4);
        String [] dataIp2 = String.valueOf(BroadcastIpBinary).split("\\.", 4);
        for (int i = 0; i < 4; i++)
        {
            networkIp += (Integer.parseInt(String.valueOf(Integer.valueOf(dataIp[i])), 2));
            broadcastIp +=(Integer.parseInt(String.valueOf(Integer.valueOf(dataIp2[i])), 2));
            if (i != 3)
            {
                networkIp += (".");
                broadcastIp += (".");
            }
        }

        //get the range of all possible ip host in the network that start from the network ip + 1 to Broadcast - 1
        String currentIp = networkIp;

        dataIp2 = (broadcastIp).split("\\.", 4);
        while (true)
        {
            String[] dataCurrentIp = String.valueOf(currentIp).split("\\.", 4);
            if (currentIp.equals(broadcastIp))
                break;
            //change the next ip
            if (Integer.parseInt(dataCurrentIp[3]) < 255) {
                dataCurrentIp[3] = String.valueOf(1 + Integer.parseInt(dataCurrentIp[3]));
            }
            else if (Integer.parseInt(dataCurrentIp[2]) != Integer.parseInt(dataIp2[2]))
            {
                dataCurrentIp[2] = String.valueOf(1 + Integer.parseInt(dataCurrentIp[2]));
                dataCurrentIp[3] = String.valueOf(0);
            }
            else if (Integer.parseInt(dataCurrentIp[1]) != Integer.parseInt(dataIp2[1]))
            {
                dataCurrentIp[1] = String.valueOf(1 + Integer.parseInt(dataCurrentIp[1]));
                dataCurrentIp[2] = String.valueOf(0);
                dataCurrentIp[3] = String.valueOf(0);
            }
            else if (Integer.parseInt(dataCurrentIp[0]) != Integer.parseInt(dataIp2[0]))
            {
                dataCurrentIp[0] = String.valueOf(1 + Integer.parseInt(dataCurrentIp[0]));
                dataCurrentIp[1] = String.valueOf(0);
                dataCurrentIp[2] = String.valueOf(0);
                dataCurrentIp[3] = String.valueOf(0);
            }
            int rpt = 1;
            currentIp = "";
            for (String str : dataCurrentIp)
            {
                currentIp += str;
                if (rpt < 4) {
                    currentIp += ".";
                }
                rpt++;

            }
            //Check ip if is in network then save it in the list
            ArrayList<String> ipList  = new ArrayList<>();
            out.println("test ip         " + currentIp);


            if (sendRequestToIp(currentIp))
            {
                ipList.add(currentIp);
                out.println("current ip         " + currentIp);
            }

        }

        /*out.println("subnet in binary        " + subnetMask);
        out.println("ip in binary            " + binaryIp);
        out.println("network ip in binary    " + networkIpBinary);
        out.println("network ip              " + networkIp);
        out.println("broadcast  ip in binary " + BroadcastIpBinary);
        out.println("broadcast  ip           " + broadcastIp);*/
    }

    public static void main(String[] args){
        String ip = myIpAddress();
        /*if (InetAddress.getByName("192.168.1.5").isReachable(200))
        {
            System.out.println(" is reachable");
        }*/
        short sub = 24;
        //findAllIp(ip, sub);
        if (sendRequestToIp("192.168.1.6"))
            out.println("true");

    }

}
