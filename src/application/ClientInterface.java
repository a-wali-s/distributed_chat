package application;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class ClientInterface{
	private static ClientInterface instance;
 	List<Connection> connections;
 	List<Friend> friends;
 	String message;	
 	String username = "";
	private Integer nodeDepth = 1;

	public static ClientInterface getInstance(){
		if(instance == null){
			instance = new ClientInterface();
		}
		return instance;
	}

	private ClientInterface(){
		connections = new ArrayList<Connection>();
		friends = new ArrayList<Friend>();
	}
	
	/*
	 * Adds a new connection and corresponding thread for both accepting and creating connections.
	 * This function should generally not be touched unless current functionality drastically changes.
	 */
	void addConnection(Connection conn){
		Thread connThread = new Thread(conn);
		connThread.start();
		connections.add(conn);
		
	}
	
	/*
	 * Adds new connection to list of peers (that are directly connected to this host)
	 * Sends a connection acknowledgment message to the newly connected peer.
	 * Also sends it's nodeDepth to the newly connected peer, which should increment it and set it's own node depth
	 */
	void acceptConnection(Connection conn){
		addConnection(conn);
		conn.sendMessage(new Message("ACK:connection", username, Message.MESSAGE_CODE_CONNECTION_ACK));
		ChatController.getInstance().receiveDebugMessage("NodeDepth " + getNodeDepth().toString());
		conn.sendMessage(new Message(getNodeDepth().toString(),username, Message.MESSAGE_CODE_NODE_DEPTH_UPDATE));
		
		System.out.println("I'm going to send this: \n" + generateFriendsString());
		// TODO: SEND FoF UPDATE
	}

	
	void createConnection(String hostname, int port){
		try {
			if(connections.isEmpty()){
				Socket newSock = new Socket(hostname, port);
				addConnection(new Connection(newSock));
			}
			

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
		sendMessage(new Message(msg, username, Message.MESSAGE_CODE_REGULAR_MESSAGE));
	}
	
	public void setUsername(String username) {
		this.username = username;
		if(DistributedChat.DEBUG){
			DebugGraph.createFile(username);
		}
	}

	
	/*
	 * A new message has been received from one of the connections.
	 * Check for the message originator.  If null, then this is a system message - do not send to UI
	 */
	void receiveMessage(Message msg, Connection conn)
	{
		if( msg.getMessageCode() == Message.MESSAGE_CODE_REGULAR_MESSAGE )
		{
			ChatController.getInstance().receiveMsg(msg);
			forwardMessage(msg, conn);
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_CONNECTION_ACK){
			ChatController.getInstance().receiveMsg(msg);
			if(DistributedChat.DEBUG){
				sendMessage(new Message(conn.socket.getInetAddress().getHostAddress() + ":" + msg.getUsername() + " -- " + 
						conn.socket.getLocalAddress() + ":" + username
						,username, Message.MESSAGE_CODE_CONNECTION_RELATIONSHIP));
			}
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_CONNECTION_RELATIONSHIP)
		{
			DebugGraph.addEdge(msg, this.username);
			forwardMessage(msg, conn);
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_NODE_DEPTH_UPDATE)
		{
			Integer newNodeDepth = Integer.parseInt(msg.getMsgText())+1;
			setNodeDepth(newNodeDepth);
			ChatController.getInstance().receiveDebugMessage("after connection, set nodeDepth to " + newNodeDepth);
			msg.setMsgText(newNodeDepth.toString());
			forwardMessage(msg, conn);
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_FOF_UPDATE)
		{
			// 1) process FOF
			// 2) send ACK
			System.out.println("DEBUG: Received FOF Update.. processing...");
			refreshFriends(msg.getMsgText());
			conn.sendMessage(new Message("ACK:FoF", username, Message.MESSAGE_CODE_FOF_ACK));
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_FOF_ACK)
		{
			System.out.println("FoF Ack Received");
			
			// TODO: Update some variable, so the program knows we don't need to resend FoF Update
		}
		else
			ChatController.getInstance().receiveDebugMessage("Unknown system message received.");
			
	}
	
	/*
	 * Parses through a FoF update message string for multiple friend nodes and 
	 * generates a Friend object for each one before adding to the Friend list.
	 */
	private void refreshFriends(String flist) {
		String [] nodes;  String [] hn; String host;  int port;  int priority;
		
		// Parse own host IP and Port   (given in format "0.0.0.0/0.0.0.0:5000")
		host = (ChatController.getInstance().server.providerSocket.getLocalSocketAddress()).toString();
		hn = host.split("[:/]");
		String myHost = hn[0];
		int myPort = Integer.parseInt(hn[2]);
		
		System.out.println("My hostname: " + myHost + "  |   My port: " + myPort);
		
		
		friends.clear();
		// split the FoF string by lines for separate friend nodes	
		nodes = flist.split("\n");
		
		// Parse each FoF line for the (host, port, priority)
		// Check if the FoF is self, if not.. add to Friends
		for(int i=0; i<nodes.length; i++){
			String [] tmp;
			tmp = nodes[0].split("/");
			if( tmp.length == 3){
				host = tmp[0];
				port = Integer.parseInt(tmp[1]);
				priority = Integer.parseInt(tmp[2]);
				if( (host == myHost) && (port == myPort) ){
					System.out.println("Ignoring self..  ");
				}
				else{
					friends.add(new Friend(host, port, priority));
				}
			}
			else{
				System.out.println("Unexpected Friend Info Format detected, ignoring");
			}
		}
	}
	
	private String generateFriendsString() {
		String rv = "";
		Iterator<Connection> connList = connections.iterator();
		while (connList.hasNext()) {
			Object tmpConn = connList.next();
			rv = rv + ((Connection) tmpConn).toFriendString() + "\n";
		}
		return rv;
	}
	
	private Integer getNodeDepth() {
		return nodeDepth;
	}

	private void setNodeDepth(Integer nodeDepth) {
		this.nodeDepth = nodeDepth;
	}
}
