package com.main;

import java.util.*;
import java.net.*;

public class Broadcaster extends TimerTask {

    ArrayList<InetAddress> broadcastAddresses;
    private byte[] message;
    private int port;
    DatagramSocket socket;

    /**
     * Constructor to create a new Broadcaster
     * Sets the socket, port, message and the list of broadcast addresses
     *
     * @param list
     */
    public Broadcaster(ArrayList<InetAddress> list) {
        // create a new Socket
        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("Socket could not be created: " + e);
        }
        // set Port and Broadcast-Message
        port = 9876;
        message = ("Dieser Server wurde von Jannis Rieger, Steffen Ansorge und Nicolai Brandt implementiert und stellt die Fibonacci-Funktion als Dienst bereit. Um den Dienst zu nutzen, senden Sie eine Nachricht an Port 65535 auf diesem Server. Das Format der Nachricht sollte folgendermaßen aussehen: ASCII encoded String: 'get;[nummer]' Um die Fibonaccizahl der Zahl 5 zu erhalten muss z.B. 'get;5' übermittelt werden.").getBytes();
        // save broadcastAddresses
        broadcastAddresses = list;
    }

    /**
     * Runs the broadcaster (on a timer) and sends out the Packets (on timer tick)
     */
    public void run() {
        System.out.println("[BROADCASTER]: Broadcast running.");
        // for every Broadcast-Address in reach...
        for (InetAddress curInstance : broadcastAddresses) {
            System.out.println("[BROADCASTER]: " + curInstance);
            // ...create a Packet, fill it with the Message etc...
            DatagramPacket packet = new DatagramPacket(message, message.length, curInstance, port);
            try {
                // ...and send it
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("[BROADCASTER]: An error occoured while sending Broadcast.");
                System.out.println(e);
            }
        }
    }
}
