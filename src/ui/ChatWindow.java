package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.Message;
import application.MessageAPI;

public class ChatWindow implements GenericUI {
	JTextArea textArea;
	private static final int TEXTAREA_ROWS = 20;
	private static final int TEXTAREA_COLUMNS = 20;
	public ChatWindow(){
		
	}

	@Override
	public void msgReceived(Message msg) {
		textArea.append(msg.getMsgText());

	}

	@Override
	public void sentMsg(String msg) {
		MessageAPI.getInstance().sendMsg(msg);

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
		
	}
	private JComponent initConnectionButton(){
		final JButton button = new JButton("Connect");
		button.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	button.setEnabled(false);
                String addr = JOptionPane.showInputDialog(null, "Address of Peer:");
                String port = JOptionPane.showInputDialog(null, "Port of Peer:");
                MessageAPI handler = MessageAPI.getInstance();
                //handler.addConnection(addr, Integer.getInteger(port));
                button.setEnabled(true);
            }
        });
		return button;
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
