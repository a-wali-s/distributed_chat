package application;

import java.util.ArrayList;
import java.util.List;



public class main {
	public static List<User> users = new ArrayList<User>();

	public static void main(String[] args) {
		//Thread client = new Thread(new Client(),"T1");
		Thread server = new Thread(new MultiSocketServer(),"T2");
	    //client.start();
	    server.start();
        System.out.println("Goodbye, World");
    }
	
}
