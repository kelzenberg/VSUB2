package com.main;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.out;
import com.main.Broadcaster;

public class Networking {

    static ArrayList<InetAddress> broadcastAddresses;

    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> netIntf = NetworkInterface.getNetworkInterfaces();
        ArrayList<InetAddress> broadcastAddresses = new ArrayList<>();
        for (NetworkInterface x : Collections.list(netIntf))
            displayInterfaceInformation(x);
            
        Timer timer = new Timer();
        timer.schedule(new Broadcaster(broadcastAddresses), 0, 5000);
    }

    private static void displayInterfaceInformation(NetworkInterface netintf) throws SocketException {
        out.printf("Display name: %s\n", netintf.getDisplayName());
        out.printf("Name: %s\n", netintf.getName());
        List<InterfaceAddress> intfAdresses = netintf.getInterfaceAddresses();
        for (InterfaceAddress intfAdress : intfAdresses) {
            out.printf("| InetAddress: %s\n", intfAdress.getAddress());
            out.printf("| | Binary: %s\n", getBinaryAddress(intfAdress));
            out.printf("| IP-Version: %s\n", getIPVersion(intfAdress));
            if (intfAdress.getBroadcast() != null) {
                out.printf("| Broadcast: %s\n", intfAdress.getBroadcast() );
                broadcastAddresses.add(intfAdress.getBroadcast());
            }
            out.printf("| Prefix: %s\n", intfAdress.getNetworkPrefixLength());
            // swap to getBinaryAdress
            out.printf("| | Network: %s\n", getBinaryAddress(intfAdress).substring(0,intfAdress.getNetworkPrefixLength()));
            out.printf("| | Client: %s\n", getBinaryAddress(intfAdress).substring(intfAdress.getNetworkPrefixLength()));
            out.printf("\n");
        }
        out.printf("\n");
    }

    private static String getIPVersion(InterfaceAddress intfAdress) {
        if (intfAdress.getAddress().toString().contains(".")) {
            return "IPv4";
        } else {
            return "IPv6";
        }
    }

    private static long getBinary(InterfaceAddress intfAdress) {
        byte[] bytes = intfAdress.getAddress().getAddress();
        long out = 0;
        for (Byte x : bytes) {
            // shift 8 bits to the left e.g. 01111111 -> 01111111 00000000 + x
            out = (out << 8) + x;
        }
        return out;
    }
    
    private static String getBinaryAddress(InterfaceAddress intfAdress) {
        String version = getIPVersion(intfAdress);
        if(version == "IPv4"){
            return String.format("%1$" + 32 + "s", Long.toBinaryString(getBinary(intfAdress))).replace(" ", "0");
        } else {
            return String.format("%1$" + 128 + "s", Long.toBinaryString(getBinary(intfAdress))).replace(" ", "0");
        }
    }
}
