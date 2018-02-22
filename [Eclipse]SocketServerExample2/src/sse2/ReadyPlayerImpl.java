package sse2;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ReadyPlayerImpl {

	private String nickName;
	
	private Socket s;
	private InetAddress inetAddress;
	private int port;
	
	private ArrayList<String> chatList = null;
	
	public ReadyPlayerImpl() {
		
	}
	
	public ReadyPlayerImpl(String nickName, Socket s) {
		this.nickName = nickName;
		this.s = s;
		this.inetAddress = s.getInetAddress();
		this.port = s.getPort();
	}
}
