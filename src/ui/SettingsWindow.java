package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.ChatController;

public class SettingsWindow extends JFrame {
	
	String myUserName = "";
	JFrame myFrame = this;
	JLabel userErrorLabel;
	JLabel portErrorLabel;
	JTextField usernameInput;
	JTextField portInput;
	ChatController handler = ChatController.getInstance();
	
	SettingsWindow() {
		this.setTitle("Settings");
		this.setResizable(false);
		this.setVisible(false);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		Dimension winSize = new Dimension(500,180);
		this.setSize(winSize);
		this.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
		        usernameInput.setText(myUserName);
		        if( handler.hasServer() == true ) {
		        	portInput.setText(Integer.toString(handler.getListenerPort()));
		        	portInput.setEditable(false);
		        }
		        else
		        	portInput.setEditable(true);
		    }
			@Override
			public void componentHidden(ComponentEvent e) { }
			@Override
			public void componentMoved(ComponentEvent e) { }
			@Override
			public void componentResized(ComponentEvent e) { }
		});
		this.add(generateSettingsPanel(), BorderLayout.CENTER);
		this.add(generateButtonPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel generateSettingsPanel() {
		JPanel tmpPanel = new JPanel();
		tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
		
		userErrorLabel = new JLabel("Username must be 1-20 Characters Long.");
		userErrorLabel.setForeground(Color.RED);
		userErrorLabel.setVisible(false);
		portErrorLabel = new JLabel("Please choose port number between 1024 and 65535.");
		portErrorLabel.setForeground(Color.RED);
		portErrorLabel.setVisible(false);
		
		JPanel userPanel = new JPanel();
		userPanel.setSize(400, 150);
		userPanel.setLocation(100, 100);
		userPanel.setAlignmentX(LEFT_ALIGNMENT); 
		JLabel usernameLabel = new JLabel("Username: ");
		usernameLabel.setAlignmentX(LEFT_ALIGNMENT);
		usernameInput = new JTextField();
		usernameInput.setColumns(30);
		userPanel.add(usernameLabel);
		userPanel.add(usernameInput);
		
		
		JPanel portPanel = new JPanel();
		portPanel.setSize(400, 150);
		JLabel portLabel = new JLabel("Listening Port: ");
		portLabel.setAlignmentX(LEFT_ALIGNMENT);
		portLabel.setSize(300, 50);
		portInput = new JTextField();
		portInput.setColumns(6);
		userPanel.add(portLabel,BorderLayout.WEST);
		userPanel.add(portInput,BorderLayout.CENTER);
		userPanel.add(Box.createHorizontalStrut(262));
		
		tmpPanel.add(Box.createRigidArea(new Dimension(0,20)));
		tmpPanel.add(userErrorLabel);
		tmpPanel.add(portErrorLabel);
		tmpPanel.add(userPanel);
		tmpPanel.add(Box.createRigidArea(new Dimension(0,5)));
		tmpPanel.add(portPanel);
		return tmpPanel;
	}
	
	private JPanel generateButtonPanel() {
		JPanel tmpPanel = new JPanel();
		tmpPanel.setAlignmentX(RIGHT_ALIGNMENT);
		
		JButton acceptButton = new JButton("Accept");
		acceptButton.setPreferredSize(new Dimension(75,30));
		acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	userErrorLabel.setVisible(false);
            	portErrorLabel.setVisible(false);
            	String usernameTxt = usernameInput.getText();
            	String portTxt = portInput.getText();
            	System.out.println("Accept button clicked!");
            	if( usernameTxt.length() < 2 || usernameTxt.length() > 20 ){
            		userErrorLabel.setVisible(true);
            	}
            	else if( !isValidPort(portTxt) ){
            		portErrorLabel.setVisible(true);
            	}
            	else {
            		ChatController handler = ChatController.getInstance();
            		myUserName = usernameTxt;
            		handler.setUsername(myUserName);
            		if( portInput.isEditable() == true )
            			handler.initListener(Integer.parseInt(portTxt));
            		myFrame.setVisible(false);
            	}
            }
        });
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(75,30));
		cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	userErrorLabel.setVisible(false);
            	portErrorLabel.setVisible(false);
            	usernameInput.setText("");
            	portInput.setText("");
            	myFrame.setVisible(false);
            }
        });	
		myFrame.getRootPane().setDefaultButton(acceptButton);
		tmpPanel.add(Box.createHorizontalStrut(280));
		tmpPanel.add(acceptButton, BorderLayout.WEST);
		tmpPanel.add(cancelButton, BorderLayout.EAST);
		
		return tmpPanel;
	}
	
	private boolean isValidPort(String port) {
		if( port == null )
			return false;
		else if( port.equals(""))
			return false;
		else if( !isParsableToInt(port) )
			return false;
		else {
			int portInt = Integer.parseInt(port);
			if( portInt < 1024 )
				return false;
			else if( portInt > 65535 )
				return false;
			else
				return true;
		}
		
	}
	private boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public void componentShown(ComponentEvent e) {
        System.out.println(e.getComponent().getClass().getName() + " --- Shown");

    }
}
