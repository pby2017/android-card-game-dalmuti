package game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class GRoomManageImpl {

	HashMap<String, GameRoomImpl> gameRoomList;
	GameRoomImpl gri;
	RobbyImpl ri;
	
	GRoomManageImpl(RobbyImpl ri){
		gameRoomList = new HashMap<>();
		Collections.synchronizedMap(gameRoomList);
		this.ri = ri;
	}
	
	void processCMD(String msg, String CMDvalue1, GamePlayerImpl gpi) {
		if(msg.equals("CMD_createGame1")) {
			gri = new GameRoomImpl(CMDvalue1, gpi);
    		addGameRoom(CMDvalue1, gri);
    		
    		gri.processCMD(msg, CMDvalue1, gpi);
		} // CMD_createGame1
		
		if(msg.equals("CMD_joinGame1")) {
			gri = getGri(CMDvalue1);
    		
    		gri.processCMD(msg, CMDvalue1, gpi);
		} // CMD_joinGame1
	}
	
	void addGameRoom(String roomName, GameRoomImpl gri) {
		System.out.println(roomName + "방 추가");
		gri.setGri(gri, this);
		gameRoomList.put(roomName, gri);
	}
	
	void removeGameRoom(String roomName) {
		System.out.println(roomName + "방 제거");
		gameRoomList.remove(roomName);
	}
	
	HashMap<String, GameRoomImpl> getAllGri() {
		return gameRoomList;
	}
	
	GameRoomImpl getGri(String roomName) {
		Iterator<String> griIt = gameRoomList.keySet().iterator();
		while(griIt.hasNext()) {
			String griItNext = griIt.next();
			
			if(griItNext.equals(roomName)) {
				return gameRoomList.get(griItNext);
			}
		}
		return null;
	}
	
	int getGameRoomCount() {
		return gameRoomList.size();
	}
	
	boolean checkGameName(String roomName) {
		if(gameRoomList.containsKey(roomName)) {
			return true;
		}else {
			return false;
		}
	}
}
