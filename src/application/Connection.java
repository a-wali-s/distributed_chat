package application;

import java.io.*;
import java.net.Socket;

public class Connection {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	String inBuffer = null;
	
	public Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public int sendMessage(String msg) {
		try {
			out.writeObject(msg);
			return 0;
		}
		catch (IOException e) {
			return -1;
		}
	}
	
	public String receiveMessage() {
		try {
			inBuffer = in.readObject().toString();
			return String.copyValueOf(inBuffer.toCharArray());
		}
		catch(IOException e){
			return "-1";
		}
		catch(ClassNotFoundException e){
			return "String not received";
		}
	}
}
