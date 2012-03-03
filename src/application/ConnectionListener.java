package application;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ConnectionListener implements Runnable{
	
	ServerSocket providerSocket;
	Socket connection = null;
	String message;
	int port;
	boolean run = true;
	public ConnectionListener(int port){
		this.port = port;
	}
	
	public void stop(){
		run = false;
	}
	
	public void run()
	{
		try{
			//1. creating a server socket
			providerSocket = new ServerSocket(port, 10);
			while(run) {
				//2. Wait for connection
				ChatController.getInstance().receiveDebugMessage("Waiting for connection");
				connection = providerSocket.accept();
				ChatController.getInstance().receiveDebugMessage("Connection received from " + connection.getInetAddress().getHostName() 
						+ ":" + connection.getPort(), Message.MESSAGE_CODE_CONNECTION_ACK);
				//3. Wrap in a connection object, spawn a thread, and go back to listening
				Connection connWrapper = new Connection(connection);
				ClientInterface.getInstance().addConnection(connWrapper);
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

