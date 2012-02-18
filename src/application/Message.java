package application;

import java.util.Date;

public class Message {
	private String msgText = "";
	private int id = -1;
	private Date timestamp;
	private String username = "";
	
	/*
	 * 
	 */
	public Message(){
		timestamp = new Date();
	}
	
	/* 
	 * Generate Message object to be sent through the chat network.
	 * 		- 'null' username indicates a System Message
	 * 		System messages will be used for passing user data, connections acks, and more.
	 * 		ie. messages that users will not read
	 */
	public Message(String msgText, String username) {
		this.msgText = msgText;
		this.username = username;
		timestamp = new Date();
		id = msgText.hashCode() + username.hashCode() + timestamp.hashCode();
	}
 
	/*
	 * Extracts actual text of a message
	 */
	public String getMsgText() {
		return msgText;
	}

	public int getId() {
		return id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getUsername() {
		return username;
	}
}
