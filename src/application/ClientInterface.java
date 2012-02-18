package application;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
public class ClientInterface{
	private static ClientInterface instance;
 	ArrayList<Connection> connections;
 	String message;	
 	// FIXME: should have some sort of authentication system for userid
 	String userid = "";

	public static ClientInterface getInstance(){
		if(instance == null){
			instance = new ClientInterface();
		}
		return instance;
	}

	public ClientInterface(){
		connections = new ArrayList<Connection>();
	}
	
	/*
	 * Adds new connection to list of peers (that are directly connected to this host)
	 * Sends a connection acknowledgment message to the newly connected peer.
	 */
	void addConnection(Connection conn){
		connections.add(conn);
		conn.sendMessage(new Message("ACK:connection",null));
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

	/*
	 * Broadcasts message to all peers that are directly connected to this host
	 * (Message propagation to the rest of the network will be responsibility of the peers)
	 */
	void sendMessage(Message msg)
	{
		if (connections.size() > 0){
			for(int x=0;x<connections.size();x++){
				connections.get(x).sendMessage(msg);
			}
		}
	}
	
	/*
	 * API for sendMessage(Message msg)
	 * This is called by the UI layer to broadcast a newly generated user message
	 */
	public void sendMessage(String msg){
		sendMessage(new Message(msg, userid));
	}
	
	/*
	 * A new message has been received from one of the connections.
	 * Check for the message originator.  If null, then this is a system message - do not send to UI
	 */
	void receiveMessage(Message msg)
	{
		if( msg.getUsername() != null )
			MessageAPI.getInstance().receiveMsg(msg);
		else if(msg.getMsgText().equals("ACK:connection"))
			System.out.println("Connection request accepted!");
		else
			System.out.println("Unknown system message received.");
			
	}
}
