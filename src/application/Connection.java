package application;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	Message inBuffer = null;
	ClientInterface client = null;
	
	public Connection(Socket socket, ClientInterface client) throws IOException{
		this.socket = socket;
		this.client = client;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public void run(){
		try {
			while(true) {
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
		client.receiveMessage(msg);
	}
}
