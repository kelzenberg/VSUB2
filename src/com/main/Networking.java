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
            out.printf("| IP-Version: %s\n", getIPVersion(intfAdress));
            out.printf("| Prefix: %s\n", intfAdress.getNetworkPrefixLength());
            out.printf("\n");
        }
        out.printf("\n");
    }

    private static String getIPVersion(InterfaceAddress intfAdress){
        if (!intfAdress.getAddress().toString().contains(".")){
            return "IPv6";
        } else {
            return "IPv4";
        }
    }
}
