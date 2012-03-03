package application;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
public class ClientInterface{
	private static ClientInterface instance;
 	List<Connection> connections;
 	String message;	
 	String username = "";
	private Integer nodeDepth = 1;

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
	 * Also sends it's nodeDepth to the newly connected peer, which should increment it and set it's own node depth
	 */
	void addConnection(Connection conn){
		Thread connThread = new Thread(conn);
		connThread.start();
		connections.add(conn);
		conn.sendMessage(new Message(conn.socket.getRemoteSocketAddress() + " -- " + conn.socket.getLocalAddress(),null));
		System.out.println(getNodeDepth().toString());
		conn.sendMessage(new Message(getNodeDepth().toString(),null, 101));
	}

	
	void createConnection(String hostname, int port){
		try {
			Socket newConn = new Socket(hostname, port);
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
	 * Propagates the message to all connected peers except one, which is the one that sent it
	 */
	void forwardMessage(Message msg, Connection conn)
	{
		for(int x=0;x<connections.size();x++){
			if(connections.get(x) != conn) // if it isn't from the connection that sent the message
				connections.get(x).sendMessage(msg);
		}
		//receiveMessage(msg);
	}

	/*
	 * Broadcasts message to all peers that are directly connected to this host
	 * (Message propagation to the rest of the network will be responsibility of the peers)
	 */
	void sendMessage(Message msg)
	{
		for(int x=0;x<connections.size();x++){
			connections.get(x).sendMessage(msg);
		}
		ChatController.getInstance().receiveMsg(msg);
	}
	
	/*
	 * API for sendMessage(Message msg)
	 * This is called by the UI layer to broadcast a newly generated user message
	 */
	public void sendMessage(String msg){
		sendMessage(new Message(msg, username));
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	
	/*
	 * A new message has been received from one of the connections.
	 * Check for the message originator.  If null, then this is a system message - do not send to UI
	 */
	void receiveMessage(Message msg, Connection conn)
	{
		if( msg.getUsername() != null )
		{
			ChatController.getInstance().receiveMsg(msg);
			forwardMessage(msg, conn);
		}
		else if(msg.getMsgText().contains("--") && msg.getUsername() == null)
		{
			System.out.println("Connection request accepted!");
			DebugGraph.getInstance().addEdge(msg);
		}
		else if(msg.getMessageCode() == 101)
		{
			setNodeDepth(Integer.parseInt(msg.getMsgText())+1);
			msg.setMessageCode(getNodeDepth());
			forwardMessage(msg, conn);
			System.out.println("after connection, set nodeDepth to " + (Integer.parseInt(msg.getMsgText())));
		}
		else
			System.out.println("Unknown system message received.");
			
	}
	private Integer getNodeDepth() {
		return nodeDepth;
	}

	private void setNodeDepth(Integer nodeDepth) {
		this.nodeDepth = nodeDepth;
	}
}
