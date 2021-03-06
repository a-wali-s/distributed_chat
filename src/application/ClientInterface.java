package application;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
public class ClientInterface{
	private static final String USERNAMES_SEPERATOR = ",";
	private static final int DISARM_NET_SPLIT_TIMER = 5;
	private static ClientInterface instance;
	private List<Message> netSplitMessageQueue;
 	List<Connection> connections;
 	List<Friend> friends;
 	List<String> localAddresses;
	SortedSet<String> knownUsers;
 	String message;	
 	public String username = "";
	private Integer nodeDepth = 1;
	private Integer messageNumber = 1;
	private boolean isHotNode = false;
	private int totalMessages = 0; //Message count for metrics purposes
	public boolean netSplitStatus = false;
	private Timer disarmTimer = null;

	public static ClientInterface getInstance(){
		if(instance == null){
			instance = new ClientInterface();
		}
		return instance;
	}

	private ClientInterface(){
		connections = new ArrayList<Connection>();
		friends = new ArrayList<Friend>();
		resetKnownUsers();
		localAddresses = new ArrayList<String>();
		netSplitMessageQueue = new ArrayList<Message>();
	}
	
	//Handlers for the total messages metric
	public int getTotalMessages(){
		return totalMessages;
	}
	
	public void incrementTotalMessages(){
		totalMessages++;
	}
	
	public int getKnownUsersCount(){
		return knownUsers.size();
	}
	
	// disconnect from all connections and reinitialize the singleton instance
	public void disconnect() throws IOException{
		for(int i = 0; i<connections.size();i++){
			Connection connect = connections.get(i);
			connect.disconnect();
			connect.socket.close();
			connect.in.close();
			connect.out.flush();
			connect.out.close();
		}
		connections = new ArrayList<Connection>();
		friends = new ArrayList<Friend>();
		resetKnownUsers();
		localAddresses = new ArrayList<String>();
		netSplitMessageQueue = new ArrayList<Message>();
		new DebugGraph();
	}
	
	private void resetKnownUsers() {
		if( knownUsers == null )
			knownUsers = Collections.synchronizedSortedSet(new TreeSet<String>());
		else
			knownUsers.clear();
		
		knownUsers.add(this.username);
	}
	
	/*
	 * Adds a new connection and corresponding thread for both accepting and creating connections.
	 * This function should generally not be touched unless current functionality drastically changes.
	 */
	void addConnection(Connection conn){
		Thread connThread = new Thread(conn);
		connThread.start();
		connections.add(conn);
		System.out.println("Number of connections: " + connections.size());
		
	}
	public int getMessageClock(){
		return messageNumber;
	}
	
	/*
	 * Adds new connection to list of peers (that are directly connected to this host)
	 * Sends a connection acknowledgment message to the newly connected peer, telling it its external IP.
	 * Also sends it's nodeDepth to the newly connected peer, which should increment it and set it's own node depth
	 */
	void acceptConnection(Connection conn){
		addConnection(conn);
		//Give new user the latest debug graph
		if(DistributedChat.DEBUG) conn.sendMessage(new Message(DebugGraph.getInstance().serializeGraph(),username,Message.MESSAGE_CODE_GRAPH_UPDATE));
		
		//Send ACK message to new user
		conn.sendMessage(new Message(conn.socket.getInetAddress().getHostAddress() + "", username, Message.MESSAGE_CODE_CONNECTION_ACK));
		
		//Update Lamport time of new user
		conn.sendMessage(new Message(this.messageNumber.toString(), username, Message.MESSAGE_CODE_SEND_MESSAGE_NUMBER));
		
		//Update node depth for new user
		ChatController.getInstance().receiveDebugMessage("NodeDepth " + getNodeDepth().toString());
		conn.sendMessage(new Message(getNodeDepth().toString(),username, Message.MESSAGE_CODE_NODE_DEPTH_UPDATE));
		conn.updateNodeDepth(getNodeDepth()+1);

		//send the complete username list plus its own username to the newly accepted node
		conn.sendMessage(new Message(username + USERNAMES_SEPERATOR + generateUserListString(), username, Message.MESSAGE_CODE_USERNAME_LIST_UPDATE));
		//send this username for the connection username on the new peer
		conn.sendMessage(new Message("", username, Message.MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT));
		
		// If we are expecting reconnects, forward the messages in the queue to this newly connected user.
		// There might be more than one person trying to reconnect to us due to the net split, so we will start a 
		// timer before setting the netSplitStatus back to "false".
		if( netSplitStatus ){
			System.out.println(username + ": " + "this is a reconnect.");
			flushNetSplitQueue(conn);
			if( disarmTimer == null ){
				disarmTimer = new Timer();
				disarmTimer.schedule(new disableNetSplitTask(), DISARM_NET_SPLIT_TIMER*1000);
			}
		}
	}

	/*
	 * TIMER TASK
	 * 	- Turns off netSplitStatus after timer expires.  This is to allow a time limit for processes
	 * 		to reconnect to this Node once a disconnect occurs in the system.  
	 */
    class disableNetSplitTask extends TimerTask {
        public void run() {
        	netSplitStatus = false;
            System.out.format("Time's up!%n");
            disarmTimer.cancel(); //Terminate the timer thread
            disarmTimer = null;
        }
	}

	/*
	 * Attempts to recover from a broken server-side connection (ie. connection that this node initiated)
	 * If this is a HOT node, then we'll ignore trying to reconnect because other people may be attempting to connecting to us.
	 */
	boolean tryRecovery(){
		if( isHotNode )
			return true;
		for( int i = 0; i < friends.size(); i++ ){
			Friend tmp = friends.get(i);
			if( createConnection( tmp.getHost(), tmp.getPort(), true ) ) {
				netSplitStatus = false;
				netSplitMessageQueue.clear();
				return true;
			}
		}
		return false;
	}
	
	boolean createConnection(String hostname, int port){
		return createConnection(hostname, port, false);
	}
	
	/*
	 * Attempt to create connection with given hostname and port
	 * 	- If "reconnect" is true, then this is called by a socket disconnect.  We will try to reconnect to a friend.
	 * 	- If "reconnect" is false, and we detect that connections is not empty, we failed connection sometime ago.  
	 * 		Purge connections and try again.
	 * 
	 * 	Returns True if connection was successful. False if failed.
	 */
	boolean createConnection(String hostname, int port, boolean reconnect){
		Connection conn;
		try {
			if( (!reconnect) && !(connections.isEmpty()) ){
					connections.clear();
			}
			Socket newSock = new Socket(hostname, port);
			conn = new Connection(newSock);
			addConnection(conn);
		}
		catch(UnknownHostException unknownHost) {
			unknownHost.printStackTrace();
			System.err.println("You are trying to connect to an unknown host!");
			return false;
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		if( reconnect ){
			System.out.println(username + ": " + "I successfully reconnected to " + hostname + ":" + Integer.toString(port));
			flushNetSplitQueue(conn);
		}
		
		return true;
	}
	
	private boolean flushNetSplitQueue(Connection conn) {
		System.out.println(username + ": " + "Attempting to flush messages......., should have " + netSplitMessageQueue.size());
		if( conn == null ){
			System.out.println(username + ": " + "ERROR, no connection to flush to");
			return false;
		}
		if( netSplitMessageQueue.isEmpty() ){
			System.out.println(username + ": " + "NO Message in Queue!");
			return true;
		}
		for(Message msg : netSplitMessageQueue)
		{
			conn.sendMessage(msg);
		}
		return true;
	}

	/*
	 * Propagates the message to all connected peers except one, which is the one that sent it
	 */
	void forwardMessage(Message msg, Connection conn)
	{
		Connection thisConn = null;
		for(int i = 0; i < connections.size(); i++) {
			thisConn = connections.get(i);
			if(thisConn != null && thisConn.socket != null && thisConn != conn) // if it isn't from the connection that sent the message
			{
				thisConn.sendMessage(msg);
			}
		}
		
		if( conn == null){
			ChatController.getInstance().receiveMsg(msg);
			messageNumber++;
		}
		
		if( netSplitStatus == true && (msg.getMessageCode() == Message.MESSAGE_CODE_REGULAR_MESSAGE) ){
			netSplitMessageQueue.add(msg);
//			System.out.println("+1 msg in NSQ");
		}
	}

	/*
	 * Broadcasts message to all peers that are directly connected to this host
	 * (Message propagation to the rest of the network will be responsibility of the peers)
	 */
	void sendMessage(Message msg)
	{
		forwardMessage(msg, null);
	}
	
	/*
	 * API for sendMessage(Message msg)
	 * This is called by the UI layer to broadcast a newly generated user message
	 */
	public void sendMessage(String msg){
		sendMessage(new Message(msg, username, Message.MESSAGE_CODE_REGULAR_MESSAGE));
	}
	
	public void setUsername(String username) {
		knownUsers.remove(this.username);
		this.username = username;
		knownUsers.add(this.username);
		ChatController.getInstance().receiveMsg(new Message("","",Message.MESSAGE_CODE_USERLIST_UI_UPDATE));
	}
	
	/*
	 * A new message has been received from one of the connections.
	 * Check for the message originator.  If null, then this is a system message - do not send to UI
	 */
	void receiveMessage(Message msg, Connection conn) throws IOException
	{
		if(msg.messageNumber >= this.messageNumber)
			this.messageNumber = msg.messageNumber+1;
		
		this.totalMessages++;
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
		case Message.MESSAGE_CODE_USER_DISCONNECT:
			processUserDisconnect(msg, conn);
			break;
		case Message.MESSAGE_CODE_GRAPH_UPDATE:
			processGraphUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_NODE_DEPTH_UPDATE:
			processNodeDepthUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_USERNAME_LIST_UPDATE:
			processUserListUpdate(msg, conn);
			break;
		case Message.MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT:
			processUserUpdate(msg, conn);
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
		case Message.MESSAGE_CODE_SEND_MESSAGE_NUMBER:
			Integer incomingMessageNumber = Integer.parseInt(msg.getMsgText());
			this.messageNumber = incomingMessageNumber;
			break;
		case Message.MESSAGE_CODE_CONNECT_REDIRECT:
			String[] addrAndPort = msg.getMsgText().split("/");
			this.disconnectFromParent();
			System.out.println(username + ": " + "creating a new connection!!!! to " + addrAndPort[1]);
			this.createConnection(addrAndPort[0], Integer.parseInt(addrAndPort[1]), false);
			break;
			
		default:
			ChatController.getInstance().receiveDebugMessage("Unknown system message received.");
		}
			
	}
	public void disconnectFromParent() throws IOException{
		for(int i=0;i<connections.size();i++){
			Connection connection = connections.get(i);
			if(!connection.isChild){
				connection.disconnect();
				connection.socket.close();
				connection.in.close();
				connection.out.flush();
				connection.out.close();
				connections.remove(i);
				break;
			}
		}
	}
	public int numberOfChildConnections() {
		int childCount = 0;
		for(Connection conn : connections)
		{
			if(conn.isChild)
			{
				childCount++;
			}
		}
		return childCount;
	}
	
	public void redirectConnection(Connection conn) {
		String freePeer = DebugGraph.getInstance().getFreePeer(connections, conn).toFriendString();
		System.out.println("sdfsfsdffsfsdsfsfsfssljhfskjsfhsdjksfsd" + freePeer);
		Message message = new Message(freePeer, null, Message.MESSAGE_CODE_CONNECT_REDIRECT);
		conn.sendSystemMessage(message, Message.MESSAGE_CODE_CONNECT_REDIRECT);
		conn.disconnect();
		
		
	}
	
	//////////////////////////////////////////////////////////////////
	/////////// process message

	/*
	 * Standard chat messages
	 */
	private void processRegularMessage(Message msg, Connection conn) {
		ChatController.getInstance().receiveMsg(msg);
		forwardMessage(msg, conn);
	}
	
	/*
	 * Initial connection acknowledgments. This is responded with information about local username, listener port, and graph information if in Debug mode
	 */
	private void processConnectionAck(Message msg, Connection conn) {
		ChatController.getInstance().receiveMsg(msg);
		if(DistributedChat.DEBUG){
			Message graphMsg = new Message(conn.socket.getInetAddress().getHostAddress() + ":" + msg.getUsername() + " -- " + 
					msg.getMsgText() + ":" + username //Creates a DOT language edge containing the acknowledging machine's external IP & username and this machine's external IP and username
					,username, Message.MESSAGE_CODE_CONNECTION_RELATIONSHIP);
			sendMessage(graphMsg);
			DebugGraph.getInstance().addEdge(graphMsg, this.username);
		}
		conn.sendMessage(new Message(username, username, Message.MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT));
		// Send the peer our listening port number so they can update their Connection list
		conn.sendMessage(new Message(Integer.toString(ChatController.getInstance().server.port), username, Message.MESSAGE_CODE_PORT_INFO));
	}
	
	/*
	 * Graph messages for adding edges.
	 */
	private void processConnectionRelationshipUpdate(Message msg,
			Connection conn) {
		DebugGraph.getInstance().addEdge(msg, this.username);
		forwardMessage(msg, conn);
	}
	
	/*
	 * Disconnect messages
	 */
	private void processUserDisconnect(Message msg, Connection conn){
		DebugGraph.getInstance().removeVertex(msg, this.username);
		ChatController.getInstance().receiveDebugMessage(msg.getUsername() + " has left the chat.");
		knownUsers.remove(msg.getUsername());
		ChatController.getInstance().receiveMsg(new Message("","",Message.MESSAGE_CODE_USERLIST_UI_UPDATE));
		forwardMessage(msg, conn);
	}
	
	/*
	 * New graph messages. Only intended for users as they come in. Do not forward.
	 */
	private void processGraphUpdate(Message msg, Connection conn){
		DebugGraph.getInstance().readGraph(msg, this.username);
	}
	
	/*
	 * Node depth update messages
	 */
	private void processNodeDepthUpdate(Message msg, Connection conn) {
		Integer newNodeDepth = Integer.parseInt(msg.getMsgText());
		conn.updateNodeDepth(newNodeDepth);
		newNodeDepth++;
		setNodeDepth(newNodeDepth);
		//ChatController.getInstance().receiveDebugMessage("after connection, set nodeDepth to " + newNodeDepth);
		msg.setMsgText(newNodeDepth.toString());
	}
	
	/*
	 * User list update messages
	 */
	private void processUserListUpdate(Message msg, Connection conn){
		resetKnownUsers();
		knownUsers = processUserListString(msg.getMsgText());
		knownUsers.add(username);
		ChatController.getInstance().receiveMsg(new Message("","",Message.MESSAGE_CODE_USERLIST_UI_UPDATE));

	}
	
	/*
	 * New user added to system messages.
	 */
	private void processUserUpdate(Message msg, Connection conn){
		knownUsers.add(msg.getUsername());
		ChatController.getInstance().receiveDebugMessage(msg.getUsername() + " has joined the chat.");
		ChatController.getInstance().receiveMsg(new Message("","",Message.MESSAGE_CODE_USERLIST_UI_UPDATE));
		if( msg.getMessageCode() == Message.MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT ){
			conn.updateUsername(msg.getUsername());
			forwardMessage(new Message(msg.getMsgText(), msg.getUsername(), Message.MESSAGE_CODE_NEW_USERNAME_UPDATE),conn);
		}
		else
			forwardMessage(msg,conn);
	}

	/*
	 * Friends of friends update messages
	 */
	private void processFOFUpdate(Message msg, Connection conn) {
		// 1) process FOF
		// 2) send ACK
		System.out.println(username + ": " + "DEBUG: Received FOF Update.. processing...");
		refreshFriends(msg.getMsgText());
		conn.sendMessage(new Message("ACK:FoF", username, Message.MESSAGE_CODE_FOF_ACK));
	}
	
	/*
	 * Friends of friends message initialization
	 */
	private void processPortInfo(Message msg, Connection conn) {
		// We received the verified port info for this peer, update our connections list and send out updated FoF list
		conn.updatePort(msg.getMsgText());
		conn.updateUsername(msg.getUsername());
		System.out.println(username + ": " + "I'm going to send this: \n" + generateFriendsString());
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
	private static SortedSet<String> processUserListString(String userlist){
		String[] users = userlist.split(USERNAMES_SEPERATOR);
		return Collections.synchronizedSortedSet(new TreeSet<String>(Arrays.asList(users)));
	}
	
	/*
	 * Parses through a FoF update message string for multiple friend nodes and 
	 * generates a Friend object for each one before adding to the Friend list.
	 */
	private void refreshFriends(String flist) {
		String [] nodes, hn;
		String host, port;
		int priority;
		isHotNode = false;
		System.out.println(username + ": " + "Parsing Friend String: " + flist);
		
		// Parse own host IP and Port   (given in format "0.0.0.0/0.0.0.0:5000")
		host = (ChatController.getInstance().server.providerSocket.getLocalSocketAddress()).toString();
		hn = host.split("[:/]");
		//String myHost = hn[0];
		String myPort = hn[2];
		//System.out.println("My hostname: " + myHost + "  |   My port: " + myPort);
		
		// split the FoF string by lines for separate friend nodes
		// If this new FoF update has a smaller sample than an existing FoF list, then just ignore it
		nodes = flist.split("\n");
		System.out.println(username + ": " + "Nodes size: "+ nodes.length + " |  Current friends sizes: " + friends.size());
		if( nodes.length < friends.size() ) {
			System.out.println(username + ": " + "Existing Friend sample size is bigger than new FoF list, ignoring");
			return;
		}
		else
			friends.clear();
		
		// Parse each FoF line for the (host, port, priority)
		// Check if the FoF is self, if not.. add to Friends
		// --  If self is first node on FoF list, then this node is HOT
		for(int i=0; i<nodes.length; i++){
			String [] tmp;
			tmp = nodes[i].split("/");
			if( tmp.length == 3){
				host = tmp[0];
				port = tmp[1];
				priority = Integer.parseInt(tmp[2]);
				if( port.trim().compareTo(myPort) == 0 ) {
					if( (host.compareTo("127.0.0.1") == 0) || localAddresses.contains(host) ) {
						if( i == 0 ){
							isHotNode = true;
							System.out.println(username + ": " + "i'm a hot node!");
						}
						System.out.println(username + ": " + "Ignoring self..  ");
						continue;
					}
				}
				System.out.println(username + ": " + "Added a new friend");
				friends.add(new Friend(host, Integer.parseInt(port), priority));
			}
			else{
				System.out.println(username + ": " + "Unexpected Friend Info Format detected, ignoring");
			}
		}
		System.out.println(username + ": " + "Current size of my friends list: " + friends.size());
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
	
	public void refreshLocalAddresses() {
		if( !localAddresses.isEmpty() )
			localAddresses.clear();
		
		InetAddress in;  String tmp;
		/*try {
			in = InetAddress.getLocalHost();
			InetAddress[] all = InetAddress.getAllByName(in.getHostName());
			for (int i=0; i<all.length; i++) {
				tmp = all[i].toString().split("/");
				localAddresses.add(tmp[1]);
			}
			System.out.println("My local addresses:");
			for( int i=0; i<localAddresses.size(); i++ ){
				System.out.println(localAddresses.get(i));
			}
		} catch (UnknownHostException e) {
			System.out.println(username + ": " + "WARNING: failed to load local addresses");
		}*/
		
		try {
			Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
			if( nifs == null )
				return;
			
			while( nifs.hasMoreElements()){
				NetworkInterface nif = nifs.nextElement();
				Enumeration<InetAddress> adrs = nif.getInetAddresses();
				while( adrs.hasMoreElements()){
					InetAddress adr = adrs.nextElement();
					if( adr != null && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress())){
						tmp = adr.toString();
						System.out.println(tmp.substring(1, tmp.length()));
						localAddresses.add(tmp.substring(1, tmp.length()));
					}
				}
			}
		}
		catch( SocketException e){
			e.printStackTrace();
			System.out.println(username + ": " + "WARNING: failed to load local addresses");
		}
	}
}
