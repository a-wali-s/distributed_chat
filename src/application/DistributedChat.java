package application;

import ui.*;

public class DistributedChat {
	private static GenericUI gui;
	public static final boolean DEBUG = true;
	
	public static void main(String args[]){
		gui = new ChatWindow();
		gui.init();
	}
}
