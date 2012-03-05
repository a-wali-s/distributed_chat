package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.DistributedChat;
import application.Message;
import application.ChatController;

public class ChatWindow implements GenericUI {
	JTextArea textArea;
	private static final int TEXTAREA_ROWS = 20;
	private static final int TEXTAREA_COLUMNS = 20;
	private static ChatController messageAPI = ChatController.getInstance();
	private JComponent connectionButton;
	// for implementaion of jlist that show a list of knownusers.
	private List<String> knownUsers;
	
	/**
	 * Constructor
	 */
	public ChatWindow(){
		knownUsers = new ArrayList<String>();
	}
	
	/**
	 * Function implementation from GenericUI
	 */
	@Override
	public void init() {
		JFrame frame = new JFrame("Chat Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridLayout inputPanelLayout = new GridLayout(3, 1);
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(inputPanelLayout);

		inputPanel.add(initTextInput());
		connectionButton = initConnectionButton();
		inputPanel.add(connectionButton);
		inputPanel.add(initChangeUsernameButton());

		frame.getContentPane().add(initMsgScreen(), BorderLayout.CENTER);
		frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		frame.setEnabled(false);
		String username = JOptionPane.showInputDialog(null, "Enter an user name: ");
		ChatController handler = ChatController.getInstance();
		handler.setUsername(username);
		handler.initListener(ensureValidPortInput());
		frame.setEnabled(true);
		
		messageAPI.addObserver(this);
	}
	private int ensureValidPortInput(){
		String port = JOptionPane.showInputDialog(null, "Enter the port for connection: ");
		while (port.equals("") && isParsableToInt(port)){
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
	private JComponent initConnectionButton(){
		final JButton button = new JButton("Connect");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
//            	button.setEnabled(false);
                String addr = JOptionPane.showInputDialog(null, "Address of Peer:");
                ChatController handler = ChatController.getInstance();
                if(addr != null){
                	handler.createConnection(addr, ensureValidPortInput());
//                	button.setEnabled(false);
                }else{
                	JOptionPane.showMessageDialog(null, "Invalid Input!");
                	button.setEnabled(true);
                }
                
            }
        });
		return button;
	}
	
	
	private JComponent initChangeUsernameButton() {
		final JButton button = new JButton("Change Username");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				String username = JOptionPane
						.showInputDialog(null, "Enter an user name: ");
				ChatController handler = ChatController.getInstance();
				if (username != null) {
					handler.setUsername(username);
				} else {
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				}
				button.setEnabled(true);
			}
		});
		return button;
	}
	
	private JComponent initMsgScreen(){
		textArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
		textArea.setEditable(false);
		JComponent scrollPane = new JScrollPane(textArea);
		((JScrollPane) scrollPane).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return scrollPane;
	}
	private JComponent initTextInput(){
		JComponent result = new JTextField();
		result.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
			JTextField textField = (JTextField) e.getSource();
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				sentMsg(((JTextField) e.getSource()).getText());
				textField.setText("");
			}
			}
		});
		return result;
	}

	/**
	 * Returns Human readable message
	 */
	private static String getFormattedMessage(Message message) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return String.format("%s (%s): %s", message.getUsername(), sdf.format(message.getTimestamp()),
				message.getMsgText() + "\n");
	}
	
	private static List<String> processUserListString(String userlist){
		String[] users = userlist.split(",");
		return new ArrayList<String>(Arrays.asList(users));
	}
	private static String toUsersString(List<String> users){
		String result = "";
		for(String name: users){
			result += name + ", ";
		}
		result = "Current user list: "+ result + "\n";
		return result;
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
					// Prints message to the message field in the format of time stamp, user name, and received message
					textArea.append(getFormattedMessage(message));
					// Force the text area to scroll to the bottom.
					textArea.setCaretPosition(textArea.getDocument().getLength());
					break;
				case Message.MESSAGE_CODE_CONNECTION_ACK:
					connectionButton.setEnabled(false);
					break;
				case Message.MESSAGE_CODE_USERNAME_LIST_UPDATE:
					if (DistributedChat.DEBUG) {
						knownUsers = processUserListString(message.getMsgText());
						textArea.append(toUsersString(knownUsers));
					}
					break;
				case Message.MESSAGE_CODE_NEW_USERNAME_UPDATE:
					knownUsers.add(message.getMsgText());
					if(DistributedChat.DEBUG){
						textArea.append("System Message: "+ message.getUsername() +" has joined the chat.\n");
						textArea.append(toUsersString(knownUsers));
					}

					break;
					
				default:
					// Prints message to the message field in the format of time stamp, user name, and received message
					textArea.append(getFormattedMessage(message));
					// Force the text area to scroll to the bottom.
					textArea.setCaretPosition(textArea.getDocument().getLength());
					break;

				}
				
			}
		} 
	}

}
