package application;

import ui.*;

public class DistributedChat {
	private static GenericUI gui;
	public static final boolean DEBUG = true;
	// public static final boolean DEBUG_NETWORK_DELAY = true && DEBUG;
	public static final boolean DEBUG_NETWORK_DELAY = false && DEBUG;
	public static final int DEBUG_NETWORK_DELAY_TIME = 1000; // in ms

	public static void main(String args[]) {
		if (args.length > 0) {
			// Testing interface takes 2, 4, 6, or 7 arguments.
			// The order for 7 arguments: username ListeningPort
			// connectingHost connectingPort testMessage msPerMsg numMessages 
			switch (args.length) {
			case 2:
				gui = new TestingInterface(args[0], Integer.parseInt(args[1]));
				break;
			case 4:
				gui = new TestingInterface(args[0], Integer.parseInt(args[1]),
						args[2], Integer.parseInt(args[3]));
				break;
			case 6:
				gui = new TestingInterface(args[0], Integer.parseInt(args[1]),
						args[2], Integer.parseInt(args[3]), args[4],
						Integer.parseInt(args[5]));
				System.out.printf("0: %s, 1: %d, 2: %s, 3: %d, 4: %s, 5: %d\n",args[0], Integer.parseInt(args[1]),
						args[2], Integer.parseInt(args[3]), args[4],
						Integer.parseInt(args[5]));
				break;
			case 7:
				gui = new TestingInterface(args[0], Integer.parseInt(args[1]),
						args[2], Integer.parseInt(args[3]), args[4], Integer.parseInt(args[5]),
						Integer.parseInt(args[6]));
			default:
				System.out.printf("Usage: distributed_chat <username> <port to listen on> [<host to connect to> <port to connect to> [<test message> <time per message (ms)> [<number of messages>]]]");
				System.exit(-1);
			}
		} else {
			gui = new ChatWindow();
		}
		gui.init();
	}
}
