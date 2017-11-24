package com.fibonacci;

import java.io.*;
import java.net.*;
import java.net.InetAddress;

/**
* FibonacciServer receives Input from FibonacciClient and uses
* FibonacciCalc to calculate the ordered Fibonacci number, then
* sends it back to the Client.
*
* Kill the process to stop the Server.
*
* @author Jannis Rieger
* Steffen Ansorge
* Nicolai Brandt
*/
public class FibonacciServer extends Thread {
    private static int port;
    private static InetAddress address;
    
    public FibonacciServer(String argv[]) {
        port = 8080; // default port for this app
        try {
            address = InetAddress.getByName("127.0.0.1"); // localhost
        } catch(Exception e) {
            
        }
        parseArguments(argv); // parse command line arguments
        
    }
    
    public void run() {
        try {
            String received; // string to save last received message
            ServerSocket socket; // main communication socket
            // tries to bind to the given address and port
            try {
                socket = new ServerSocket(port, 0, address);
            } catch (IOException e) {
                System.out.println("Unable to bind to " + address.getHostAddress() + ":" + port + ".");
                System.out.println(e.getMessage());
                System.exit(1);
                return; // needed for exiting program if socket threw exception
            }
            
            
            System.out.println("Server is listening on " + address.getHostAddress() + ":" + port + ".");
            System.out.println("Host address: " + InetAddress.getLocalHost());
            
            // this loop waits for (more) clients to connect when Server has no client (any longer)
            while (true) {
                
                Socket connectionSocket = socket.accept(); // wait for client connection
                BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); // create a stream reader for socket
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // create a stream writer for socket
                
                System.out.println("Client connected.");
                
                // runs as long as client is connected to the Server
                while (true) {
                    
                    // reads input from client and displays it
                    received = inFromClient.readLine();
                    System.out.println("Received: " + received);
                    
                    
                    if (received == null){
                        System.out.println("Connection lost. Socket will be closed.");
                        break;
                    }
                    String response = ""; // empty string to build response in
                    
                    response = parseRequest(outToClient, received);
                    
                    if (response != "") {
                        
                        try {
                            
                            // displays the appropriate response to the result (without parsing needed)
                            outToClient.writeBytes(response + "\n"); // writes bytes because the client needs the new line character for its readline method.
                            
                        } catch (IOException e) {
                            System.out.println("Client disconnected!");
                            break;
                        }
                    }
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }
    
    private static String parseRequest(DataOutputStream toClient,String request) {
        String response;
        if (request == null) {
            //The socket read null. The socket broke
            System.out.println("Invalid Request");
            // TODO: Handle error. Send to client
        }
        String[] data;
        //Splits the response intro responseCode and payload
        data = request.split(";");
        switch (data[0]) {
            // The client wants to get the number in data[1] calculated
            case "get":
                try {
                    int input = Integer.parseInt(data[1], 10);
                    
                    if (input < 0) {
                        response = "402;too low";
                    } else if (input > 91) {
                        response = "501;too large";
                    } else {
                        try {
                            toClient.writeBytes("100;Continue\n");
                        } catch (IOException e) {
                            System.out.println("Client disconnected!");
                            response = "";
                            return "";
                        }
                        
                        long result = FibonacciCalc.calculate(input);
                        // outputs calculated result & verifies output
                        System.out.println("Calculated: " + result);
                        response = "200;" + String.valueOf(result);
                    }
                } catch (NumberFormatException e) {
                    response = "401;NaN";
                }
            break;
            case "ok":
                System.out.println("Message was received by client.");
                response = "";
            break;
            default:
                return "The client Request was invalid.";
        }
        return response;
    }
    
    // parses the command line arguments
    private static void parseArguments(String argv[]) {
        for (int i = 0; i < argv.length; i++) {
            switch (argv[i]) {
                // argument to set the port
                case "-p":
                i++;
                // check if a next argument exists
                if (i == argv.length) {
                    System.out.println("No port provided.");
                } else {
                    // try to parse the argument as a number
                    try {
                        int number = Integer.parseInt(argv[i]);
                        // check if the number is a valid port
                        if (number >= 0 && number <= 65535) {
                            port = number;
                        } else {
                            System.out.println("Invalid port number. (" + number + ")");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number.");
                    }
                }
                break;
                //argument to set the address to listen on
                case "-a":
                i++;
                // check if a next argument exists
                if (i == argv.length) {
                    System.out.println("No address provided.");
                } else {
                    try {
                        address = InetAddress.getByName(argv[i]);
                    } catch (UnknownHostException e) {
                        System.out.println("Invalid address. (" + argv[i] + ")");
                    }
                }
                break;
                // display help info
                case "-h":
                System.out.println("FibonacciServer");
                System.out.println("  -h            displays this help");
                System.out.println("  -p [port]     sets the port to run the server on");
                System.out.println("  -a [address]  sets the address to run the server on");
                // exit
                System.exit(0);
                break;
                default:
                System.out.println("Unknown command line argument '" + argv[i] + "'");
            }
        }
    }
}
