package application;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
public class ClientInterface{
	private static ClientInterface instance;
 	ArrayList<Connection> connections;
 	String message;
 	
	public static ClientInterface getInstance(){
		if(instance == null){
			instance = new ClientInterface();
		}
		return instance;
	}
	
	public ClientInterface(){
		connections = new ArrayList<Connection>();
	}
	void addConnection(Connection conn){
		connections.add(conn);
	}
	void createConnection(String hostname){
		try {
			Socket newConn = new Socket(hostname, 2004);
			addConnection(new Connection(newConn));
		}
		catch(UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	void sendMessage(Message msg)
	{
		if (connections.size() > 0){
			for(int x=0;x<connections.size();x++){
				connections.get(x).sendMessage(msg);
			}
		}
	}
	void receiveMessage(Message msg)
	{
		MessageHandler.getInstance().receiveMsg(msg);
	}
}
