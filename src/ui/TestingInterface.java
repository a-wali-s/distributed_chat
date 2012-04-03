package ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import application.ChatController;
import application.DebugGraph;
import application.Message;

public class TestingInterface implements GenericUI, Runnable {
	private static final long UPDATES_PER_FILE_WRITE = 10;
	private ChatController chatController = ChatController.getInstance();
	private Hashtable<String, TestUserProperty> usersMsgCount = new Hashtable<String, TestUserProperty>();
	private String testMessage = "";
	private int msPerMsg = -1;
	private int numMessages = -1;
	private int maxConnections = 0;
	private String username;
	private File testfile;
	private boolean isStartSending = false;

	public TestingInterface(String username, int ListeningPort, int maxConnections,
			String connectingHost, int connectingPort, String testMessage,
			int msPerMsg, int numMessages) {
		this(username, ListeningPort, maxConnections, connectingHost, connectingPort);
		this.testMessage = testMessage;
		this.msPerMsg = msPerMsg;
		this.numMessages = numMessages;
	}
	
	public TestingInterface(String username, int ListeningPort, int maxConnections,
			String connectingHost, int connectingPort, String testMessage,
			int msPerMsg) {
		this(username, ListeningPort, maxConnections, connectingHost, connectingPort);
		this.testMessage = testMessage;
		this.msPerMsg = msPerMsg;
	}

	public TestingInterface(String username, int ListeningPort, int maxConnections,
			String connectingHost, int connectingPort) {
		this(username, ListeningPort, maxConnections);
		chatController.createConnection(connectingHost, connectingPort);
	}

	public TestingInterface(String username, int ListeningPort, int maxConnections) {
		this.username = username;
		chatController.addObserver(this);
		chatController.setUsername(username);
		chatController.initListener(ListeningPort, maxConnections);
	}

	@Override
	public void update(Observable messageAPI, Object msg) {
		if (messageAPI instanceof ChatController) {
			if (msg instanceof Message) {
				Message message = (Message) msg;
				switch (message.getMessageCode()) {
				case Message.MESSAGE_CODE_REGULAR_MESSAGE:
					addMessage(message);
					break;
				}
			}
		}

	}

	private void addMessage(Message message) {
		String username = message.getUsername();
		if (usersMsgCount.get(username) == null) {
			usersMsgCount.put(username, new TestUserProperty(username, message.getTimestamp()));
		} else {
			usersMsgCount.get(username).update(message.getTimestamp());
		}
		if(message.getMsgText().equals("start")){
			isStartSending = true;
		}else if(message.getMsgText().equals("stop")){
			isStartSending = false;
		}
	}

	@Override
	public void init() {
		testfile = new File(username + "-test.txt");
		try {
			FileWriter writer = new FileWriter(testfile);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread t = new Thread(this);
		t.run();
		
	}

	@Override
	public void msgReceived(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sentMsg(String msg) {
		chatController.sendMsg(msg);

	}

	private void updateTestFile() {
		String content = "";
		testfile = new File(username + "-test.txt");
		synchronized (this) {
			Object[] values = usersMsgCount.values().toArray();
			for (int i = 0; i < values.length; i++) {
				TestUserProperty property = (TestUserProperty) values[i];
				content += property.toString() + "\n";
			}
		}
		try {
			FileWriter writer = new FileWriter(testfile);
			writer.write(content);
			System.out.println(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(username.equals("Server")){
			DebugGraph.getInstance().writeToFile(username);
			System.out.println(username);
		}
//		System.out.println(content);
	}

	@Override
	public void run() {
		int runningSecond = 0;
		try {
			while (msPerMsg != -1 && numMessages != runningSecond) {
				
				Thread.sleep(msPerMsg);
				while(!isStartSending){};
				sentMsg(testMessage);
				runningSecond++;
				if ((runningSecond % UPDATES_PER_FILE_WRITE) == 0) {
					updateTestFile();
				}
			}
			while (true) {
				// for root node
				Thread.sleep(UPDATES_PER_FILE_WRITE * 1000);
				updateTestFile();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
