package application;

import ui.*;

public class DistributedChat {
	private static GenericUI gui;
	private static Integer nodeDepth = 1;
	private static DistributedChat instance;
	
	public static void main(String args[]){
		MessageAPI.getInstance().init();
		
		gui = new ChatWindow();
		gui.init();
	}

	public static DistributedChat getInstance(){
		if(instance == null){
			instance = new DistributedChat();
		}
		return instance;
	}

	public static Integer getNodeDepth() {
		return nodeDepth;
	}

	public static void setNodeDepth(Integer nodeDepth) {
		DistributedChat.nodeDepth = nodeDepth;
	}
}
