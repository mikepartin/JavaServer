/*
 * Class that will be the thread for handling the client if the client has been
 * determined to be a listener client.
 */
package javaserver;

import java.io.IOException;

/**
 * Listener Client Class
 * Version 1.0
 * @author Michael
 */
public class ListenerConnectionHandler implements Runnable {

    private final Client client;
    
    public ListenerConnectionHandler(Client client) throws IOException {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //wait for data trigger
            synchronized(JavaServer.lastEntryTrigger){
                JavaServer.lastEntryTrigger.wait();
            }
            // Send chat data
            switch (JavaServer.listenerResponseCode) {
                case 0:
                    //client.sendCommand("#CHATTEXT#" + JavaServer.security.encryptedToString(JavaServer.security.encrypt(JavaServer.lastEntry)));
                    client.sendCommand("#CHATTEXT#" + JavaServer.lastEntry);
                    client.disconnect();
                    break;
                case 1:
                    Thread aThread = new Thread(new UpdateClientsHandler(client));
                    aThread.start(); 
                    break;
                default:
                    break;
            }
        } catch(Exception e) {
            System.out.println("Listener Client");
            System.out.println(e);
        }
    }
    
}
