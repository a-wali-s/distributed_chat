package Server;

import java.net.*;
import java.io.*;

public class Server {    
	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(args[1]));
			System.out.printf("Listening on port: %s\n", args[1]);
		} 
		catch (IOException e) {
			System.out.printf("Could not listen on port: %s", args[1]);
			System.exit(-1);
		}
	}
}
