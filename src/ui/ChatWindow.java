package ui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.MessageHandler;

public class ChatWindow implements GenericUI {
	JTextArea textArea;
	private static final int TEXTAREA_ROWS = 20;
	private static final int TEXTAREA_COLUMNS = 20;
	public ChatWindow(){
		
	}

	@Override
	public void msgReceived(String msg) {
		textArea.append(msg);

	}

	@Override
	public void sentMsg(String msg) {
		MessageHandler.getInstance().sendMsg(msg);

	}
	@Override
	public void init() {
		JFrame frame = new JFrame("Chat Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel("blah");
		
		frame.getContentPane().add(initTextInput(), BorderLayout.CENTER);
		frame.getContentPane().add(initMsgScreen(), BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		
	}
	private JComponent initMsgScreen(){
		textArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
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

}
