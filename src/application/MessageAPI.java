package application;

import java.util.Observable;


public class MessageAPI extends Observable {
	private static MessageAPI instance;
	ConnectionListener server;
	ClientInterface client;
	
	/**
	 * Constructor
	 */
	private MessageAPI(){
		
	}
	
	public static MessageAPI getInstance(){
		if(instance == null){
			instance = new MessageAPI();
		}
		return instance;
	}
	
	public void init(){
		client = ClientInterface.getInstance();
		server = new ConnectionListener();
		Thread serverThread = new Thread(server,"T2");
	    serverThread.start();
	}
	
	public void sendMsg(String msg){
		client.sendMessage(msg);
	}
	
	public void receiveMsg(Message msg){
		setChanged();
		notifyObservers(msg);
	}
	
	public void setUsername(String username) {
		client.setUsername(username);
	}
	
	public void createConnection(String host){
		client.createConnection(host);
	}
}
