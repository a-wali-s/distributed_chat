package application;


import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
public class ConnectionListener implements Runnable{
	
	ServerSocket providerSocket;
	Socket connection = null;
	String message;
	int port;
	int nextChildNumber = 0;
	boolean run = true;
	private static int MAX_CONNECTIONS = 2;
	public ConnectionListener(int port){
		this.port = port;
	}
	public ConnectionListener(int port, int maxConnections){
		this.port = port;
		this.MAX_CONNECTIONS = maxConnections;
	}
	
	public static int getMaxConnections(){
		return MAX_CONNECTIONS;
	}
	
	public void stop(){
		run = false;
		try {
			providerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				Integer numberOfConnections = ClientInterface.getInstance().connections.size();
				System.out.println(ClientInterface.getInstance().username + ": the number of connections i see is " + numberOfConnections);
				if(numberOfConnections >= MAX_CONNECTIONS) {
					ClientInterface.getInstance().addConnection(connWrapper);
					ClientInterface.getInstance().redirectConnection(connWrapper);
				}
				else {
					connWrapper.isParent = true;
					connWrapper.childNumber = nextChildNumber;
					nextChildNumber++;
					ClientInterface.getInstance().acceptConnection(connWrapper);
				}
			}
		}
		catch(BindException bindException){
			ChatController.getInstance().error(bindException.getMessage());
		}catch(SocketException socketException){
			ChatController.getInstance().error("Socket Closed");
			
		}catch(IOException ioException){
			ChatController.getInstance().error(ioException.getMessage());
		}
		finally{
			//4: Closing connection
				System.out.println("Socket closed");

		}
	}
}

