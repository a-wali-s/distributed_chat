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

	private ClientInterface(){
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
		conn.sendMessage(new Message("ACK:connection", username, Message.MESSAGE_CODE_CONNECTION_ACK));
		ChatController.getInstance().receiveDebugMessage("NodeDepth " + getNodeDepth().toString());
		conn.sendMessage(new Message(getNodeDepth().toString(),username, Message.MESSAGE_CODE_NODE_DEPTH_UPDATE));
		
		// TODO: SEND FOF UPDATE
	}

	
	void createConnection(String hostname, int port){
		try {
			if(connections.isEmpty()){
				Socket newConn = new Socket(hostname, port);
				addConnection(new Connection(newConn));
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
			setNodeDepth(Integer.parseInt(msg.getMsgText())+1);
			msg.setMsgText(getNodeDepth().toString());
			forwardMessage(msg, conn);
			ChatController.getInstance().receiveDebugMessage("after connection, set nodeDepth to " + (Integer.parseInt(msg.getMsgText())));
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_FOF_UPDATE)
		{
			// 1) process FOF
			// 2) send ACK
		}
		else if(msg.getMessageCode() == Message.MESSAGE_CODE_FOF_ACK)
		{
			// Update some variable, don't need to reset ACK
		}
		else
			ChatController.getInstance().receiveDebugMessage("Unknown system message received.");
			
	}
	
	private Integer getNodeDepth() {
		return nodeDepth;
	}

	private void setNodeDepth(Integer nodeDepth) {
		this.nodeDepth = nodeDepth;
	}
}
