package com.main;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.out;

public class Networking {

    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> netIntf = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface x : Collections.list(netIntf))
            displayInterfaceInformation(x);
    }

    private static void displayInterfaceInformation(NetworkInterface netintf) throws SocketException {
        out.printf("Display name: %s\n", netintf.getDisplayName());
        out.printf("Name: %s\n", netintf.getName());
        List<InterfaceAddress> intfAdresses = netintf.getInterfaceAddresses();
        for (InterfaceAddress intfAdress : intfAdresses) {
            out.printf("| InetAddress: %s\n", intfAdress.getAddress());
            out.printf("| | Binary: %s\n", Long.toBinaryString(getBinary(intfAdress)));
            out.printf("| IP-Version: %s\n", getIPVersion(intfAdress));
            out.printf("| Prefix: %s\n", intfAdress.getNetworkPrefixLength());
            out.printf("| | Network: %s\n", Long.toBinaryString(getBinary(intfAdress)).substring(0,intfAdress.getNetworkPrefixLength()/8));
            out.printf("| | Client: %s\n", Long.toBinaryString(getBinary(intfAdress)).substring(intfAdress.getNetworkPrefixLength()/8+1,//TODO BIS STRING LENGTH);
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
}
