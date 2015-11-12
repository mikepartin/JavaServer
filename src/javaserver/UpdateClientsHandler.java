/*
 * This connection handler is the thread that provides a response to a request 
 * for a list of all the users currently on the server.
 */
package javaserver;

import java.io.IOException;

/**
 *
 * @author Michael
 */
public class UpdateClientsHandler implements Runnable {
    private final Client client;
    
    public UpdateClientsHandler(Client client) throws IOException {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String output = "#USERUPD8#";
            //create output string that is a list of all current users
            for (int i=0; i<JavaServer.users.size()-1; i++) {
                output = output + JavaServer.users.get(i);
                output = output + ";";
            }
            //add last one without ';' at the end
            if (JavaServer.users.size() > 0)
                output = output + JavaServer.users.get(JavaServer.users.size()-1);
            //send the requested infomation to the client
            client.sendCommand(output);
            client.disconnect();
        } catch(Exception e) {
            System.out.println("Listener Client");
            System.out.println(e);
        }
    }
}
