package application;

import java.io.*;

public class DebugGraph {
	private static DebugGraph instance;
	
	public DebugGraph(){}
	
	public static DebugGraph getInstance(){
		if(instance == null){
			instance = new DebugGraph();
		}
		return instance;
	}
	
	public void addEdge(Message msg){
		String[] addresses = msg.getMsgText().replaceAll("/", "").split(" ");
		String edge = "\"" + addresses[0].split(":")[0] + "\" " + addresses[1] + " \"" + addresses[2] + "\"";
		write(edge);
	}
	
	private void write(String edge){
		FileReader reader = null;
		FileWriter writer = null;
		File file = new File("graph.gv");
		try{
			reader = new FileReader(file);
			char[] cbuf = {};
			String fileContents = null;
			reader.read(cbuf);
			reader.close();
			fileContents = new String(cbuf);
			fileContents.replace("{", "{\n  " + edge);
			writer = new FileWriter(file);
			writer.write(fileContents);
			writer.close();
		}
		catch (FileNotFoundException e){
			System.out.printf("No Graph Found. Making new one");
		}
		catch (IOException e) {
			System.out.printf("N34-Df+G33r");
		}
		if (reader == null) {
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
