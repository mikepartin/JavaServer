/*
 * First thread to handle the connection of a client to this server. 
 */
package javaserver;

import java.io.IOException;
import java.net.Socket;

/**
 * Connection Class
 * Version 1.0
 * @author Michael
 */
public class GenConnectionHandler implements Runnable {

    private final Client client;

    public GenConnectionHandler(Socket clientSocket) throws IOException {
        this.client = new Client(clientSocket);
    }
    
    @Override
    public void run() {
        // check to see if server is still running and if not try to disconnect
        if (!JavaServer.isRunning) {
            try {
                client.disconnect();
            } catch (IOException ex) {
            }
            return;
        }
        try {
            client.connect();
            client.recieveCommand();
            JavaServer.isRunning = client.isAlive();
        } catch(Exception e) {
            System.out.print("***Connection Error: " + e + "\n");
        }
    }
    
}
