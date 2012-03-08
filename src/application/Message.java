package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Message implements Serializable {
	/*
	 * Message code: 1xx - network informational code
	 * 				 100 - regular message
	 * 				 101 - update node depth
	 * 				 102 - connection ACK
	 * 				 103 - Friends of Friend information Message
	 * 				 104 - Friends of Friend information ACK
	 * 				 105 - Username update
	 * 				 106 - Initial Username update (ONLY FOR INITIAL SENDING, gets forwarded as 105 -- this is to update peer node for FoF purposes)
	 * 				 107 - Username list update
	 * 				 110 - Time sync request
	 * 				 111 - Time sync ACK
	 * 			     2xx - network debug code
	 * 				 201 - connection relationship
	 * 				 4xx - internal use code
	 * 				 400 - internal debug plain message
	 * 				 401 - internal error message
	 */
	public static final int MESSAGE_CODE_REGULAR_MESSAGE = 100;
	public static final int MESSAGE_CODE_NODE_DEPTH_UPDATE = 101;
	public static final int MESSAGE_CODE_CONNECTION_ACK = 102;
	public static final int MESSAGE_CODE_FOF_UPDATE = 103;
	public static final int MESSAGE_CODE_FOF_ACK = 104;
	public static final int MESSAGE_CODE_NEW_USERNAME_UPDATE = 105;
	public static final int MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT = 106;
	public static final int MESSAGE_CODE_USERNAME_LIST_UPDATE = 107;
	public static final int MESSAGE_CODE_PORT_INFO = 108;
	public static final int MESSAGE_CODE_TIME_REQUEST = 110;
	public static final int MESSAGE_CODE_TIME_ACK = 111;
	public static final int MESSAGE_CODE_SEND_MESSAGE_NUMBER = 112;
	public static final int MESSAGE_CODE_CONNECTION_RELATIONSHIP = 201;
	public static final int MESSAGE_CODE_USER_DISCONNECT = 202;
	
	public static final int MESSAGE_CODE_INTERNAL_DEBUG_MESSAGE = 400;
	public static final int MESSAGE_CODE_INTERNAL_ERROR_MESSAGE = 401;
	/**
	 * @serial
	 */
	private String msgText = "";
	/**
	 * @serial
	 */
	private int messageCode = 100;
	/**
	 * @serial
	 */
	private int id = -1;
	/**
	 * @serial
	 */
	private Calendar timestamp;
	/**
	 * @serial
	 */
	private String username = "";
	/**
	 * @serial
	 */
	public int messageNumber;
	/**
	 * @serial
	 */
	public ArrayList<Integer> childNumbers = new ArrayList<Integer>();
	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * 
	 */
	public Message(){
		timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}
	
	public Message(String msgText, String username, Integer messageCode) {
		this.setMessageCode(messageCode);
		this.msgText = msgText;
		this.username = username;
		this.messageNumber = ClientInterface.getInstance().getMessageClock();
		timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		id = msgText.hashCode() + timestamp.hashCode();
		if (username != null) id += username.hashCode();
	}
	
	/* 
	 * Generate Message object to be sent through the chat network.
	 * 		- 'null' username indicates a System Message
	 * 		System messages will be used for passing user data, connections acks, and more.
	 * 		ie. messages that users will not read
	 */
	public Message(String msgText, String username) {
		this(msgText, username, 100);
	}
 
	/*
	 * Extracts actual text of a message
	 */
	public String getMsgText() {
		return msgText;
	}
	
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	public int getId() {
		return id;
	}

	public Date getTimestamp() {
		timestamp.setTimeZone(TimeZone.getDefault());
		return timestamp.getTime();
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

    
	public int getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}
}
