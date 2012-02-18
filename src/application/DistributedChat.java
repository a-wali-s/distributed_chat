package application;

import ui.*;

public class DistributedChat {
	private static GenericUI gui;
	
	public static void main(String args[]){
		MessageAPI.getInstance().init();
		
		gui = new ChatWindow();
		gui.init();
	}
}
