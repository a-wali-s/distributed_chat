package application;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Runnable {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	private String connPort = "";
	Message inBuffer = null;
	
	public Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		this.connPort = Integer.toString(socket.getPort());
	}
	
	public void run(){
		try {
			while(true) {
				ChatController.getInstance().receiveDebugMessage("Wait for object");
				inBuffer = (Message)in.readObject();
				receiveMessage(inBuffer);
			}
		}
		catch(IOException e){
			System.out.println("Oh noes I got disconnected, what to do??!");
			
			//TODO -- HANDLE DISCONNECT BY ATTEMPTING TO CONNECT TO SOMEONE IN FOF LIST
			//e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Sends Message Object through this particular socket connection.
	 * This should only be called if the connection is alive.
	 */
	public int sendSystemMessage(Message msg, Integer messageCode) {
		try {
			out.writeObject(msg);
			return 0;
		}
		catch (IOException e) {
			return -1;
		}
	}
	
	
	/*
	 * Sends Message Object through this particular socket connection.
	 * This should only be called if the connection is alive.
	 */
	public int sendMessage(Message msg) {
		try {
			out.writeObject(msg);
			return 0;
		}
		catch (IOException e) {
			return -1;
		}
	}
	
	public void receiveMessage(Message msg) {
		ClientInterface.getInstance().receiveMessage(msg, this);
	}
	
	/*
	 * Returns connection info in a Friend format ("[ip]/[port]/[priority]")
	 */
	public String toFriendString() {
		
		//TODO -- UPDATE PRIORITY, SOMEHOW..
		
		String ip = socket.getInetAddress().getHostAddress();
		String port = this.connPort;
		String priority = "1";
		return (ip+"/"+port+"/"+priority);		
	}
	
	/*
	 * Update the locally saved port number for this connection
	 * (Port number for this socket is not necessarily same as node's listening port depending on point of initiation)
	 */
	public void updatePort(String port) {
		this.connPort = port;
	}
}
