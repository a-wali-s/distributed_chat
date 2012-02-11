package ui;

public class ChatWindow implements GenericUI {

	private static GenericUI instance;
	private ChatWindow(){
		
	}
	public GenericUI getInstance() {
		if(instance == null){
			instance = new ChatWindow();
		}
		return instance;

	}

	@Override
	public void msgReceived(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sentMsg(String msg) {
		// TODO Auto-generated method stub

	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
