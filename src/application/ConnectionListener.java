package application;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ConnectionListener implements Runnable{
	
	ServerSocket providerSocket;
	Socket connection = null;
	String message;
	boolean run = true;
	ClientInterface client;
	public ConnectionListener(ClientInterface client){
		this.client = client;
	}
	
	public void stop(){
		run = false;
	}
	
	public void run()
	{
		try{
			while(run) {
				//1. creating a server socket
				providerSocket = new ServerSocket(2004, 10);
				//2. Wait for connection
				System.out.println("Waiting for connection");
				connection = providerSocket.accept();
				System.out.println("Connection received from " + connection.getInetAddress().getHostName());
				//3. Wrap in a connection object, spawn a thread, and go back to listening
				Connection connWrapper = new Connection(connection, client);
				Thread connThread = new Thread(connWrapper);
				connThread.start();
				client.addConnection(connWrapper);
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				providerSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
}

