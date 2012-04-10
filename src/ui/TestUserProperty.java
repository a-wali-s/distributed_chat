package ui;

import java.util.Date;

public class TestUserProperty {
	private long currentTime;
	private long totalLatency;
	private int count;
	private String username;
	private long startTime;
	public TestUserProperty(String username, long startTime){
		count = 1;
		this.startTime = startTime;
		currentTime = System.currentTimeMillis();
		totalLatency = currentTime - startTime;
		this.username = username;
	}
	public void update(long l){
		currentTime = System.currentTimeMillis();
		totalLatency += currentTime - l;
		count++;
	}
	public double getAvgRTT(){
		return (totalLatency)/ (double)(count);
	}
	@Override
	public String toString(){
		return String.format("%f %d", getAvgRTT(), count);
		//return String.format("Username: %s Avg Latency: %f count: %d", username, getAvgRTT(), count);
	}
}
