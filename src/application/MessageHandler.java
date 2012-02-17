package application;

import ui.ChatWindow;
import ui.GenericUI;

public class MessageHandler {
	private static MessageHandler instance;
	private GenericUI ui;
	ConnectionListener server;
	ClientInterface client;
	private MessageHandler(){
		ui = new ChatWindow();
		
	}
	public static MessageHandler getInstance(){
		if(instance == null){
			instance = new MessageHandler();
		}
		return instance;
	}
	public void init(){
		ui.init();
		client = new ClientInterface();
		server = new ConnectionListener(client);
		Thread serverThread = new Thread(server,"T2");
	    serverThread.start();
	}
	public void sendMsg(Message msg){
		client.sendMessage(msg);
	}
	public static void receiveMsg(Message msg){
		ui.msgReceived(msg);
	}
}
