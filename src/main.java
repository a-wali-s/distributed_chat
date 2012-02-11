


public class main {
	
	public static void main(String[] args) {
		Thread client = new Thread(new Client(),"T1");
		Thread client2 = new Thread(new Client(),"T15");
		Thread server = new Thread(new Server(),"T2");
	    client.start();
	    client2.start();
	    server.start();
        System.out.println("Goodbye, World");
    }
	
}
