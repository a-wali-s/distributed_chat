package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import application.ClientInterface;

public class MenuBar extends JMenuBar {

	/* Construct menu bar for program */
	MenuBar() {
		JMenu fileMenu = createFileMenu();
		JMenu chatMenu = createChatMenu();
		this.add(fileMenu);
		this.add(chatMenu);
	}
	
	private JMenu createFileMenu() {
		JMenu tmpMenu = new JMenu("File");
		JMenuItem aboutAction = new JMenuItem("About");
		aboutAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("About item got clicked!");				
			}
		});
		
		JMenuItem quitAction = new JMenuItem("Quit");
		quitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.printf("%s\n", ClientInterface.getInstance().getTotalMessages());
				System.exit(-1);		
			}
		});
		
		tmpMenu.add(aboutAction);
		tmpMenu.add(quitAction);
		return tmpMenu;
	}
	
	private JMenu createChatMenu() {
		JMenu tmpMenu = new JMenu("Chat");
		JMenuItem connectAction = new JMenuItem("Connect");
		connectAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Connect item got clicked!");				
			}
		});
		
		JMenuItem disconnectAction = new JMenuItem("Disconnect");
		disconnectAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Disconnect item got clicked!");	
			}
		});
		
		JMenuItem prefAction = new JMenuItem("Preferences");
		prefAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Preferences item got clicked!");	
			}
		});
		
		tmpMenu.add(connectAction);
		tmpMenu.add(disconnectAction);
		tmpMenu.addSeparator();
		tmpMenu.add(prefAction);
		return tmpMenu;
	}
}
