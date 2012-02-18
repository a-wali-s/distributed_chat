package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	/**
	 * @serial
	 */
	private String msgText = "";
	/**
	 * @serial
	 */
	private int id = -1;
	/**
	 * @serial
	 */
	private Date timestamp;
	/**
	 * @serial
	 */
	private String username = "";
	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;
	
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
	   /**
	   * Always treat de-serialization as a full-blown constructor, by
	   * validating the final state of the de-serialized object.
	   */
	   private void readObject(
	     ObjectInputStream aInputStream
	   ) throws ClassNotFoundException, IOException {
	     //always perform the default de-serialization first
	     aInputStream.defaultReadObject();

	     //ensure that object state has not been corrupted or tampered with maliciously
	     validateState();
	  }

	/**
	 * Stub method for later validation
	 * @return true
	 */
	private boolean validateState() {
		return true;
	}
	   
    /**
    * This is the default implementation of writeObject.
    * Customise if necessary.
    */
    private void writeObject(
      ObjectOutputStream aOutputStream
    ) throws IOException {
      //perform the default serialization for all non-transient, non-static fields
      aOutputStream.defaultWriteObject();
    }
}
