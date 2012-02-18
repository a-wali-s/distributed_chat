package application;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	Message inBuffer = null;
	
	public Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public void run(){
		try {
			while(true) {
				System.out.println("Wait for object");
				inBuffer = (Message)in.readObject();
				receiveMessage(inBuffer);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
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
		ClientInterface.getInstance().receiveMessage(msg);
	}
}
