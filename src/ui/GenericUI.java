package ui;

import java.util.Observer;

import application.Message;

public interface GenericUI extends Observer {
	public void init();
	public void msgReceived(Message msg);
	public void sentMsg(String msg);
}
