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
	
	public MultiSocketServer() {
		int port = 2004;
		int count = 0;
			try{
				ServerSocket socket1 = new ServerSocket(port);
				System.out.println("MultipleSocketServer Initialized");
				while (true) {
					Socket connection = socket1.accept();
					Runnable runnable = new MultiSocketServer(connection, ++count);
					Thread thread = new Thread(runnable);
					thread.start();
				}
		    }
		    catch (Exception e) {}
	}
	  
	MultiSocketServer(Socket s, int i) throws IOException {
		this.connection = s;
		User newUser = new User(s);
		main.users.add(newUser);
		this.ID = i;
	}

	public void run() {
		try {
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			//3. get Input and Output streams
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("Connection successful");
			//4. The two parts communicate via the input and output streams
			do{
				try{
					message = (String)in.readObject();
					System.out.println("client>" + message);
					if (message.equals("bye"))
						sendMessage("bye");
					else{
						message = "Server receipted message: " + message;
						System.out.println("client count = " + main.users.size());
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
			for(int i = 0;i < main.users.size();i++)
			{
				main.users.get(i).sendMessageToUser(msg + i);
			}
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
}