package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.Message;
import application.MessageAPI;

public class ChatWindow implements GenericUI, Observer {
	JTextArea textArea;
	private static final int TEXTAREA_ROWS = 20;
	private static final int TEXTAREA_COLUMNS = 20;
	private static MessageAPI messageAPI = MessageAPI.getInstance();
	public ChatWindow(){
		
	}

	@Override
	public void msgReceived(Message msg) {
		textArea.append(msg.getMsgText());

	}

	@Override
	public void sentMsg(String msg) {
		messageAPI.sendMsg(msg);

	}
	@Override
	public void init() {
		JFrame frame = new JFrame("Chat Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(initTextInput(), BorderLayout.CENTER);
		frame.getContentPane().add(initMsgScreen(), BorderLayout.NORTH);
		frame.getContentPane().add(initConnectionButton(), BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		messageAPI.addObserver(this);
	}
	private JComponent initConnectionButton(){
		final JButton button = new JButton("Connect");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	button.setEnabled(false);
                String addr = JOptionPane.showInputDialog(null, "Address of Peer:");
//                String port = JOptionPane.showInputDialog(null, "Port of Peer:");
                MessageAPI handler = MessageAPI.getInstance();
                if(addr != null){
                	handler.createConnection(addr);
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

	@Override
	public void update(Observable messageAPI, Object msg) {
		if (messageAPI instanceof MessageAPI) {
			Message message = (Message) msg;
			textArea.append(message.getTimestamp().toString().substring(10, 19) + " " + message.getUsername() + " said: "+ message.getMsgText());
		}
	}

}
