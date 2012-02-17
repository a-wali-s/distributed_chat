package application;

import ui.ChatWindow;
import ui.GenericUI;

public class MessageHandler {
	private static MessageHandler instance;
	private GenericUI ui;
	MultiSocketServer server;
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
		Thread clientThread = new Thread(client,"T1");
		server = new MultiSocketServer();
		Thread serverThread = new Thread(server,"T2");
		clientThread.start();
	    serverThread.start();
	}
	public void sendMsg(String msg){
		client.sendMessage(msg);
	}
	public void reseiveMsg(String msg){
		ui.msgReceived(msg);
	}
}
