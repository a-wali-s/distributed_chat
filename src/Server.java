

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Server implements Runnable{
	
	ServerSocket providerSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	public Server(){}
	
	public void run()
	{
		try {
			//1. creating a server socket
			providerSocket = new ServerSocket(2004, 10);
			//2. Wait for connection
			System.out.println("Waiting for connection");
			
			ServerThread newConnection = new ServerThread(providerSocket.accept(), providerSocket);
			new Thread(newConnection).start();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
			
	public static void main(String args[])
	{
		Server srvr = new Server();
		while(true){
			srvr.run();
		}
	}
}

