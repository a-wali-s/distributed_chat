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
		client.sendRegularMessage(msg);
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
	public void disconnect(){
		server.stop();
		client.disconnect();
		server = null;
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
}
