package Server;

import java.net.*;
import java.io.*;

public class Server {    
	/**
	 * Takes port as argument. Opens a connection if port is valid
	 * 
	 * @param args Port number to listen on
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0){
			System.out.printf("Usage: Server [port]");
			System.exit(-2);
		}
		
        ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			System.out.printf("Listening on port: %s\n", args[0]);
		}
		catch (IOException e) {
			System.out.printf("Could not listen on port: %s", args[0]);
			System.exit(-1);
		}
		Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.append("test");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                clientSocket.getInputStream()));
        String inputLine, outputLine;
        while ((inputLine = in.readLine()) != null) {
        	System.out.println(inputLine);
        }
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
	}
}
