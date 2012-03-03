package application;

import java.io.*;

public class DebugGraph {
	public static void addEdge(Message msg, String username){
		String[] addresses = msg.getMsgText().replaceAll("/", "").split(" ");
		String edge = "\"" + addresses[0] + "\" " + addresses[1] + " \"" + addresses[2] + "\"";
		write(edge, username);
	}
	public static void createFile(String username){
		File file = new File(username + "-graph.gv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
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
				fileContents = fileContents.replace("{", "{\n  " + edge);
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
}
