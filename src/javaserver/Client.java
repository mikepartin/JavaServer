/*
 * Class that deals with client connection needs such as initializing input and
 * outputs, connecting and disconnecting client, and processing inputs and
 * sending outputs. The objects created with this class can be thought of as
 * the client.
 */
package javaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client Connection Class
 * Version 1.0
 * @author Michael
 */
public class Client {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean keepAlive = true;
    public boolean isListener = false;
    public boolean isClientUpdater = false;
    
    
    public Client (Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }
      
    public void connect() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    
    public void disconnect() throws IOException {
        clientSocket.close();
    }
    
    public void recieveCommand() throws IOException {
        String cmd = in.readLine();
        System.out.println("Command In: " + cmd);
        processCommand(cmd);
    }
    
    public void sendCommand(String output) {
        out.write(output);
        out.close();
    }
    
    private void processCommand(String cmd) {
        String data;
        switch (cmd.substring(0, 10)) {
            case "#SHUTDOWN#":
                keepAlive = false;
                break;
            case "#LISTENER#":
                try {
                    //connection is determined to be listening for
                    //changes on the server create new thread for this
                    Thread aThread = new Thread(new ListenerConnectionHandler(this));
                    aThread.start();
                } catch (Exception e) {
                }
                break;
            case "#USERCONN#":
                data = cmd.substring(10);
                JavaServer.users.add(data);
                try {
                    //connection is determined to be new connection
                    //create new thread to send client a list of all the 
                    //users on server
                    Thread aThread = new Thread(new UpdateClientsHandler(this));
                    aThread.start();
                    //Activate trigger
                    JavaServer.listenerResponseCode = 1;
                    synchronized(JavaServer.lastEntryTrigger) {
                        JavaServer.lastEntryTrigger.notifyAll();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case "#USERDISS#":
                //remove the user
                data = cmd.substring(10);
                JavaServer.users.remove(data);
                try {
                    //Activate trigger
                    JavaServer.listenerResponseCode = 1;
                    synchronized(JavaServer.lastEntryTrigger) {
                        JavaServer.lastEntryTrigger.notifyAll();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case "#CHATSEND#":
                try {
                    //connection is determined to be a message
                    //set message on lastEntry and trigger
                    data = cmd.substring(10);
                    JavaServer.lastEntry = data;
                    JavaServer.listenerResponseCode = 0;
                    synchronized(JavaServer.lastEntryTrigger) {
                        JavaServer.lastEntryTrigger.notifyAll();
                    }
                    this.sendCommand("#RECEIVED#");
                    this.disconnect();
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }
    
    public boolean isAlive() {
        return keepAlive;
    }
}
