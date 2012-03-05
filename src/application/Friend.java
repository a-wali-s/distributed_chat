package application;

/*
 * Friend Object.  To be used by ClientInterface to keep track of Friends (as a list)
 * 
 * Variables
 * 	hostName  	(String)	- IP address or hostname in string format
 * 	Priority	(Int)		- Priority to connect  (1-3 -- 1 being highest priority)
 * 
 */

public class Friend {
	
	private String hostname = "";
	private int port = 0;
	private int priority = 0;
	
	/*
	 * Default Constructor
	 */
	public Friend(){
		hostname = "";
		port = 0;
		priority = 0;
	}
	
	// Commenting this out for now, not sure of it's feasibility..  
//	/*
//	 * Constructs a Friend object directly from parsing a FoF update message (format - "[username]/[port]/[priority]" )
//	 * eg)  "localhost/5000/1"
//	 */
//	public Friend(String hostStr){
//		// hostname = anything up to first slash
//		// port = anything up to second slash
//		// priority = the remaining
//		
//		String [] tmp;
//		tmp = hostStr.split("/");
//		if( tmp.length != 3){
//			System.out.println("Unexpected FoF Format detected, ignoring");
//			
//			//TODO: WHAT TO DO ABOUT THIS???
//			return;
//		}
//		else{
//			hostname = tmp[0];
//			port = Integer.parseInt(tmp[1]);
//			priority = Integer.parseInt(tmp[2]);
//		}	
//	}
	
	
	/*
	 * Constructs a Friend object with separated hostname string, port number, and priority number
	 */
	public Friend(String host, int hport, int p)
	{
		hostname = host;
		port = hport;
		priority = 0;
	}
	
	/*
	 * Returns Hostname string of this Friend Object
	 */
	public String getHost(){
		return hostname;
	}
	
	/*
	 * Returns port integer of this Friend Object
	 */
	public int getPort(){
		return port;
	}
	
	/* 
	 * Returns details of this Friend object in String format  ( separated by a slash / )
	 * To be used for distributing Friends of Friends info
	 */
	public String toString(){
		String tmp;
		tmp = hostname + "/" + priority;
		return tmp;
	}
}
