package Client;

import java.io.*;
import java.net.*;

public class Client {

	/**
	 * Client code. Takes server IP and Port as arguments
	 * 
	 * @param args First argument is IP, second is port
	 */
	public static void main(String[] args) throws IOException {
		if(args.length != 2){
			System.out.println("Usage: Client [Server Name] [Server Port]");
			System.exit(-3);
		}
		
        Socket chatSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
 
        try {
            chatSocket = new Socket(args[0],Integer.parseInt(args[1]));
            out = new PrintWriter(chatSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.printf("Don't know about host: %s.\n", args[0]);
            System.exit(-1);
        } catch (IOException e) {
            System.err.printf("Couldn't get I/O for the connection to: %s.\n", args[0]);
            System.exit(-2);
        }
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
 
        
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            if (fromServer.equals("Bye."))
                break;
             
            fromUser = stdIn.readLine();
	        if (fromUser != null) {
	            System.out.println("Client: " + fromUser);
	            out.println(fromUser);
	        }
        }
 
        out.close();
        in.close();
        stdIn.close();
        chatSocket.close();
	}

}
