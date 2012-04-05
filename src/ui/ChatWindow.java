package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.SortedSet;


import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.ChatController;
import application.DistributedChat;
import application.Message;

public class ChatWindow implements GenericUI {
	JTextArea textArea;
	private static ChatController messageAPI = ChatController.getInstance();
	private JButton connectionButton;
	JFrame frame;
	// for implementaion of jlist that show a list of knownusers.
	private DefaultListModel knownUsers;
	//private DefaultListModel knownUsers;
	private JButton startButton;
	private JButton disconnectionButton;
	private JFrame settingWindow;
	
	private String displayText;
	private LinkedList<Message> msgs;
	private ListIterator<Message> msgIterator;
	
	/**
	 * Constructor
	 */
	public ChatWindow(){
		knownUsers = new DefaultListModel();
		//knownUsers = new DefaultListModel();
	}
	
	/**
	 * Function implementation from GenericUI
	 */
	@Override
	public void init() {
		frame = new JFrame("Chat Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		JPanel inputPanel = new JPanel();
//		inputPanel.setLayout(inputPanelLayout);
//
//		inputPanel.add(initTextInput());
		connectionButton = initConnectionButton();
		disconnectionButton = initDisconnectionButton();
		startButton = initStartButton();
		disconnectionButton.setVisible(false);
		startButton.setVisible(false);
		settingWindow = new SettingsWindow();
		
		JMenuBar menu = new MenuBar();
		frame.setJMenuBar(menu);
		
		frame.getLayeredPane();
		frame.getContentPane().add(initMsgScreen(), BorderLayout.CENTER);
		frame.getContentPane().add(initTextInput(), BorderLayout.SOUTH);
		frame.getContentPane().add(initUserPane(), BorderLayout.EAST);
		frame.setPreferredSize(new Dimension(700,450));
		frame.setMinimumSize(new Dimension(500,300));
		frame.pack();
		frame.setVisible(true);
		
		//promptInitialSetup();
		
		messageAPI.addObserver(this);
		
		msgs = new LinkedList<Message>();
	}

	private Component initUserPane() {			
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

		JList userList = new JList(knownUsers);
		userList.setFont( userList.getFont().deriveFont(Font.PLAIN) );
		userList.setPrototypeCellValue("1234567890");
		userList.setMinimumSize(new Dimension(80,300));
		JScrollPane userScrollPane = new JScrollPane(userList);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(connectionButton);
		buttonPanel.add(disconnectionButton);
		buttonPanel.add(startButton);
		
		userPanel.add(userScrollPane);
		userPanel.add(buttonPanel);
		userPanel.add(initChangeUsernameButton());
		
		return userPanel;
	}

	private void promptInitialSetup() {
		frame.setEnabled(false);
		String username = JOptionPane.showInputDialog(null, "Enter an user name: ");
		initNetwork(username);
		frame.setEnabled(true);
	}

	private void initNetwork(String username) {
		ChatController handler = ChatController.getInstance();
		handler.setUsername(username);
		handler.initListener(ensureValidPortInput());
	}
	private int ensureValidPortInput(){
		String port = JOptionPane.showInputDialog(null, "Enter the port for connection: ");
		while (port == null || port.equals("") || !isParsableToInt(port)){
			JOptionPane.showMessageDialog(null, "invalid Port");
			port = JOptionPane.showInputDialog(null, "Enter the port for connection: ");
		}
		return Integer.parseInt(port);
	}

	private boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	 
	@Override
	public void msgReceived(Message msg) {
		textArea.append(msg.getMsgText());
	}
	@Override
	public void sentMsg(String msg) {
		messageAPI.sendMsg(msg);
	}
	

	
	/**
	 * Window UI components including connect button, message field, and input field
	 * @return
	 */
	private JButton initConnectionButton(){
		final JButton button = new JButton("Connect");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	ChatController handler = ChatController.getInstance();
            	if( handler.hasServer() == false ){
            		JOptionPane.showMessageDialog(null, "Please setup listener port first!");
            		return;
            	}
//            	button.setEnabled(false);
                String addr = JOptionPane.showInputDialog(null, "Address of Peer:");
                if(addr != null){
                	handler.createConnection(addr, ensureValidPortInput());
//                	button.setEnabled(false);
                	toggleConnectionButton(button.getText());
                	
                }else{
                	JOptionPane.showMessageDialog(null, "Invalid Input!");
                	button.setEnabled(true);
                }
                
            }
        });
		return button;
	}
	
	private JButton initDisconnectionButton(){
		final JButton button = new JButton("Disconnect");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	ChatController handler = ChatController.getInstance();
            	try {
					handler.disconnect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	toggleConnectionButton(button.getText());
            }
        });
		return button;
	}
	
	private JButton initStartButton(){
		final JButton button = new JButton("Start Client");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	promptInitialSetup();
            	toggleConnectionButton(button.getText());
            }
        });
		return button;
	}
	
	//toggle between connect and disconnect
	private void toggleConnectionButton(String buttonText){
		if(buttonText.equals(connectionButton.getText())){
			connectionButton.setVisible(false);
			disconnectionButton.setVisible(true);
		}else if(buttonText.equals(startButton.getText())){
			startButton.setVisible(false);
			connectionButton.setVisible(true);
		}else{
			disconnectionButton.setVisible(false);
			connectionButton.setVisible(true);
			//startButton.setVisible(true);
		}
		connectionButton.repaint();
		frame.repaint();
	}
	
	private JComponent initChangeUsernameButton() {
		final JButton button = new JButton("Settings");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				/*String username = JOptionPane
						.showInputDialog(null, "Enter an user name: ");
				ChatController handler = ChatController.getInstance();
				if (username != null) {
					handler.setUsername(username);
				} else {
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				}*/
				if( !settingWindow.isVisible() ) {
					settingWindow.setVisible(true);
				}
				button.setEnabled(true);
			}
		});
		return button;
	}
	
	private JComponent initMsgScreen(){
		//textArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		JComponent scrollPane = new JScrollPane(textArea);
		((JScrollPane) scrollPane).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setMinimumSize(new Dimension(600,400));
		scrollPane.setPreferredSize(new Dimension(600,400));
		return scrollPane;
	}
	private JComponent initTextInput(){
		JComponent result = new JTextField();
		result.setSize(1000,20);
		result.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
			JTextField textField = (JTextField) e.getSource();
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				if (!textField.getText().trim().equals("")){
					sentMsg(textField.getText());
				}
				textField.setText("");
			}
			}
		});
		return result;
	}

	/**
	 * Returns Human readable message
	 */
	private String getFormattedMessage(Message message) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		if (message != null){
			String result =  String.format("%s (%s): %s", message.getUsername(), sdf.format(message.getTimestamp()),
					message.getMsgText() + "\n");
			if(DistributedChat.DEBUG){
				result = String.valueOf(message.getMessageNumber()) + "-" + result;
			}
			return result;
		}
		return "";
	}
	
	/*
	 * ChatController notified about change in userlist, obtain the new update.  
	 */
	private void updateUserList(){
		ChatController handler = ChatController.getInstance();
		SortedSet<String> newUsers = handler.getKnownUsers();
		knownUsers.clear();
		
		java.util.Iterator<String> userIter = newUsers.iterator();
		while(userIter.hasNext()){
			knownUsers.addElement(userIter.next());
		}
	}
	
	public void addMessage(Message message)
	{
		Message iteratorMsg;
		boolean inserted = false;

		if (msgs.isEmpty() == true) {
			msgs.addLast(message);
		}
		else {
			msgIterator = msgs.listIterator();
			while (msgIterator.hasNext() && !inserted) {
				iteratorMsg = msgIterator.next();
				if (iteratorMsg.getMessageNumber() == message.getMessageNumber() && message.getUsername().compareTo(iteratorMsg.getUsername()) < 0) {
					msgs.add(msgIterator.previousIndex(), message);
					inserted = true;
				}
				else if (iteratorMsg.getMessageNumber() == message.getMessageNumber() && !msgIterator.hasNext()) {
					msgs.addLast(message);
					inserted = true;
				}
				else if (iteratorMsg.getMessageNumber() > message.getMessageNumber()) {
					msgs.add(msgIterator.previousIndex(), message);
					inserted = true;
				}
				else if (iteratorMsg.getMessageNumber() < message.getMessageNumber() && !msgIterator.hasNext()) {
					msgs.addLast(message);
					inserted = true;
				}
			}

			if (msgs.size() >= 5000) {
				msgs = new LinkedList<Message>();
			}
		}
		displayText = "";
		Message m = new Message();
		msgIterator = msgs.listIterator();
		while(msgIterator.hasNext()){
			m = msgIterator.next();
			displayText += getFormattedMessage(m);
		}
		/*msgIterator = msgs.listIterator();
		while (msgIterator.hasNext()) {
			displayText += getFormattedMessage(msgIterator.next());
		}*/
		// Prints message to the message field in the format of time stamp, user name, and received message
		textArea.setText(displayText);	
	}
	
	/**
	 * Observer function
	 */
	@Override
	public void update(Observable messageAPI, Object msg) {
		if (messageAPI instanceof ChatController) {
			if(msg instanceof Message){
				Message message = (Message) msg;
				switch(message.getMessageCode()){
				case Message.MESSAGE_CODE_REGULAR_MESSAGE:
					addMessage(message);
					// Force the text area to scroll to the bottom.
					textArea.setCaretPosition(textArea.getDocument().getLength());
					break;
				case Message.MESSAGE_CODE_CONNECTION_ACK:
					this.toggleConnectionButton(connectionButton.getText());
					break;
				case Message.MESSAGE_CODE_USERLIST_UI_UPDATE:
					System.out.println("RECEIVED UI UPDATE PING");
					updateUserList();
					break;
//				case Message.MESSAGE_CODE_USERNAME_LIST_UPDATE:
//					if (DistributedChat.DEBUG) {
//						updateUserList();
//					}
//					break;
//				case Message.MESSAGE_CODE_USER_DISCONNECT:
//					updateUserList();
//					break;
//				case Message.MESSAGE_CODE_NEW_USERNAME_UPDATE_INIT:
//					updateUserList();
//					break;
//				case Message.MESSAGE_CODE_NEW_USERNAME_UPDATE:
//					updateUserList();
//					break;
				case Message.MESSAGE_CODE_INTERNAL_DEBUG_MESSAGE:
					// Prints message to the message field in the format of time stamp, user name, and received message
					addMessage(message);
					// Force the text area to scroll to the bottom.
					textArea.setCaretPosition(textArea.getDocument().getLength());
					break;
				
				case Message.MESSAGE_CODE_INTERNAL_ERROR_MESSAGE:
					JOptionPane.showMessageDialog(null, message.getMsgText());
					break;
					
				default:
					// Prints message to the message field in the format of time stamp, user name, and received message
					//addMessage(message);
					// Force the text area to scroll to the bottom.
					textArea.setCaretPosition(textArea.getDocument().getLength());
					break;

				}
				
			}
		} 
	}

}
