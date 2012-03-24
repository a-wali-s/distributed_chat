package application;

import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class DebugGraph {
	private static ArrayList<String> edges = new ArrayList<String>();
	
	/**
	 * Adds an edge to a graph corresponding to a connection. 
	 * 
	 * @param msg Edge to be added to the graph (needs to be in DOT format)
	 * @param username 
	 */
	public static void addEdge(Message msg, String username){
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
	public static void removeVertex(Message msg, String username){
		String vertex = msg.getMsgText();
		System.out.println("vertex to remove: " + vertex);
		listRemoveVertex(vertex);
		write(username);
	}
	
	/**
	 * Create a graph file for a user
	 * 
	 * @param username
	 */
	public static void createFile(String username){
		File file = new File(username + "-graph.gv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Add the selected edge to the arraylist of edges
	 * 
	 * @param edge Edge to be added
	 */
	private static void listAddEdge(String edge){
		edges.add(edge);
		System.out.printf("Current edges: %s", edges.toString());
	}
	
	/**
	 * Add the selected edge to the arraylist of edges
	 * 
	 * @param vertex Vertex to be removed
	 */
	private static void listRemoveVertex(String vertex){
		String currentEdge = "";
		for (ListIterator<String> it = edges.listIterator(); ; currentEdge = it.next()){
			if (currentEdge.contains(vertex)) {
				edges.remove(currentEdge);
			}
			if (!it.hasNext()) break;
		}
		System.out.printf("Current edges: %s", edges.toString());
	}
	
	/**
	 * Write the list of edges to the graph
	 * 
	 * @param username
	 */
	private static void write(String username){
		FileWriter writer = null;
		File file = new File(username + "-graph.gv");
		try{
			String line = "";
			String fileContents = "";
			writer = new FileWriter(file);
			for (ListIterator<String> it = edges.listIterator(); ; line = it.next()){
				fileContents += line + "\n";
				if (!it.hasNext()) break;
			}
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
