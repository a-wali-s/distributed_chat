package Server;
//
//import java.net.*;
//import java.io.*;
//
//public class Server {    
//	/**
//	 * Takes port as argument. Opens a connection if port is valid
//	 * 
//	 * @param args Port number to listen on
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException {
//		if (args.length == 0){
//			System.out.printf("Usage: Server [port]");
//			System.exit(-2);
//		}
//		
//        ServerSocket serverSocket = null;
//		try {
//			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
//			System.out.printf("Listening on port: %s\n", args[0]);
//		}
//		catch (IOException e) {
//			System.out.printf("Could not listen on port: %s", args[0]);
//			System.exit(-1);
//		}
//		Socket clientSocket = null;
//        try {
//            clientSocket = serverSocket.accept();
//        } catch (IOException e) {
//            System.err.println("Accept failed.");
//            System.exit(1);
//        }
//        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//        out.append("test");
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(
//                clientSocket.getInputStream()));
//        String inputLine, outputLine;
//        while ((inputLine = in.readLine()) != null) {
//        	System.out.println(inputLine);
//        }
//        in.close();
//        out.close();
//        clientSocket.close();
//        serverSocket.close();
//	}
//}

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Server{
	
	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	Server(){}
	
	void run()
	{
		try{
			//1. creating a server socket
			providerSocket = new ServerSocket(2004, 10);
			//2. Wait for connection
			System.out.println("Waiting for connection");
			connection = providerSocket.accept();
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
				providerSocket.close();
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
	public static void main(String args[])
	{
		Server srvr = new Server();
		while(true){
			srvr.run();
		}
	}
}

