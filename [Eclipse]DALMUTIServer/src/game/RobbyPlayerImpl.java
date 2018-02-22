package game;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class RobbyPlayerImpl {

	private String nickName;
	private int ip_port_hash;
	private boolean isRobby;
	
	private Socket s;
	private InetAddress inetAddress;
	private int port;
	
	public RobbyPlayerImpl() {
		
	}
	
	public RobbyPlayerImpl(String nickName, Socket s) {
		this.nickName = nickName;
		
		this.s = s;
		this.inetAddress = s.getInetAddress();
		this.port = s.getPort();
		
		
		this.ip_port_hash = 
				(this.inetAddress.toString()+":"+
						Integer.toString(this.port))
				.substring(1).hashCode();
		this.isRobby = false;
	}
	
	public String getNickName() {
		return this.nickName;
	}
	
	public int getIpPortHash() {
		return this.ip_port_hash;
	}
	
	public boolean getIsRobby() {
		return isRobby;
	}
	
	public void setisRobby() {
		isRobby = !isRobby;
	}
	
	public String getIPHName() {
		return "["+Integer.toString(getIpPortHash())+
				"]"+getNickName();
	}
	
//	public String getRobbyListFormat() {
//		return getIPHName()+"_"+getIsRobby();
//	}
	
	public OutputStream getOutputStream() {
		try {
			return s.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Socket getSocket() {
		return s;
	}
}
