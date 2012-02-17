package application;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class ConnectionListener implements Runnable{
	
	ServerSocket providerSocket;
	Socket connection = null;
//	ObjectOutputStream out;
//	ObjectInputStream in;
	String message;
	ClientInterface client;
	public ConnectionListener(ClientInterface client){
		this.client = client;
	}
	
	public void run()
	{
		try{
			while(true) {
				//1. creating a server socket
				providerSocket = new ServerSocket(2004, 10);
				//2. Wait for connection
				System.out.println("Waiting for connection");
				connection = providerSocket.accept();
				System.out.println("Connection received from " + connection.getInetAddress().getHostName());
				//3. get Input and Output streams
				Connection connWrapper = new Connection(connection, client);
				Thread connThread = new Thread(connWrapper);
				connThread.start();
				client.addConnection(connWrapper);
//			sendMessage("Connection successful");
			//4. The two parts communicate via the input and output streams
//			do{
//				try{
//					message = (String)in.readObject();
//					System.out.println("client>" + message);
//					if (message.equals("bye"))
//						sendMessage("bye");
//					else{
//						MessageHandler.getInstance().reseiveMsg(message);
//						sendMessage(message);
//					}
//				}
//				catch(ClassNotFoundException classnot){
//					System.err.println("Data received in unknown format");
//				}
//			}while(!message.equals("bye"));
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
//				in.close();
//				out.close();
				providerSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
//	void sendMessage(String msg)
//	{
//		try{
//			out.writeObject(msg);
//			out.flush();
//			System.out.println("server>" + msg);
//		}
//		catch(IOException ioException){
//			ioException.printStackTrace();
//		}
//	}
//	public static void main(String args[])
//	{
//		Server srvr = new Server();
//		while(true){
//			srvr.run();
//		}
//	}
}

