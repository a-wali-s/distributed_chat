import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread implements Runnable {

	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	
	ServerThread(Socket connection, ServerSocket providerSocket){
		this.providerSocket = providerSocket;
		this.connection = connection;
	}
	
	@Override
	public void run() {
	try {
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

}