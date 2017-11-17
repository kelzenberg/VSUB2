package com.receiver;

import java.net.*;
import java.util.*;

public class Receiver {


    public static void main(String args[]) throws Exception {
        byte[] dataBuffer = new byte[0];
        DatagramSocket socket = new DatagramSocket(9876, InetAddress.getByName("0.0.0.0"));
        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
        
        while( true) {
            socket.receive(packet);
            if (packet.getData().length == 0)continue;
            System.out.println(packet.getAddress());
            String data = new String(packet.getData(),"UTF-8");
            System.out.println(data);
            System.out.println(packet.getData().length);
            System.out.println(packet.getPort());
        }
    }
}
