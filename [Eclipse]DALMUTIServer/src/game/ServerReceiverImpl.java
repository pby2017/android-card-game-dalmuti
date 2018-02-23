package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

class ServerReceiverImpl extends Thread {
	
	Socket s;
	
	DataInputStream din;
	DataOutputStream dout;
	ObjectOutputStream oout;
	
	Iterator<RobbyPlayerImpl> RPIit;
	RobbyImpl ri;
	RobbyPlayerImpl rpi;
	GameRoomImpl gri;
	GamePlayerImpl gpi;
	GameImpl gi;
	
	String name;
	
	ServerReceiverImpl(Socket s, RobbyImpl ri)
	{
		this.s=s;
		
		try {
			din = new DataInputStream(s.getInputStream());
			name = din.readUTF();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		this.ri = ri;
		
		System.out.println("["+s.getInetAddress()+":"+s.getPort()+"]"+"에서 접속하였습니다.");
	}
	
	public void run() {
		try {
			rpi = new RobbyPlayerImpl(name,s);
			gpi = new GamePlayerImpl(name, s);
			
			runInit();
			
			// listening client's request
			while(din!=null)
			{
				processReceiveCMD(din.readUTF());
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("error! : ServerReceiver run() getName : "+getName());
			if(s!=null) {
				try {
					System.out.println("error! : ServerReceiver run() & s.close()");
					s.close();
				}catch(Exception ex1) {
					ex1.printStackTrace();
				}
			}
		}finally {
			ri.processCMD("CMD_ExitMember1", rpi);
			if(gri != null) {
				gri.processCMD("CMD_ExitMember2", gpi);
			}
			
			System.out.println("[#"+name+"-"+s.getInetAddress()+":"+s.getPort()+"]"+"에서 접속을 종료하였습니다.");
			System.out.println("현재 서버 접속자 수는 "+ri.getRobbyPlayers().size()+"입니다.");
			System.out.println("ServerReceiver thread exit");
			if(s!=null) {
				try {
					System.out.println("ServerReceiver finally s.close()");
					s.close();
				}catch(Exception ex1) {
					ex1.printStackTrace();
				}
			}
		}
	}
	
	void runInit() {
		System.out.println("#"+name+":"+getName()+"님이 들어오셨습니다.");
		
		ri.processCMD("CMD_InitMember1", rpi);
		
		System.out.println("현재 서버 접속자 수는 "+ri.getRobbyPlayers().size()+"입니다.");
	}
	
	void processReceiveCMD(String CMD) {
		try {
			if(CMD.equals("CMD_Client_pushStartBtn")) {
				gri.processCMD("CMD_pushStartBtn2", gpi);
				this.gi = gri.getGi();
			} // CMD_Client_pushStartBtn
			
/************************************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/
			
			if(CMD.equals("CMD_Client_pushReadyBtn")) {
				gri.processCMD("CMD_pushReadyBtn2", gpi);
			} // CMD_Client_pushJoinBtn
/************************************************************************************************************************************/
			
			if(CMD.equals("CMD_Client_pushRefreshBtn2")) {
				ri.processCMD("CMD_pushRefreshBtn1", rpi);
			} // CMD_Client_pushRefreshBtn2

			if(CMD.contains("CMD_Client_pushSendChatBtn2")) {
				ri.processCMD("CMD_pushSendChatBtn1",CMD.substring(CMD.indexOf(":")+1), rpi);
			} // CMD_Client_pushSendChatBtn2
			
			if(CMD.contains("CMD_Client_createGame2")) {
				
				ri.processCMD("CMD_createGame1",CMD.substring(CMD.indexOf(":")+1), rpi, gpi);
				this.gri = gpi.getGri();
			} // CMD_Client_createGame
			
			if(CMD.contains("CMD_Client_joinGame2")) {
				
				ri.processCMD("CMD_joinGame1",CMD.substring(CMD.indexOf(":")+1), rpi, gpi);
				this.gri = gpi.getGri();
			} // CMD_Client_joinGame
			
			if(CMD.contains("CMD_Client_pushSendChatBtn3")) {
				gri.processCMD("CMD_pushSendChatBtn2", CMD.substring(CMD.indexOf(":")+1), gpi);
			} // CMD_Client_pushSendChatBtn3
			
			if(CMD.equals("CMD_Client_ExitMember3")) {
				gri.processCMD("CMD_ExitMember2", gpi);
				this.gri = null;
				runInit();
			} // CMD_Client_ExitMember3
			
			if(CMD.contains("CMD_Client_pushSubmitBtn4")) {
				gri.getGi().processCMD(CMD, gpi);
			} // CMD_Client_pushSubmitBtn4
			
			if(CMD.equals("CMD_Client_pushPassBtn4")) {
				gri.getGi().processCMD("CMD_pushPassBtn3", gpi);
			} // CMD_Client_pushPassBtn4
		}catch(Exception ex) {
			ex.printStackTrace();
			// 예외처리 필요.
		}
	}
	
	
}
