package application;

import java.io.IOException;
import java.util.Observable;
import java.util.SortedSet;


public class ChatController extends Observable {
	private static ChatController instance;
	ConnectionListener server;
	ClientInterface client;
	
	/**
	 * Constructor
	 */
	private ChatController(){
		client = ClientInterface.getInstance();
	}
	
	public static ChatController getInstance(){
		if(instance == null){
			instance = new ChatController();
		}
		return instance;
	}
	
	public void initListener(int port){
		initListener(port, 0);
	}
	public void initListener(int port, int maxConnections){
		if( server != null) {
			receiveDebugMessage("Cannot change port while connected to Chat");
			return;
		}
		if (maxConnections > 0){
			server = new ConnectionListener(port, maxConnections);
		} else {
			server = new ConnectionListener(port);
		}
		Thread serverThread = new Thread(server,"T2");
	    serverThread.start();
	    client.refreshLocalAddresses();
	}
	
	public void sendMsg(String msg){
		client.sendMessage(msg);
	}
	
	public void receiveMsg(Message msg){
		setChanged();
		notifyObservers(msg);
	}
	public void receiveDebugMessage(String sMessage){
		receiveDebugMessage(sMessage, Message.MESSAGE_CODE_INTERNAL_DEBUG_MESSAGE);
	}
	public void receiveDebugMessage(String sMessage, int msgCode){
		if(DistributedChat.DEBUG){
			receiveMsg(new Message(sMessage, "SystemMsg", msgCode));
		}
	}
	public void disconnect() throws IOException{
		server.stop();
		client.disconnect();
		int port = server.port;
		server = null;
		initListener(port);
	}
	public void error(String errorMessage){
		receiveMsg(new Message(errorMessage, "ErrorMsg" , Message.MESSAGE_CODE_INTERNAL_ERROR_MESSAGE));
	}
	
	public void setUsername(String username) {
		client.setUsername(username);
	}
	
	public void createConnection(String host, int port){
		client.createConnection(host, port);
	}
	
	public int getListenerPort(){
		return server.port;
	}

	public boolean hasServer() {
		if( server != null )
			return true;
		else
			return false;
	}

	public SortedSet<String> getKnownUsers() {
		return client.knownUsers;
	}
}
