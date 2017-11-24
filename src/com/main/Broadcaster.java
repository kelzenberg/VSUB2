package com.main;

import java.util.*;
import java.net.*;

public class Broadcaster extends TimerTask {
    
    ArrayList<InetAddress> broadcastAddresses;
    private byte[] message;
    private int port;
    DatagramSocket socket;
    
    public Broadcaster(ArrayList<InetAddress> list) {
        try {
            socket = new DatagramSocket();
        } catch(Exception e) {
            
        }  
        port = 9876;
        message = ("Dieser Server wurde von Jannis Rieger, Steffen Ansorge und Nicolai Brandt implementiert und stellt die Fibonacci-Funktion als Dienst bereit. Um den Dienst zu nutzen, senden Sie eine Nachricht an Port 65535 auf diesem Server. Das Format der Nachricht sollte folgendermaßen aussehen: ASCII encoded String: 'get|[nummer]' Um die Fibonaccizahl der Zahl 5 zu erhalten muss z.B. 'get|5' übermittelt werden.").getBytes();
        
        broadcastAddresses = list;
    }
    
    public void run() {
        System.out.println("Broadcast running.");
        for (InetAddress curInstance: broadcastAddresses) {
            System.out.println(curInstance);
            DatagramPacket packet = new DatagramPacket(message, message.length, curInstance, port);
            try {
                socket.send(packet);
            } catch(Exception e) {
                System.out.println("An error occoured while sending Broadcast.");
                System.out.println(e);
            }
        }
    }
}