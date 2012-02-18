package application;


import java.util.ArrayList;
public class ClientInterface{
 	ArrayList<Connection> connections;
 	String message;
 	// FIXME: should have some sort of authentication system for userid
 	String userid = "";
	public ClientInterface(){
		connections = new ArrayList<Connection>();
	}
	void addConnection(Connection conn){
		connections.add(conn);
	}
	void sendMessage(Message msg)
	{
		if (connections.size() > 0){
			for(int x=0;x<connections.size();x++){
				connections.get(x).sendMessage(msg);
			}
		}
	}
	public void sendMessage(String msg){
		sendMessage(new Message(msg, userid));
	}
	void receiveMessage(Message msg)
	{
		MessageAPI.getInstance().receiveMsg(msg);
	}
}
