package com.main;

import java.net.*;
import java.util.*;

import com.fibonacci.FibonacciServer;

import static java.lang.System.out;

public class Networking {

    static ArrayList<InetAddress> broadcastAddresses;

    /**
     * Starts the FibonacciServer, the timer for the broadcast
     * and calls the display of networkinformation of the current networks available
     *
     * @param args
     * @throws SocketException
     */
    public static void main(String args[]) throws SocketException {
        // start Server with arguments
        FibonacciServer T1 = new FibonacciServer(new String[]{"-a", "0.0.0.0", "-p", "65535"});
        T1.start();
        // get all networking interfaces
        Enumeration<NetworkInterface> netIntf = NetworkInterface.getNetworkInterfaces();
        broadcastAddresses = new ArrayList<>();
        // for every interface, display the respective information
        for (NetworkInterface x : Collections.list(netIntf))
            displayInterfaceInformation(x);

        // start the timer to broadcast every X milliseconds
        Timer timer = new Timer();
        // create a broadcaster through the timer for every broadcast address
        timer.schedule(new Broadcaster(broadcastAddresses), 0, 5000);
    }

    /**
     * Displays most of the information from the network interfaces available to the console
     *
     * @param netintf
     * @throws SocketException
     */
    private static void displayInterfaceInformation(NetworkInterface netintf) throws SocketException {
        out.printf("Display name: %s\n", netintf.getDisplayName());
        out.printf("Name: %s\n", netintf.getName());
        // get all addresses from the interface
        List<InterfaceAddress> intfAdresses = netintf.getInterfaceAddresses();
        for (InterfaceAddress intfAdress : intfAdresses) {
            out.printf("| InetAddress: %s\n", intfAdress.getAddress());
            out.printf("| | Binary: %s\n", getBinaryAddress(intfAdress));
            out.printf("| IP-Version: %s\n", getIPVersion(intfAdress));
            if (intfAdress.getBroadcast() != null) {
                // add every broadcast address to the list of broadcast addresses
                out.printf("| Broadcast: %s\n", intfAdress.getBroadcast());
                broadcastAddresses.add(intfAdress.getBroadcast());
            }
            out.printf("| Prefix: %s\n", intfAdress.getNetworkPrefixLength());
            // swap to getBinaryAdress
            out.printf("| | Network: %s\n", getBinaryAddress(intfAdress).substring(0, intfAdress.getNetworkPrefixLength()));
            out.printf("| | Client: %s\n", getBinaryAddress(intfAdress).substring(intfAdress.getNetworkPrefixLength()));
            out.printf("\n");
        }
        out.printf("\n");
    }

    /**
     * Simple method to detect the IP Version (IPv4/6)
     * by analyzing the IP address String
     *
     * @param intfAdress
     * @return IP Version as a String
     */
    private static String getIPVersion(InterfaceAddress intfAdress) {
        if (intfAdress.getAddress().toString().contains(".")) {
            return "IPv4";
        } else {
            return "IPv6";
        }
    }

    /**
     * Helper method for getBinaryAddress() to shift bits to the correct position
     *
     * @param intfAdress
     * @return shifted bits as a long number
     */
    private static long getBinary(InterfaceAddress intfAdress) {
        byte[] bytes = intfAdress.getAddress().getAddress();
        long out = 0;
        for (Byte x : bytes) {
            // shift 8 bits to the left e.g. 01111111 -> 01111111 00000000 + x
            out = (out << 8) + x;
        }
        return out;
    }

    /**
     * Convert IP addresses to a binary string and fill up missing parts with zeroes
     *
     * @param intfAdress
     * @return Binary IP address as a String
     */
    private static String getBinaryAddress(InterfaceAddress intfAdress) {
        String version = getIPVersion(intfAdress);
        if (version == "IPv4") {
            return String.format("%1$" + 32 + "s", Long.toBinaryString(getBinary(intfAdress))).replace(" ", "0");
        } else {
            return String.format("%1$" + 128 + "s", Long.toBinaryString(getBinary(intfAdress))).replace(" ", "0");
        }
    }
}
