package application;

import ui.*;

public class DistributedChat {
	private static GenericUI gui;
	public static final boolean DEBUG = true;
	//public static final boolean DEBUG_NETWORK_DELAY = true && DEBUG;
	public static final boolean DEBUG_NETWORK_DELAY = false && DEBUG;
	public static final int DEBUG_NETWORK_DELAY_TIME = 1000; // in ms
	
	public static void main(String args[]){
		gui = new ChatWindow();
		gui.init();
	}
}
