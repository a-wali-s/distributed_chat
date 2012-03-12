package application;

import java.io.*;

public class DebugGraph {
	/**
	 * Adds an edge to a graph corresponding to a connection. 
	 * 
	 * @param msg Edge to be added to the graph (needs to be in DOT format)
	 * @param username 
	 */
	public static void addEdge(Message msg, String username){
		String[] addresses = msg.getMsgText().replaceAll("/", "").split(" ");
		String edge = "\"" + addresses[0] + "\" " + addresses[1] + " \"" + addresses[2] + "\"";
		write(edge, username);
	}
	
	/**
	 * Removes a vertex from a graph
	 * 
	 * @param msg Name of the vertex to be removed
	 * @param username
	 */
	public static void removeVertex(Message msg, String username){
		String vertex = msg.getMsgText().replaceAll("/", "");
		unwrite(vertex,username);
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
	 * Write the added edge to the graph
	 * 
	 * @param edge Edge to be added
	 * @param username
	 */
	private static void write(String edge, String username){
		BufferedReader input = null;
		FileWriter writer = null;
		File file = new File(username + "-graph.gv");
		try{
			String line;
			String fileContents = "";
			input =  new BufferedReader(new FileReader(file));
			while((line = input.readLine()) != null){
				fileContents += line;
			}
			input.close();
			writer = new FileWriter(file);
			if(fileContents.isEmpty()){
				writer.write("graph {\n  " + edge + "\n}");
			}else{
				fileContents = fileContents.replace("{", "{\n  " + edge + "\n");
				writer.write(fileContents);
			}
			writer.close();
		}
		catch (FileNotFoundException e){
			System.out.printf("No Graph Found. Making new one");
		}
		catch (IOException e) {
			System.out.printf("N34-Df+G33r");
		}
		if (input == null) {
			try {
				writer = new FileWriter(file);
			}
			catch (IOException e) {
				System.out.printf("Could not write graph");
			}
			try {
				writer.write("graph {\n  " + edge + "\n}");
				writer.close();
			}
			catch (IOException e) {
				System.out.printf("C\\s\\d 4tt wrd6h gr5ph");
			}
		}
	}
	
	/**
	 * Remove a vertex from a file
	 * 
	 * @param vertex Vertex to be removed
	 * @param username
	 */
	private static void unwrite(String vertex, String username){
		BufferedReader input = null;
		FileWriter writer = null;
		File file = new File(username + "-graph.gv");
		try{
			String line;
			String fileContents = "";
			input =  new BufferedReader(new FileReader(file));
			//Ignore any lines that contain the name of the vertex to be removed
			while((line = input.readLine()) != null && !line.contains(vertex)){
				fileContents += line;
			}
			input.close();
			writer = new FileWriter(file);
			writer.write(fileContents);
			writer.close();
		}
		catch (FileNotFoundException e){
			System.out.printf("No Graph Found.");
		}
		catch (IOException e) {
			System.out.printf("IOException");
		}
	}
}
