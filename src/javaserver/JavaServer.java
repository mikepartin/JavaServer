/*
 * Server - Listen for incoming connections and create threads to process those
 * connections accordingly.
 */

package javaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Java Server
 * Version 1.0
 * @author Michael Partin
 */
public class JavaServer {

    // variables used to keep track of clients and server status
    //protected static Security security = new Security(1024);
    protected static ArrayList<String> users = new ArrayList<>(); //List of users connected to server
    protected static final Object lastEntryTrigger = new Object(); //Object used for triggering
    protected static String lastEntry = "";
    protected static int listenerResponseCode = 0; //0=ChatText needs updated, 1=UserList needs updated
    protected static boolean isRunning = true;
    
    private static final int PORT = 8085;
    
    public static void main(String[] args) {
        try {
            //security.generateKeyPair();
            //security.setEncryptPublicKey(security.myPublicKey); //for testing
            ServerSocket serverSocket;
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started!");
            // main loop that listens for new connections
            while (isRunning) {
                Socket clientSocket;
                // start new thread to handle connection
                clientSocket = serverSocket.accept();
                Thread aThread = new Thread(new GenConnectionHandler(clientSocket));
                aThread.start(); 
            }
        } catch (IOException ex) {
            System.out.println("MAIN");
            System.out.println(ex);
        }
        
    }
    
}
