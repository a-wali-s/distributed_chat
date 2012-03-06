package application;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
public class ClientInterface{
	private static final String USERNAMES_SEPERATOR = ",";
	private static ClientInterface instance;
 	List<Connection> connections;
 	List<Friend> friends;
 	String message;	
 	String username = "";
	private Integer nodeDepth = 1;
	private List<String> knownUsers;

	public static ClientInterface getInstance(){
		if(instance == null){
			instance = new ClientInterface();
		}
		return instance;
	}

	private ClientInterface(){
		connections = new ArrayList<Connection>();
		friends = new ArrayList<Friend>();
		knownUsers = new ArrayList<String>();
	}
	
	// disconnect from all connections and reinitialize the singleton instance
	public void disconnect(){
		for(Connection connect : connections){
			try {
				connect.socket.close();
			} catch (IOException e) {
				ChatController.getInstance().error(e.getMessage());
			}
		}
		connections = new ArrayList<Connection>();
		friends = new ArrayList<Friend>();
		knownUsers = new ArrayList<String>();
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

		//send the complete username list plus its own username to the newly accepted node
		conn.sendMessage(new Message(username + USERNAMES_SEPERATOR + generateUserListString(), username, Message.MESSAGE_CODE_USERNAME_LIST_UPDATE));
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
	}

	
	/*
	 * A new message has been received from one of the connections.
	 * Check for the message originator.  If null, then this is a system message - do not send to UI
	 */
	void receiveMessage(Message msg, Connection conn)
	{
		switch(msg.getMessageCode()){
		case Message.MESSAGE_CODE_REGULAR_MESSAGE:
			processRegularMessage(msg, conn);
			break;
		case Message.MESSAGE_CODE_CONNECTION_ACK:
			processConnectionAck(msg, conn);
			break;
		case Message.MESSAGE_CODE_CONNECTION_RELATIONSHIP:
			processConnectionRelationshipUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_NODE_DEPTH_UPDATE:
			processNodeDepthUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_USERNAME_LIST_UPDATE:
			processUserListUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_NEW_USERNAME_UPDATE:
			processUserUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_FOF_UPDATE:
			processFOFUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_FOF_ACK:
			System.out.println("FoF Ack Received");
			
			// TODO: Update some variable, so the program knows we don't need to resend FoF Update
			break;
		case Message.MESSAGE_CODE_PORT_INFO:
			processPortInfo(msg, conn);
			break;
		case Message.MESSAGE_CODE_TIME_REQUEST:
			processTimeRequest();
			break;
		case Message.MESSAGE_CODE_TIME_ACK:
			processTimeACK();
			break;
		default:
			ChatController.getInstance().receiveDebugMessage("Unknown system message received.");
		}
			
	}
	
	//////////////////////////////////////////////////////////////////
	/////////// process message

	private void processRegularMessage(Message msg, Connection conn) {
		ChatController.getInstance().receiveMsg(msg);
		forwardMessage(msg, conn);
	}
	private void processConnectionAck(Message msg, Connection conn) {
		ChatController.getInstance().receiveMsg(msg);
		if(DistributedChat.DEBUG){
			sendMessage(new Message(conn.socket.getInetAddress().getHostAddress() + ":" + msg.getUsername() + " -- " + 
					conn.socket.getLocalAddress() + ":" + username
					,username, Message.MESSAGE_CODE_CONNECTION_RELATIONSHIP));
		}
		conn.sendMessage(new Message(username, username, Message.MESSAGE_CODE_NEW_USERNAME_UPDATE));
		// Send the peer our listening port number so they can update their Connection list
		conn.sendMessage(new Message(Integer.toString(ChatController.getInstance().server.port), username, Message.MESSAGE_CODE_PORT_INFO));
	}
	private void processConnectionRelationshipUpdate(Message msg,
			Connection conn) {
		DebugGraph.addEdge(msg, this.username);
		forwardMessage(msg, conn);
	}
	private void processNodeDepthUpdate(Message msg, Connection conn) {
		Integer newNodeDepth = Integer.parseInt(msg.getMsgText())+1;
		setNodeDepth(newNodeDepth);
		ChatController.getInstance().receiveDebugMessage("after connection, set nodeDepth to " + newNodeDepth);
		msg.setMsgText(newNodeDepth.toString());
	}
	private void processUserListUpdate(Message msg, Connection conn){
		knownUsers = processUserListString(msg.getMsgText());
		
		ChatController.getInstance().receiveMsg(msg);

	}
	private void processUserUpdate(Message msg, Connection conn){
		knownUsers.add(msg.getMsgText());
		ChatController.getInstance().receiveMsg(msg);
		forwardMessage(msg,conn);
	}

	private void processFOFUpdate(Message msg, Connection conn) {
		// 1) process FOF
		// 2) send ACK
		System.out.println("DEBUG: Received FOF Update.. processing...");
		refreshFriends(msg.getMsgText());
		conn.sendMessage(new Message("ACK:FoF", username, Message.MESSAGE_CODE_FOF_ACK));
	}
	private void processPortInfo(Message msg, Connection conn) {
		// We received the verified port info for this peer, update our connections list and send out updated FoF list
		conn.updatePort(msg.getMsgText());		
		System.out.println("I'm going to send this: \n" + generateFriendsString());
		// TODO: SEND FoF UPDATE
		sendMessage(new Message(generateFriendsString(), username, Message.MESSAGE_CODE_FOF_UPDATE));
	}
	private void processTimeRequest() {
		
	}
	private void processTimeACK() {
		
	}

	///////////////////////////////////////////////////////////
	////////// user list transformation
	
	private String generateUserListString(){
		String result = "";
		for(String user : knownUsers){
			result += user + USERNAMES_SEPERATOR;
		}
		if(result.endsWith(USERNAMES_SEPERATOR)){
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	private static List<String> processUserListString(String userlist){
		String[] users = userlist.split(USERNAMES_SEPERATOR);
		return new ArrayList<String>(Arrays.asList(users));
	}
	
	/*
	 * Parses through a FoF update message string for multiple friend nodes and 
	 * generates a Friend object for each one before adding to the Friend list.
	 */
	private void refreshFriends(String flist) {
		String [] nodes, hn;
		String host, port;
		int priority;
		System.out.println("Parsing Friend String: " + flist);
		
		// Parse own host IP and Port   (given in format "0.0.0.0/0.0.0.0:5000")
		host = (ChatController.getInstance().server.providerSocket.getLocalSocketAddress()).toString();
		hn = host.split("[:/]");
		String myHost = hn[0];
		String myPort = hn[2];
		//System.out.println("My hostname: " + myHost + "  |   My port: " + myPort);
		
		// split the FoF string by lines for separate friend nodes
		// If this new FoF update has a smaller sample than an existing FoF list, then just ignore it
		nodes = flist.split("\n");
		System.out.println("Nodes size: "+ nodes.length + " |  Current friends sizes: " + friends.size());
		if( nodes.length < friends.size() ) {
			System.out.println("Existing Friend sample size is bigger than new FoF list, ignoring");
			return;
		}
		else
			friends.clear();
		
		// Parse each FoF line for the (host, port, priority)
		// Check if the FoF is self, if not.. add to Friends
		for(int i=0; i<nodes.length; i++){
			String [] tmp;
			tmp = nodes[i].split("/");
			if( tmp.length == 3){
				host = tmp[0];
				port = tmp[1];
				priority = Integer.parseInt(tmp[2]);
				if( port.trim().compareTo(myPort) == 0 ) {
					if( (host.compareTo("127.0.0.1") == 0) || ( host.compareTo(myHost) == 0) ) {
						System.out.println("Ignoring self..  ");
						continue;
					}
				}
				System.out.println("Added a new friend");
				friends.add(new Friend(host, Integer.parseInt(port), priority));
			}
			else{
				System.out.println("Unexpected Friend Info Format detected, ignoring");
			}
		}
		System.out.println("Current size of my friends list: " + friends.size());
	}
	
	
	private String generateFriendsString() {
		String rv = "";
		Iterator<Connection> connList = connections.iterator();
		while (connList.hasNext()) {
			Object tmpConn = connList.next();
			rv = rv + ((Connection) tmpConn).toFriendString() + "\n";
			System.out.println(tmpConn.toString());
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
