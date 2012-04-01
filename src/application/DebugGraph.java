package application;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DebugGraph {
	private static LinkedList<String> edges = new LinkedList<String>();
	private static DebugGraph instance;

	public static DebugGraph getInstance(){
		if(instance == null){
			instance = new DebugGraph();
		}
		return instance;
	}
	
	public DebugGraph() {
		edges = new LinkedList<String>();
	}
	
	/**
	 * Adds an edge to a graph corresponding to a connection. 
	 * 
	 * @param msg Edge to be added to the graph (needs to be in DOT format)
	 * @param username 
	 */
	public void addEdge(Message msg, String username){
		String[] addresses = msg.getMsgText().replaceAll("/", "").split(" ");
		String edge = "\"" + addresses[0] + "\" " + addresses[1] + " \"" + addresses[2] + "\"";
		System.out.println("edge to add: " + edge);
		listAddEdge(edge);
		write(username);
	}
	
	/**
	 * Removes a vertex from a graph
	 * 
	 * @param msg unique ID of the vertex getting removed
	 * @param username
	 */
	public void removeVertex(Message msg, String username){
		String vertex = msg.getMsgText();
		System.out.println("vertex to remove: " + vertex);
		listRemoveVertex(vertex);
		write(username);
	}
	
	/**
	 * Reads in a graph from another user
	 * 
	 * @param msg Message containing edges of the current graph
	 * @param username
	 */
	public void readGraph(Message msg, String username){
		if (msg.getMsgText().length() > 2){
			System.out.println("new graph" + msg.getMsgText());
			String graph[] = msg.getMsgText().replace("[","").replace("]","").split(",");
			edges.clear();
			for (int x = 0; x < graph.length; x++)
				edges.add(x,graph[x].trim());
		}
	}
	
	/**
	 * Finds a peer in the graph that is available to be connected to, starting with the current node's children
	 * 
	 */
	public Connection getFreePeer(List<Connection> connections){
		int lowestCount = ConnectionListener.getMaxConnections()+1;
		Connection lowestCountConnection = null;
		for(Connection conn : connections) {
			int count = 0;
			for (ListIterator<String> it = edges.listIterator(); it.hasNext();){
				String currentEdge = it.next();
				if(currentEdge.contains(conn.socket.getInetAddress().getHostAddress())) {
					count++;
				}
			}
			if(count < lowestCount)
			{
				lowestCount = count;
				lowestCountConnection = conn;
			}
		}
		return lowestCountConnection;
	}
	
	/**
	 * Writes out the graph to be interpreted by another user
	 *
	 * @returns String graph in string form. Edges delimited by ','
	 */
	public String serializeGraph(){
		return edges.toString();
	}
	
	/**
	 * Write out the graph to a file
	 * 
	 * @param username
	 */
	public void writeToFile(String username){
		write(username);
	}
	
	/**
	 * Add the selected edge to the arraylist of edges
	 * 
	 * @param edge Edge to be added
	 */
	private void listAddEdge(String edge){
		edges.add(edge);
		System.out.printf("Current edges: %s\n", edges.toString());
	}
	
	/**
	 * Add the selected edge to the arraylist of edges
	 * 
	 * @param vertex Vertex to be removed
	 */
	private void listRemoveVertex(String vertex){
		String currentEdge = "";
		try { 
			for (ListIterator<String> it = edges.listIterator(); it.hasNext();){
				currentEdge = it.next();
				System.out.println("Current edge to check for removal:" + currentEdge);
				if (currentEdge.contains(vertex)) {
					it.remove();
				}
			} 
		} catch (Exception e) {}
		System.out.printf("Current edges: %s", edges.toString());
	}
	
	/**
	 * Write the list of edges to the graph
	 * 
	 * @param username
	 */
	private void write(String username){
		FileWriter writer = null;
		File file = new File(username + "-graph.gv");
		try{
			String line = "";
			String fileContents = "";
			writer = new FileWriter(file);
			try {
				for (ListIterator<String> it = edges.listIterator(); ; line = it.next()){
					fileContents += line + "\n";
				}
			} catch (Exception e) {} //We are done at this point
			writer.write("graph {" + fileContents + "}");
			writer.close();
		}
		catch (FileNotFoundException e){
			System.out.printf("No Graph Found. Making new one");
		}
		catch (IOException e) {
			System.out.printf("N34-Df+G33r");
		}
	}
}
