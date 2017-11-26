package com.receiver;
/**
 *
 * Listens to broadcasts and connects to server using its broadcast message.
 * Then requests the calculation of a specific fibonacci number and returns the result.
 *
 */

import java.net.*;
import java.util.regex.*;
import java.io.*;
import java.lang.String;

public class Receiver {

    public static void main(String args[]) throws Exception {
        // Create a buffer that will hold the message
        byte[] dataBuffer = new byte[1000];
        // Create a new socket and packet
        DatagramSocket socket = new DatagramSocket(9876);
        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);

        while( true ) {
            // block execution until a packet is received
            socket.receive(packet);
            if (packet.getData().length == 0)continue;
            System.out.print("Received message from ");
            System.out.println(packet.getAddress());
            // get data from packet as string
            String data = new String(packet.getData(),"UTF-8");
            System.out.println(data);

            // parse the broadcast message and find port number to connect to
            Pattern p = Pattern.compile("Port (\\d+?) auf");
            Matcher m = p.matcher(data);
            if(m.find()) {
                System.out.println("Port found: " + m.group(1));
                int port = Integer.parseInt(m.group(1));
                // send a new fibonacci request to server
                sendFibonacciRequest(packet.getAddress(),port);
            } else {
                System.out.println("No valid port found!");
            }
        }
    }

    /**
     * Send a request to the server requesting to calculate a specific fibonacci number
     * @param address the server address to connect to
     * @param port the port to use in communication with the server
     */
    public static void sendFibonacciRequest(InetAddress address,int port) {
        String result; // answer from the server
        Socket clientSocket; // new socket to connect with server
        DataOutputStream serverWriter;
        BufferedReader serverReader;

        try {
            // tries to open a socket to the server at address:port
            clientSocket = new Socket(address, port);
            //Stream to send data through the socket to the server
            serverWriter = new DataOutputStream(clientSocket.getOutputStream());
            //Stream for receiving data through the socket from the server
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // tries to send the entered value to the server
            serverWriter.writeBytes("get;17\n");
        } catch (Exception e) {
            // Connection failed
            System.out.println("Network error.");
            System.out.println(e);
            return;
        }
        System.out.println("Connection established.");
        
        // Repeat until the communication with the server has ended.
        while (true) {
            try {
                // saves the result
                result = serverReader.readLine();
                if (result == null){
                    throw new IOException("");
                }
                // Sends an acknowledgment of receipt back to the server
                serverWriter.writeBytes("ok;\n");
            } catch(IOException e) {
                result = "Connection closed.";
                break;
            }
                        
            //parses the response of the server
            result = parseResponse(result);
            
            if (result == "") {
                // The server caluclates the number
                System.out.println("Server is calculating...");
            }  else {
                // The server has send a response. This can either be the calculated number or an error message.
                break;
            }
        }
        
        // Prints the result
        System.out.println(result);
        try {
            clientSocket.close();
            System.out.println("Socket closed!");
        } catch(Exception e){
            System.out.println("Socket couldn't be closed.");
        }
    }

    /**
     * Parse the servers response and return readable messages.
     * @param response is the response the server gave upon fibonacci request.
     * @return the answer the server gave via status code.
     */
    private static String parseResponse(String response) {
        String[] data;
        // Splits the response intro responseCode and payload
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
