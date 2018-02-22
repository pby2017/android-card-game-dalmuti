package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class RobbyImpl {

	HashMap<RobbyPlayerImpl, OutputStream> robbyPlayers;
	GRoomManageImpl grmi;
	
	ArrayList<String> robbyList;
	ArrayList<String> chatList;
	
	Iterator<Integer> integerIt;
    Integer integerItNext;
    Iterator<RobbyPlayerImpl> rpiIt;
    RobbyPlayerImpl rpiItNext;
    
    RobbyImpl ri;
    
    public RobbyImpl() {
    	robbyPlayers = new HashMap<>();
		Collections.synchronizedMap(robbyPlayers);
		robbyList = new ArrayList<>();
		chatList = new ArrayList<>();
		grmi = new GRoomManageImpl(this);
    }
    
    void processCMD(String msg, RobbyPlayerImpl rpi) {
		Iterator<RobbyPlayerImpl> rpiIt_STA;
	    RobbyPlayerImpl rpiItNext_STA;
	    Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
	    
	    try {
	    	if(msg.equals("CMD_InitMember1")) {
	    		String enterMsg = "#"+rpi.getIPHName()+"님이 들어오셨습니다.";
				rpiIt_STA = robbyPlayers.keySet().iterator();
				while(rpiIt_STA.hasNext()) {
					rpiItNext_STA = rpiIt_STA.next();
					dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
					dout.writeUTF("CMD_Server_NewMember2");
					dout.writeUTF(rpi.getIPHName());
					dout.writeUTF(enterMsg);
				}
				robbyPlayersPut(rpi);
				System.out.println(rpi.getNickName()+" 추가");
				dout = new DataOutputStream(rpi.getOutputStream());
				dout.writeUTF("CMD_Server_InitMember2");
				dout.writeUTF(rpi.getIPHName());
				oout = new ObjectOutputStream(rpi.getOutputStream());
				oout.writeObject(ri.getRobbyList());
	    	} // CMD_InitMember1
	    	
	    	if(msg.equals("CMD_pushRefreshBtn1")) {
				dout = new DataOutputStream(rpi.getOutputStream());
				dout.writeUTF("CMD_Server_pushRefreshBtn2");
				oout = new ObjectOutputStream(rpi.getOutputStream());
				oout.writeObject(ri.getRobbyList());
			} // CMD_pushRefreshBtn1
	    	
	    	if(msg.equals("CMD_ExitMember1")) {
				String exitMsg = "#"+rpi.getIPHName()+"님이 나가셨습니다.";
				robbyPlayersRemove(rpi);
				rpiIt_STA = robbyPlayers.keySet().iterator();
				while(rpiIt_STA.hasNext()) {
					try {
						rpiItNext_STA = rpiIt_STA.next();
						dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
						dout.writeUTF("CMD_Server_ExitMember2");
						dout.writeUTF(rpi.getIPHName());
						dout.writeUTF(exitMsg);
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			} // CMD_ExitMember1
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    }		
		
/************************************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/
		
//		if(msg.equals("CMD_pushJoinBtn1")) {
//			String oldValue = rpi.getRobbyListFormat();
//			robbyPlayersUpdate(rpi,"");
//			String newValue = rpi.getRobbyListFormat();
//			rpiIt_STA = robbyPlayers.keySet().iterator();
//			while(rpiIt_STA.hasNext()) {
//				try {
//					rpiItNext_STA = rpiIt_STA.next();
//					
//					dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
//					dout.writeUTF("CMD_Server_pushJoinBtn");
//					dout.writeUTF(oldValue);
//					dout.writeUTF(newValue);
//				}catch(Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		} // CMD_pushJoinBtn1
/************************************************************************************************************************************/
		
	}
	
	void processCMD(String msg, String CMDvalue1, RobbyPlayerImpl rpi) {
		Iterator<RobbyPlayerImpl> rpiIt_STA;
	    RobbyPlayerImpl rpiItNext_STA;
	    Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
		
	    try {
	    	if(msg.equals("CMD_pushSendChatBtn1")) {
				String chatFormat = rpi.getIPHName()+" : "+CMDvalue1;
				rpiIt_STA = robbyPlayers.keySet().iterator();
				while(rpiIt_STA.hasNext()) {
					rpiItNext_STA = rpiIt_STA.next();
					dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
					dout.writeUTF("CMD_Server_pushSendChatBtn2");
					dout.writeUTF(chatFormat);
				}
			} // CMD_pushSendChatBtn1
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	void processCMD(String msg, String CMDvalue1, RobbyPlayerImpl rpi, GamePlayerImpl gpi) {
		Iterator<RobbyPlayerImpl> rpiIt_STA;
	    RobbyPlayerImpl rpiItNext_STA;
	    Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
	    
	    try {
	    	if(msg.equals("CMD_createGame1")) {
	    		if(grmi.checkGameName(CMDvalue1)) {
	    			dout = new DataOutputStream(rpi.getOutputStream());
					dout.writeUTF("CMD_Server_cannotCreateGame");
					oout = new ObjectOutputStream(rpi.getOutputStream());
					oout.writeObject(robbyList);
				}else {
					String joinMsg = "#"+rpi.getIPHName()+"님이 "+CMDvalue1+" 방에 입장하셨습니다.";
					
					grmi.processCMD(msg, CMDvalue1, gpi); // 방 접속까지 처리
		    		
		    		robbyPlayersRemove(rpi);
		    		
					rpiIt_STA = robbyPlayers.keySet().iterator();
					while(rpiIt_STA.hasNext()) {
						rpiItNext_STA = rpiIt_STA.next();
						
						dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
						dout.writeUTF("CMD_Server_ExitMember2");
						dout.writeUTF(rpi.getIPHName());
						dout.writeUTF(joinMsg);
					}
				}
			} // CMD_createGame1
		    
		    if(msg.equals("CMD_joinGame1")) {
		    	if(!grmi.checkGameName(CMDvalue1) || !grmi.getGri(CMDvalue1).checkCanJoin()) {
	    			dout = new DataOutputStream(rpi.getOutputStream());
					dout.writeUTF("CMD_Server_cannotJoinGame");
					oout = new ObjectOutputStream(rpi.getOutputStream());
					oout.writeObject(robbyList);
				}else {
					String joinMsg = "#"+rpi.getIPHName()+"님이 "+CMDvalue1+" 방에 입장하셨습니다.";
		    		
					grmi.processCMD(msg, CMDvalue1, gpi); // 방 접속, 전파 처리
		    		
		    		robbyPlayersRemove(rpi);
		    		
					rpiIt_STA = robbyPlayers.keySet().iterator();
					while(rpiIt_STA.hasNext()) {
						rpiItNext_STA = rpiIt_STA.next();
						
						dout = new DataOutputStream(robbyPlayers.get(rpiItNext_STA));
						dout.writeUTF("CMD_Server_ExitMember2");
						dout.writeUTF(rpi.getIPHName());
						dout.writeUTF(joinMsg);
					}
				}
			} // CMD_joinGame1
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	void robbyPlayersPut(RobbyPlayerImpl rpi) {
		
		try {
			if(!robbyPlayers.containsKey(rpi)) {
				robbyPlayers.put(rpi, rpi.getOutputStream());
			}
			if(!robbyList.contains(rpi.getIPHName())) {
				robbyList.add(rpi.getIPHName());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
//	void robbyPlayersUpdate(RobbyPlayerImpl rpi, String cmd) {
//		
//		if(robbyList.contains(rpi.getRobbyListFormat())) {
//			int indexOfCMDvalue1 = robbyList.indexOf(rpi.getRobbyListFormat());
//			rpi.setisRobby();
//			robbyList.remove(indexOfCMDvalue1);
//			robbyList.add(indexOfCMDvalue1, rpi.getRobbyListFormat());
//		}
//	}
	
	void robbyPlayersRemove(RobbyPlayerImpl rpi) {
		
		if(robbyPlayers.containsKey(rpi)) {
			robbyPlayers.remove(rpi);
		}
		if(robbyList.contains(rpi.getIPHName())) {
			robbyList.remove(rpi.getIPHName());
		}
	}
	
	ArrayList<String> getRobbyList() {
		return this.robbyList;
	}
	
	RobbyImpl getRi() {
		return this.ri;
	}
	
	void setRi(RobbyImpl ri) {
		this.ri = ri;
	}
	
	HashMap<RobbyPlayerImpl, OutputStream> getRobbyPlayers(){
		return this.robbyPlayers;
	}
}
