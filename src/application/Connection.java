package application;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Runnable {
	public Socket socket = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in = null;
	public int childNumber;
	public int nodeDepth = 1;
	public boolean isChild = false;
	private boolean connected = true;
	private String connPort = "";
	private String username = "";
	Message inBuffer = null;
	
	public Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		out.flush();
		in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

		this.connPort = Integer.toString(socket.getPort()); 
	}
	
	public void run(){
		try {
			while(connected) {
				//ChatController.getInstance().receiveDebugMessage("Wait for object");
				try {
				Object buffer = in.readObject();
				if(buffer instanceof Message)
					{
						inBuffer = (Message)buffer;
						receiveMessage(inBuffer);
					}
				}
				catch(OptionalDataException e)
				{
					e.printStackTrace();
					//throw new IOException();
				}
				catch(StreamCorruptedException e)
				{
					e.printStackTrace();
					throw new IOException();
				}

			}
			System.out.println(ClientInterface.getInstance().username + ": " + "disconnect from network");
			System.out.println(ClientInterface.getInstance().username + ": " + "Removing self from connections");
			socket.close();
			in.close();
			out.close();
			ClientInterface.getInstance().connections.remove(this);
		}
		catch(IOException e){
//			e.printStackTrace();
			
				ClientInterface.getInstance().netSplitStatus = true;
				//ChatController.getInstance().error("disconnect from " + this.username + " -- " + this.socket.getRemoteSocketAddress());
				System.out.println(ClientInterface.getInstance().username + ": " + "disconnect from " + this.getUsername() + " -- " + this.socket.getRemoteSocketAddress());
				//System.out.println("My Listening port: " + ChatController.getInstance().getListenerPort() + "  |  This connection port: " + this.socket.getLocalPort());
				//Message sent contains data needed to remove vertex from graph
				Message DCMessage = new Message("", this.getUsername(), Message.MESSAGE_CODE_USER_DISCONNECT);
				if (DistributedChat.DEBUG){ 
					DCMessage.setMsgText(this.getUsername());
				}
				try {
					receiveMessage(DCMessage);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Only a client should bother trying to reconnect.  A node acting on server-side of a disconnect does not need to do anything
				if( this.socket.getLocalPort() != ChatController.getInstance().getListenerPort() ){
	//				try {
	//					Thread.currentThread().sleep(5000);
	//				} catch (InterruptedException e1) {
	//					// TODO Auto-generated catch block
	//					e1.printStackTrace();
	//				}
					attemptRecovery();
				}
				else{
					System.out.println(ClientInterface.getInstance().username + ": " + "Removing self from connections");
					ClientInterface.getInstance().connections.remove(this);
				}
			//e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	void disconnect(){
		connected = false;
	}
	
	private void attemptRecovery() {
		ClientInterface ci = ClientInterface.getInstance();
		if( ci.tryRecovery() ) {
			System.out.println(ClientInterface.getInstance().username + ": " + "Recovery successful!, breaking old connection");
			ci.connections.remove(this);
		}
		else {
			ChatController.getInstance().receiveDebugMessage("Error: Chat disconnected - Failed to reconnect");
		}
	}

	/*
	 * Sends Message Object through this particular socket connection.
	 * This should only be called if the connection is alive.
	 */
	public int sendSystemMessage(Message msg, Integer messageCode) {
		try {
			if(connected)
			{
				out.writeObject(msg);
				out.flush();
				return 0;
			}
			return -1;
			
		}
		catch (IOException e) {
			return -1;
		}
	}
	
	
	/*
	 * Sends Message Object through this particular socket connection.
	 * This should only be called if the connection is alive.
	 */
	public int sendMessage(Message msg) {
		ClientInterface.getInstance().incrementTotalMessages();
		return sendSystemMessage(msg, Message.MESSAGE_CODE_REGULAR_MESSAGE);

	}
	
	public void receiveMessage(Message msg) throws IOException {
		if (DistributedChat.DEBUG_NETWORK_DELAY
				&& msg.getMessageCode() == Message.MESSAGE_CODE_REGULAR_MESSAGE) {
			try {

				Thread.sleep(DistributedChat.DEBUG_NETWORK_DELAY_TIME);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ClientInterface.getInstance().receiveMessage(msg, this);
	}
	
	/*
	 * Returns connection info in a Friend format ("[ip]/[port]/[priority]")
	 */
	public String toFriendString() {
		
		//TODO -- UPDATE PRIORITY, SOMEHOW..
		
		String ip = socket.getInetAddress().getHostAddress();
		String port = this.connPort;
		return (ip+"/"+port+"/"+nodeDepth);		
	}
	
	/*
	 * Update the locally saved port number for this connection
	 * (Port number for this socket is not necessarily same as node's listening port depending on point of initiation)
	 */
	public void updatePort(String port) {
		System.out.println(ClientInterface.getInstance().username + ": " + "NEW PORT: " + port);
		this.connPort = port;
	}
	
	/*
	 * Update the locally saved Username for this connection
	 */
	public void updateUsername(String usr) {
		System.out.println(ClientInterface.getInstance().username + ": " + "NEW Username: " + usr);
		this.username = usr;
	}
	
	/*
	 * Update the locally saved nodeDepth for this connection
	 */
	public void updateNodeDepth(int nd) {
		System.out.println(ClientInterface.getInstance().username + ": " + "NEW Depth: " + nd);
		this.nodeDepth = nd;
	}

	public String getUsername() {
		return username;
	}
}
