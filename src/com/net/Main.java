package com.net;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.lang.ProcessBuilder;

import static java.lang.System.*;

public class Main {

    static String myIpAddress()
    {
        try
        {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            return socket.getLocalAddress().getHostAddress();
        }
        catch (Exception e)
        {
            System.out.println("Error in getting my ip  is : " + e);
        }
        return null;
    }


// This function give as all macAddress of any ip in network using shell command
static String getMacAddressShell(String ip)
{
    try
    {
        ProcessBuilder builder = new ProcessBuilder("arp", "-a", ip);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader  bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String[] dataFromLine = new String[3];
        while ((line = bufferedReader.readLine()) != null)
        {
            dataFromLine = line.split("\\s+");
        }
        return dataFromLine[3];
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }

    return null;
}
static String  getMacAddress(String ip)  {
        try
        {
            // Her we are getting our mac address
            InetAddress inetAddress = InetAddress.getByName(ip);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            byte [] hardwareAddress = networkInterface.getHardwareAddress();
            String[] hexadecimalFormat = new String[hardwareAddress.length];
            // transform hexadecimal to decimal format
            for (int i = 0; i < hardwareAddress.length; i++)
                // 02x mean add 0 to beginning if is just one digit
                hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
            return String.join("-", hexadecimalFormat);
        }
        catch (Exception exception)
        {
            out.println("error in getting Mac address : " + exception);
        }
    return  null;
    }

static boolean sendRequestToIp(String ip)
{
    // This function test ip if is network or not
    try {
        if (InetAddress.getByName(ip).isReachable(300))
            return true;
    }
    catch (Exception exception)
    {
        out.println("Error in sending request to ip :" + exception);
    }
    return false;
}

    // This function get all ip possible in network by calculate them using subnetMask
    static ArrayList<deviceInfo> findAllIp(String myIp, short subnet) {
        try
        {
            ArrayList <deviceInfo> deviceList = new ArrayList<>();
            //get Binary Subnet Mask
            StringBuilder subnetMask = new StringBuilder();
            for (int i = 0; i < 32; i++)
            {
                if (i <= subnet) {
                    subnetMask.append("1");
                }
                else
                    subnetMask.append("0");
                if (i % 8 == 0 && i != 0) {
                    subnetMask.append(".");
                }
            }
            // transform Ip to Binary
            String[] dataIp = myIp.split("\\.", 4);
            StringBuilder binaryIp = new StringBuilder();
            for (String str : dataIp)
            {
                StringBuilder res = new StringBuilder(Integer.toBinaryString(Integer.parseInt(str)));
                while (res.length() < 8) {
                    res.insert(0, "0");
                }
                binaryIp.append(res);
                //for not adding point in the last 32 + 3 point
                if (binaryIp.length() != 35) {
                    binaryIp.append(".");
                }
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

                    if (IpChar == '0' || subnetChar == '0') {
                        networkIpBinary.append("0");
                    }
                    else
                        networkIpBinary.append("1");

                    if (i  > subnet) {
                        BroadcastIpBinary.append("1");
                    }
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
              networkIp = networkIp.concat(String.valueOf(Integer.parseInt(String.valueOf(Integer.valueOf(dataIp[i])), 2)));
                broadcastIp = broadcastIp.concat(String.valueOf(Integer.parseInt(String.valueOf(Integer.valueOf(dataIp2[i])), 2)));
                if (i != 3)
                {
                    networkIp = networkIp.concat(".");
                    broadcastIp = broadcastIp.concat(".");
                }
            }
            out.println("Sheck Range Of Host between " + networkIp + " to " + broadcastIp);
            int numberOfIp = (int) Math.pow(2, 32 - subnet) - 2;
            int ipCalc = 1;
            int noIpNumber = 0;
            //get the range of all possible ip host in the network that start from the network ip + 1 to Broadcast - 1
            String currentIp = networkIp;

            dataIp2 = (broadcastIp).split("\\.", 4);
            while (true)
            {
                String[] dataCurrentIp = currentIp.split("\\.", 4);
                if (currentIp.equals(broadcastIp) || noIpNumber > 10)
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
                    currentIp = currentIp.concat(str);
                    if (rpt < 4) {
                        currentIp += ".";
                    }
                    rpt++;
                }
                //Check ip if is in network then save it in the list
                if (sendRequestToIp(currentIp))
                {
                    String ipName = InetAddress.getByName(currentIp).getHostName();
                    String macAddress = getMacAddressShell(currentIp);
                    if (currentIp.equals(myIp))
                    {
                        ipName = InetAddress.getLocalHost().getHostName();
                        macAddress = getMacAddress(currentIp);
                    }
                    deviceInfo device = new deviceInfo();
                    device.deviceip = currentIp;
                    device.deviceName = ipName;
                    device.DeviceMacAddress = macAddress;
                    deviceList.add(device);
                    //out.println("current ip         " + currentIp + "  Name of Ip is : " + ipName + "  mac Address : " + macAddress);
                }
                else
                    noIpNumber++;
                out.println((ipCalc * 100) / numberOfIp + "%...");
                ipCalc++;
            }
            out.println("Searching finish .");
            out.println("The are " + deviceList.size() + " Devices in your network");
            return deviceList;
        }
        catch (Exception exception)
        {
            out.println("erore in getting all ip : " + exception);
        }
        return null;
    }

     static void printALLDevices(ArrayList<deviceInfo> allDevices)
     {
         out.println("--------------------------------------------------------------------------------");
         out.println("| Device Name                    | Device IP            | Device Mac Address   |");
         out.println("--------------------------------------------------------------------------------");
         for (deviceInfo device : allDevices)
         {
             //String deviceName
             out.println("| " + String.format("%-30s",device.deviceName) + " | " + String.format("%-20s",device.deviceip) + " | " + String.format("%-20s",device.DeviceMacAddress) + " |");
             out.println("--------------------------------------------------------------------------------");

         }
     }



    public static void main(String[] args)
    {
        String myIp = myIpAddress();
        short sub = 24;
       ArrayList <deviceInfo> allDevices =  findAllIp(myIp, sub);
       printALLDevices(allDevices);
     //   getMacAddressShell("192.168.1.1");
    }



}
