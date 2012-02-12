package application;


import java.net.*;
import java.io.*;
import java.util.*;

public class MultiSocketServer implements Runnable {

	private Socket connection = null;
	private String TimeStamp;
	private int ID;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	int count = 0;
	private final static int PORT = 2004;
	
	public static void main(String[] args) {
		
		
			
	}
	  
	MultiSocketServer(Socket s, int i) {
		this.connection = s;
		this.ID = i;
		
	}
	MultiSocketServer(){
		
	}

	public void run() {
		try {
			try{
				ServerSocket socket1 = new ServerSocket(PORT);
				System.out.println("MultipleSocketServer Initialized");
				while (true) {
					Socket connection = socket1.accept();
					Runnable runnable = new MultiSocketServer(connection, ++count);
					Thread thread = new Thread(runnable);
					thread.start();
				}
		    }
		    catch (Exception e) {}
			
			
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			//3. get Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("Connection successful");
			//4. The two parts communicate via the input and output streams
			do{
				try{
					message = (String)in.readObject();
					MessageHandler.getInstance().reseiveMsg(message);
					System.out.println("client>" + message);
					if (message.equals("bye"))
						sendMessage("bye");
					else{
						message = "Server receipted message: " + message;
						sendMessage(message);
					}
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
			}while(!message.equals("bye"));
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				connection.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
}