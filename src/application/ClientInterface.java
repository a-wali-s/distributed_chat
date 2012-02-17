package application;


import java.util.ArrayList;
public class ClientInterface{
 	ArrayList<Connection> connections;
 	String message;
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
	void receiveMessage(Message msg)
	{
		MessageHandler.receiveMsg(msg);
	}
}
