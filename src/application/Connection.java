package application;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	private String connPort = "";
	private String username = "";
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
				//ChatController.getInstance().receiveDebugMessage("Wait for object");
				inBuffer = (Message)in.readObject();
				receiveMessage(inBuffer);
			}
		}
		catch(IOException e){
			//System.out.println("My Listening port: " + ChatController.getInstance().getListenerPort() + "  |  This connection port: " + this.socket.getLocalPort());
			ClientInterface.getInstance().sendMessage(new Message("",this.username, Message.MESSAGE_CODE_USER_DISCONNECT));
			// Only a client should bother trying to reconnect.  A node acting on server-side of a disconnect does not need to do anything
			if( this.socket.getLocalPort() != ChatController.getInstance().getListenerPort() ){
				attemptRecovery();
			}
			else{
				System.out.println("Removing self from connections");
				ClientInterface.getInstance().connections.remove(this);
			}
			//TODO -- HANDLE DISCONNECT BY ATTEMPTING TO CONNECT TO SOMEONE IN FOF LIST
			//e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	private void attemptRecovery() {
		// TODO Auto-generated method stub
		System.out.println("I will attempt recovery some day");
		ClientInterface ci = ClientInterface.getInstance();
		if( ci.tryRecovery() ) {
			System.out.println("Recovery successful!, breaking old connection");
			ci.connections.remove(this);
		}
		else {
			ChatController.getInstance().receiveDebugMessage("Error: Chat disconnected - Failed to reconnect");
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
		System.out.println("NEW PORT: " + port);
		this.connPort = port;
	}
	
	/*
	 * Update the locally saved Username for this connection
	 */
	public void updateUsername(String usr) {
		System.out.println("NEW Username: " + usr);
		this.username = usr;
	}
}
