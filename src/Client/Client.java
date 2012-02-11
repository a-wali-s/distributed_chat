package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	BufferedReader usrIn;
 	String message;
	Client(){}
	void run()
	{
		try{
			//1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 2004);
			System.out.println("Connected to localhost in port 2004");
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			usrIn = new BufferedReader(new InputStreamReader(System.in));
			//3: Communicating with the server
			do{
				try{
					message = (String)in.readObject();
					System.out.println("server>" + message);
					message = usrIn.readLine();
					sendMessage(message);
				}
				catch(ClassNotFoundException classNot){
					System.err.println("data received in unknown format");
				}
			}while(!message.equals("bye"));
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
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
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Client cli = new Client();
		cli.run();
	}
}
