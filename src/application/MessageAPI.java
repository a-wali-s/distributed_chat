package application;

import java.util.Observable;

//import ui.ChatWindow;
//import ui.GenericUI;

public class MessageAPI extends Observable {
	private static MessageAPI instance;
	//private GenericUI ui;
	ConnectionListener server;
	ClientInterface client;
	private MessageAPI(){
		//ui = new ChatWindow();
		
	}
	public static MessageAPI getInstance(){
		if(instance == null){
			instance = new MessageAPI();
		}
		return instance;
	}
	public void init(){
		//ui.init();
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
}
