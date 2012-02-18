package ui;

import application.Message;

public interface GenericUI {
	public void init();
	public void msgReceived(Message msg);
	public void sentMsg(String msg);
}
