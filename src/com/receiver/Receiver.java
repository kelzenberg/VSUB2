package com.receiver;

import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.String;

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
            
            Pattern p = Pattern.compile("Port (\\d+?) auf");
            Matcher m = p.matcher(data);
            if(m.find()) {
                System.out.println("Port found: " + m.group(1));
                int port = Integer.parseInt(m.group(1));
                sendFibonacciRequest(packet.getAddress(),port);
            } else {
                System.out.println("No valid port found!");
            }
        }
    }
    
    public static void sendFibonacciRequest(InetAddress address,int port) {
        String result; //answer from the server
        Socket clientSocket; //The socket
        DataOutputStream serverWriter;
        BufferedReader serverReader;
        try {
            // tries to open a socket to the server at address:port
            clientSocket = new Socket(address, port);
        } catch (Exception e) {
            // Connection failed
            System.out.println("Server not reachable.");
            return;
        }
        System.out.println("Connection established.");

        try {
            //Stream to send data through the socket to the server
            serverWriter = new DataOutputStream(clientSocket.getOutputStream());
            //Stream for receiving data through the socket from the server
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(Exception e) {
            System.out.println("Network error");
            System.out.println(e);
            return;
        }

        try {
            // tries to send the entered value to the server
            serverWriter.writeBytes("get;17\n");
        } catch (IOException e) {
            //An error occoured while writing the value to the server or while reading the response
            System.out.println("Connection closed.");
            result = ""; // this needs to be set otherwise it doesn't compile
        }
        
        while (true) {
            try {
                // saves the result
                result = serverReader.readLine();
            } catch(IOException e) {
                result = "Connection closed.";
                break;
            }
            
            try {
                // tries to send the entered value to the server
                serverWriter.writeBytes("ok;\n");
            } catch (IOException e) {
                //An error occoured while writing the value to the server or while reading the response
                result = "Connection closed.";
            }
            
            //parses the response of the server
            result = parseResponse(result);
            
            // The server caluclates the number
            if (result != "") {
                break;
            }  else {
                System.out.println("Server is calculating...");
            }
        }
        
        //Prints the result
        System.out.println(result);
        try {
            clientSocket.close();
            System.out.println("Socket closed!");
        } catch(Exception e){
            System.out.println("Socket couldn't be closed.");
        }
    }
    
    private static String parseResponse(String response) {
        if (response == null) {
            //The socket read null. The socket broke
            System.out.println("Connection closed.");
        }
        String[] data;
        //Splits the response intro responseCode and payload
        data = response.split(";");
        switch (data[0]) {
            // 100 Continue
            case "100":
                return "";
            // 200 OK
            case "200":
                return "Result: " + data[1];
            // 4xx The request contained an error
            // The entered data was invalid
            case "401":
                return "Invalid input.";
            // The entered number was too low
            case "402":
                return "Number too low.";
            // 5xx The server ran into an error
            // The server is not able to execute the calculation on a number that large
            case "501":
                return "The number is too large for the server.";
            // The responseCode was not valid
            default:
                return "The server didn't respond correctly. " + response;
        }
    }
}
