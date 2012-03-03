package application;

import java.util.Observable;


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
	
	public void initListener(){
		initListener(2004);
	}
	public void initListener(int port){
		server = new ConnectionListener(port);
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
	
	public void createConnection(String host, int port){
		client.createConnection(host, port);
	}
}
