package ui;

public interface GenericUI {
	public void init();
	public void msgReceived(String msg);
	public void sentMsg(String msg);
}
