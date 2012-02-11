package Client;
//
//import java.io.*;
//import java.net.*;
//
//public class Client {
//
//	/**
//	 * Client code. Takes server IP and Port as arguments
//	 * 
//	 * @param args First argument is IP, second is port
//	 */
//	public static void main(String[] args) throws IOException {
//		if(args.length != 2){
//			System.out.println("Usage: Client [Server Name] [Server Port]");
//			System.exit(-3);
//		}
//		
//        Socket chatSocket = null;
//        PrintWriter out = null;
//        BufferedReader in = null;
// 
//        try {
//            chatSocket = new Socket(args[0],Integer.parseInt(args[1]));
//            out = new PrintWriter(chatSocket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
//        } catch (UnknownHostException e) {
//            System.err.printf("Don't know about host: %s.\n", args[0]);
//            System.exit(-1);
//        } catch (IOException e) {
//            System.err.printf("Couldn't get I/O for the connection to: %s.\n", args[0]);
//            System.exit(-2);
//        }
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//        String fromServer;
//        String fromUser;
// 
//        
//        while ((fromServer = in.readLine()) != null) {
//            System.out.println("Server: " + fromServer);
//            if (fromServer.equals("Bye."))
//                break;
//             
//            fromUser = stdIn.readLine();
//	        if (fromUser != null) {
//	            System.out.println("Client: " + fromUser);
//	            out.println(fromUser);
//	        }
//        }
// 
//        out.close();
//        in.close();
//        stdIn.close();
//        chatSocket.close();
//	}
//
//}

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
