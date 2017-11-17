package com.main;

import java.util.*;
import java.net.*;

public class Broadcaster extends TimerTask {
    ArrayList<InetAddress> broadcastAddresses;
    
    public Broadcaster(ArrayList<InetAddress> list) {
        broadcastAddresses = list;
    }
    
    public void run() {
        System.out.println("Broadcast running.");
        for (InetAddress curInstance: broadcastAddresses) {
            System.out.println(curInstance);
        }
    }
}