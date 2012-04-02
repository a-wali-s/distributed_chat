package ui;

import java.util.Date;

public class TestUserProperty {
	private Date currentTime;
	private long totalLatency;
	private int count;
	private String username;
	private long startTime;
	public TestUserProperty(String username, long startTime){
		count = 1;
		this.startTime = startTime;
		currentTime = new Date(System.currentTimeMillis());
		totalLatency = currentTime.getTime() - startTime;
		this.username = username;
	}
	public void update(long l){
		currentTime = new Date(System.currentTimeMillis());
		totalLatency += currentTime.getTime() - l;
		count++;
	}
	public double getAvgRTT(){
		return (totalLatency)/ (double)(count);
	}
	@Override
	public String toString(){
		return String.format("Username: %s AvgRTT: %f count: %d", username, getAvgRTT(), count);
	}
}
