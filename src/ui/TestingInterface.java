package ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;

import application.ChatController;
import application.Message;

public class TestingInterface implements GenericUI, Runnable {
	private static final long UPDATES_PER_FILE_WRITE = 10;
	private ChatController chatController = ChatController.getInstance();
	private Hashtable<String, TestUserProperty> usersMsgCount = new Hashtable<String, TestUserProperty>();
	private String testMessage = "";
	private int msPerMsg = -1;
	private String username;
	private File testfile;

	public TestingInterface(String username, int ListeningPort,
			String connectingHost, int connectingPort, String testMessage,
			int msPerMsg) {
		this(username, ListeningPort, connectingHost, connectingPort);
		this.testMessage = testMessage;
		this.msPerMsg = msPerMsg;
	}

	public TestingInterface(String username, int ListeningPort,
			String connectingHost, int connectingPort) {
		this(username, ListeningPort);
		chatController.createConnection(connectingHost, connectingPort);
	}

	public TestingInterface(String username, int ListeningPort) {
		this.username = username;
		chatController.addObserver(this);
		chatController.setUsername(username);
		chatController.initListener(ListeningPort);
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
			usersMsgCount.put(username, new TestUserProperty(username));
		} else {
			usersMsgCount.get(username).update();
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
		for (Map.Entry<String, TestUserProperty> userEntry : usersMsgCount
				.entrySet()) {
			TestUserProperty property = userEntry.getValue();
			content += property.toString() + "\n";
		}
		try {
			FileWriter writer = new FileWriter(testfile);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(content);
	}

	@Override
	public void run() {
		int runningSecond = 0;
		try {
			while (msPerMsg != -1) {

				Thread.sleep(msPerMsg);
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