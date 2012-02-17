package application;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ListIterator;
public class ClientInterface implements Runnable{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	BufferedReader usrIn;
 	ArrayList<Connection> connections;
 	String message;
	public ClientInterface(){
		connections = new ArrayList<Connection>();
	}
	public void run()
	{
//		try{
//			//1. creating a socket to connect to the server
//			requestSocket = new Socket("localhost", 2004);
//			System.out.println("Connected to localhost in port 2004");
//			//2. get Input and Output streams
//			out = new ObjectOutputStream(requestSocket.getOutputStream());
//			out.flush();
//			in = new ObjectInputStream(requestSocket.getInputStream());
//			usrIn = new BufferedReader(new InputStreamReader(System.in));
//			//3: Communicating with the server
//			do{
//				try{
//					message = (String)in.readObject();
//					System.out.println("server>" + message);
//					message = usrIn.readLine();
//					sendMessage(message);
//				}
//				catch(ClassNotFoundException classNot){
//					System.err.println("data received in unknown format");
//				}
//			}while(!message.equals("bye"));
//		}
//		catch(UnknownHostException unknownHost){
//			System.err.println("You are trying to connect to an unknown host!");
//		}
//		catch(IOException ioException){
//			ioException.printStackTrace();
//		}
//		finally{
//			//4: Closing connection
//			try{
//				in.close();
//				out.close();
//				requestSocket.close();
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}
	}
	void addConnection(Connection conn){
		connections.add(conn);
	}
	void sendMessage(String msg)
	{
		if (connections.size() > 0){
			for(int x=0;x<connections.size();x++){
				connections.get(x).sendMessage(msg);
			}
		}
	}
	void receiveMessage(String msg)
	{
		//TODO: Send message to messageAPI
	}
//	public static void main(String args[])
//	{
//		Client cli = new Client();
//		cli.run();
//	}
}
