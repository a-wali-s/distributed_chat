package ui;

import java.util.Scanner;

public class ConsoleInterface implements GenericUI {
	private String address;
	private String port;
	private static GenericUI instance;
	private ConsoleInterface(){
	}
	public static GenericUI getInstance(){
		if(instance == null){
			instance = new ConsoleInterface();
		}
		return instance;
	}
	@Override
	public void init() {
		Scanner s = null;
		s = new Scanner(
				System.in);
		System.out.println("Enter ip address:");
		address = s.nextLine();
		System.out.println("Enter port:");
		port = s.nextLine();
	}
	public void msgReceived(String msg){
		System.out.println(msg);
	}
	public void sentMsg(String msg){
		
	}
}
