package application;

import java.util.Date;

public class Message {
	private String msgText = "";
	private int id = -1;
	private Date timestamp;
	private String username = "";
	
	public Message(){
		timestamp = new Date();
	}
	
	public Message(String msgText, String username) {
		this.msgText = msgText;
		this.username = username;
		timestamp = new Date();
		id = msgText.hashCode() + username.hashCode() + timestamp.hashCode();
	}

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
