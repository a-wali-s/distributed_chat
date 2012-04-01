package ui;

import java.util.Date;

public class TestUserProperty {
	private Date currentTime;
	private long totalLatency;
	private int count;
	private String username;
	private Date startTime;
	public TestUserProperty(String username, Date startTime){
		count = 1;
		this.startTime = startTime;
		currentTime = new Date(System.currentTimeMillis());
		totalLatency = currentTime.getTime() - startTime.getTime();
		this.username = username;
	}
	public void update(Date msgTime){
		currentTime = new Date(System.currentTimeMillis());
		totalLatency += currentTime.getTime() - msgTime.getTime();
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
