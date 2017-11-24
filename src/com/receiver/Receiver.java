package com.receiver;

import java.net.*;
import java.util.*;

public class Receiver {


    public static void main(String args[]) throws Exception {
        byte[] dataBuffer = new byte[1000];
        DatagramSocket socket = new DatagramSocket(9876);
        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
        
        while( true) {
            socket.receive(packet);
            if (packet.getData().length == 0)continue;
            System.out.print("Received message from ");
            System.out.println(packet.getAddress());
            String data = new String(packet.getData(),"UTF-8");
            System.out.println(data);
        }
    }
}
