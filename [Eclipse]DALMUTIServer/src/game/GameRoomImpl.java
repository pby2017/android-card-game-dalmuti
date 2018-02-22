package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class GameRoomImpl {
	
	String roomName;
	GamePlayerImpl master;
	HashMap<GamePlayerImpl, OutputStream> gamePlayers;
	ArrayList<String> gamerList;
	GameRoomImpl gri;
	GRoomManageImpl grmi;
	ArrayList<String> readyList;
	Boolean canStart;
	Boolean canJoin;
	GameImpl gi;
	
	public GameRoomImpl() {
		
	}
	
	public GameRoomImpl(String roomName, GamePlayerImpl gpi) {
		this.roomName = roomName;
		this.master = gpi;
		if(!gpi.IsMaster()) {
			gpi.setIsMaster();
		}
		gamePlayers = new HashMap<>();
		Collections.synchronizedMap(gamePlayers);
		gamerList = new ArrayList<>();
		readyList = new ArrayList<>();
		
		this.canStart = false;
		this.canJoin = true;
	}
	
	public GameRoomImpl getGri() {
		return this.gri;
	}
	
	public void setGri(GameRoomImpl gri, GRoomManageImpl grmi) {
		this.gri = gri;
		this.grmi = grmi;
	}
	
	public GameImpl getGi() {
		return this.gi;
	}
	
	public void setGi(GameImpl gi) {
		this.gi = gi;
	}
	
	public String getroomName() {
		return this.roomName;
	}
	
	public HashMap<GamePlayerImpl, OutputStream> getGamePlayers(){
		return gamePlayers;
	}
	
	public ArrayList<String> getGamerList() {
		return gamerList;
	}
	
	public Boolean checkCanJoin() {
		return canJoin;
	}
	
	public void gamePlayersPut(GamePlayerImpl gpi) {
		try {
			if(!gamePlayers.containsKey(gpi)) {
				gamePlayers.put(gpi, gpi.getOutputStream());
			}
			if(!gamerList.contains(gpi.getGamerListFormat())) {
				gamerList.add(gpi.getGamerListFormat());
			}
			gpi.setGri(this);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void gamePlayersRemove(GamePlayerImpl gpi) {
		try {
			if(gamePlayers.containsKey(gpi)) {
				gamePlayers.remove(gpi);
			}
			if(gamerList.contains(gpi.getGamerListFormat())) {
				gamerList.remove(gpi.getGamerListFormat());
			}
			gpi.setGri(null);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	void processCMD(String msg, GamePlayerImpl gpi) {
		
		Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    
	    String outInfo = "";
	    String oldInfo = "";
	    String newInfo = "";
	    boolean changeMaster = false;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
	    
	    try {
	    	if(msg.equals("CMD_pushStartBtn2")) {
	    		canJoin = false;
	    		
	    		GameImpl gi = new GameImpl(gamePlayers);
	    		
	    		this.gi = gi.getGi();
	    	} // CMD_pushStartBtn2
	    	
	    	if(msg.equals("CMD_pushReadyBtn2")) {
	    		oldInfo = gpi.getGamerListFormat();
	    		gamerList.remove(oldInfo);
				gpi.setIsReady();
				newInfo = gpi.getGamerListFormat();
				gamerList.add(newInfo);
				if(gpi.getIsReady()) {
					readyList.add(gpi.getIPHName());
					if(readyList.size() == gamerList.size()) {
						canStart = true;
					}else {
						canStart = false;
					}
				}else {
					readyList.remove(gpi.getIPHName());
					canStart = false;
				}
				gpiIt_STA = gamePlayers.keySet().iterator();
				while(gpiIt_STA.hasNext()) {
					gpiItNext_STA = gpiIt_STA.next();
					
					dout = new DataOutputStream(gamePlayers.get(gpiItNext_STA));
					dout.writeUTF("CMD_Server_pushReadyBtn3");
					if(gpiItNext_STA == this.master) {
						if(canStart) {
							dout.writeUTF("CMD_Server_canStartBtn3");
						}else {
							dout.writeUTF("CMD_Server_cannotStartBtn3");
						}
					}
					dout.writeUTF(oldInfo);
					dout.writeUTF(newInfo);
				}
				System.out.println("canJoinGame - "+gpi.getNickName());
				for(String tmp : gamerList) {
					System.out.println(tmp+",");
				}
	    	}
	    	
	    	if(msg.equals("CMD_ExitMember2")) {
	    		changeMaster = false;
	    		outInfo = gpi.getGamerListFormat();
	    		gamePlayersRemove(gpi);
	    		if(readyList.contains(gpi.getIPHName())) {
	    			readyList.remove(gpi.getIPHName());
	    		}
	    		
	    		if(gamerList.isEmpty()) {
					grmi.removeGameRoom(roomName);
				}else {
					if(gpi.IsMaster()) {
			    		gpiIt_STA = gamePlayers.keySet().iterator();
			    		while(gpiIt_STA.hasNext()) {
			    			gpiItNext_STA = gpiIt_STA.next();
			    			oldInfo = gpiItNext_STA.getGamerListFormat();
			    			gamerList.remove(oldInfo);
			    			this.master = gpiItNext_STA;
			    			if(!gpiItNext_STA.IsMaster()) {
			    				gpiItNext_STA.setIsMaster();
			    			}
			    			newInfo = gpiItNext_STA.getGamerListFormat();
			    			gamerList.add(newInfo);
			    			System.out.println(roomName+"방장 변경 - "+outInfo+" -> ("+oldInfo+" : "+newInfo+")");
			    			changeMaster = true;
			    			break;
			    		}
					}
					
					String exitMsg = "#"+gpi.getIPHName()+"님이 나가셨습니다.";
					
					gpiIt_STA = gamePlayers.keySet().iterator();
					while(gpiIt_STA.hasNext()) {
						gpiItNext_STA = gpiIt_STA.next();
						dout = new DataOutputStream(gamePlayers.get(gpiItNext_STA));
						dout.writeUTF("CMD_Server_ExitMember3");
						dout.writeUTF(outInfo);
						if(changeMaster) {
							dout.writeUTF("CMD_Server_changeMaster3");
							dout.writeUTF(oldInfo);
							dout.writeUTF(newInfo);
							if(gpiItNext_STA == this.master) {
								dout.writeUTF("master");
								if(canStart) {
									dout.writeUTF("canStart");
								}else {
									dout.writeUTF("");
								}
							}else {
								dout.writeUTF("");
							}
						}
						dout.writeUTF(exitMsg);
					}
		    	}
	    		gpi.initClass();
			} // CMD_ExitMember2
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	void processCMD(String msg, String CMDvalue1, GamePlayerImpl gpi) {
	    Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
	    
	    try {
	    	if(msg.equals("CMD_createGame1")) {
		    	gamePlayersPut(gpi);
		    	
		    	dout = new DataOutputStream(gpi.getOutputStream());
				dout.writeUTF("CMD_Server_canCreateGame");
				dout.writeUTF(CMDvalue1);
				oout = new ObjectOutputStream(gpi.getOutputStream());
				oout.writeObject(gamerList);
		    } // CMD_createGame1
	    	
	    	if(msg.equals("CMD_joinGame1")) {
	    		String joinMsg = "#"+gpi.getIPHName()+"님이 "+CMDvalue1+" 방에 입장하셨습니다.";
	    		gpiIt_STA = gamePlayers.keySet().iterator();
				while(gpiIt_STA.hasNext()) {
					gpiItNext_STA = gpiIt_STA.next();
					dout = new DataOutputStream(gamePlayers.get(gpiItNext_STA));
					dout.writeUTF("CMD_Server_JoinMember3");
					dout.writeUTF(gpi.getGamerListFormat());
					dout.writeUTF(joinMsg);
				}
				
				gamePlayersPut(gpi);
				
				dout = new DataOutputStream(gpi.getOutputStream());
	    		dout.writeUTF("CMD_Server_canJoinGame");
				dout.writeUTF(CMDvalue1);
				oout = new ObjectOutputStream(gpi.getOutputStream());
				oout.writeObject(gamerList);
				System.out.println("canJoinGame - "+gpi.getNickName());
				for(String tmp : gamerList) {
					System.out.println(tmp+",");
				}
			} // CMD_joinGame1
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    }
		
	    if(msg.equals("CMD_pushSendChatBtn2")) {
			String chatFormat = gpi.getIPHName()+" : "+CMDvalue1;
			gpiIt_STA = gamePlayers.keySet().iterator();
			while(gpiIt_STA.hasNext()) {
				try {
					gpiItNext_STA = gpiIt_STA.next();
					
					dout = new DataOutputStream(gamePlayers.get(gpiItNext_STA));
					dout.writeUTF("CMD_Server_pushSendChatBtn3");
					dout.writeUTF(chatFormat);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		} // CMD_pushSendChatBtn1
	}
}
