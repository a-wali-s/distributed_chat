package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.Message;
import application.MessageAPI;

public class ChatWindow implements GenericUI {
	JTextArea textArea;
	private static final int TEXTAREA_ROWS = 20;
	private static final int TEXTAREA_COLUMNS = 20;
	private static MessageAPI messageAPI = MessageAPI.getInstance();
	
	/**
	 * Constructor
	 */
	public ChatWindow(){
		
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
		inputPanel.add(initConnectionButton());
		inputPanel.add(initChangeUsernameButton());

		frame.getContentPane().add(initMsgScreen(), BorderLayout.CENTER);
		frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		frame.setEnabled(false);
		String username = JOptionPane.showInputDialog(null, "Enter an user name: ");
		MessageAPI handler = MessageAPI.getInstance();
		handler.setUsername(username);
		frame.setEnabled(true);
		
		messageAPI.addObserver(this);
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
            	button.setEnabled(false);
                String addr = JOptionPane.showInputDialog(null, "Address of Peer:");
                MessageAPI handler = MessageAPI.getInstance();
                if(addr != null){
                	handler.createConnection(addr);
                }else{
                	JOptionPane.showMessageDialog(null, "Invalid Input!");
                }
                button.setEnabled(true);
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
				MessageAPI handler = MessageAPI.getInstance();
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
				sentMsg(((JTextField) e.getSource()).getText() + "\n");
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
				message.getMsgText());
	}
	
	/**
	 * Observer function
	 */
	@Override
	public void update(Observable messageAPI, Object msg) {
		if (messageAPI instanceof MessageAPI) {
			Message message = (Message) msg;
			// Prints message to the message field in the format of time stamp, user name, and received message
			textArea.append(getFormattedMessage(message));
			// Force the text area to scroll to the bottom.
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}

}
