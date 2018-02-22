package game;

import java.net.ServerSocket;
import java.net.Socket;

class ServerImpl extends Thread {
	
	ServerSocket ss;
	Socket s;
	
	RobbyImpl ri;
	
	public ServerImpl() {}
	
	public void run() {
		try {
			ss = new ServerSocket(7777);
			System.out.println("로비 서버가 시작되었습니다.");
			
			ri = new RobbyImpl();
			ri.setRi(ri);
			
			while(true)
			{
				s = ss.accept();
				new ServerReceiverImpl(s, ri).start();
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			try {
				if(ss != null) {
					ss.close();
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
}