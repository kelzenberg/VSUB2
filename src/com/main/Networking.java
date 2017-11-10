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
        Enumeration<InetAddress> inetAddresses = netintf.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
    }

    private String getIPVersion(){
        return null;
    }
}
