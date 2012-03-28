package ui;

public class TestUserProperty {
	private long currentTime;
	private int count;
	private String username;
	private long startTime;
	public TestUserProperty(String username){
		count = 0;
		startTime = System.currentTimeMillis();
		currentTime = startTime;
		this.username = username;
	}
	public void update(){
		currentTime = System.currentTimeMillis();
		count++;
	}
	public double getAvgRTT(){
		return (currentTime - startTime)/ (double)(count);
	}
	@Override
	public String toString(){
		return String.format("Username: %s AvgRTT: %f count: %d", username, getAvgRTT(), count);
	}
}
