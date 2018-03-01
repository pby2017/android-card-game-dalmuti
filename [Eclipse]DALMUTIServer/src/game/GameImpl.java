package game;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class GameImpl{

	private GamePlayerImpl[] playersArr;
	private PlayerManageImpl pm;
	private HashMap<Integer, GamePlayerImpl> playersHm, finishPlayersHm;
	private DeckImpl gameDeck;
	private int nowCardNumber, nowCardCount, nowTurn, lastTurn, ranking, lastTurnSubNameNumber;
	private int playerCount;
	private boolean ifFirstTurn;
	
	private int[] receiveSelectCard;
	private int jokerCount;
	
	public GameImpl() {
		
	}
	
	public GameImpl(HashMap<GamePlayerImpl, OutputStream> gamePlayers) {
		// input player count & give space to players array
		
		this.playerCount = gamePlayers.size();
		
		playersArr = new GamePlayerImpl[playerCount];
		int griItIndex=0;
		Iterator<GamePlayerImpl> griIt = gamePlayers.keySet().iterator();
		while(griIt.hasNext()) {
			playersArr[griItIndex++] = griIt.next();
		}
		
		// init deck
		gameDeck = new DeckImpl();
		
		// calculate each player card count
		int tmpDiv = gameDeck.getDeckLength(); // 80
		int tmpMod = tmpDiv%playerCount; // 80%사람수.
		// 1=0, 2=0, 3=2(78), 4=0, 5=0, 6=2(78), 7=3(77)
		tmpDiv = tmpDiv/playerCount;
		// 1=80, 2=40, 3=26(78), 4=20, 5=16, 6=13(78), 7=11(77)
		
		// card shuffle
		gameDeck.setAllDeck();
		
		// share card to players
		for(int i=0; i<(playerCount-tmpMod); ++i)
		{
			playersArr[i].shareCard(tmpDiv,gameDeck.getAllDeck(),i);
		}
		for(int i=playerCount-tmpMod; i<playerCount; ++i)
		{
			playersArr[i].shareCard(tmpDiv*(playerCount-tmpMod),tmpDiv+1,gameDeck.getAllDeck(),i-(playerCount-tmpMod), i);
		}
		
		// playerManage
		pm = new PlayerManageImpl(playersArr);
		playersHm = pm.getPlayers();
		finishPlayersHm = new HashMap<>();
		
		// init subValue
		nowCardNumber = 0;
		nowCardCount = 0;
		jokerCount = 0;
		nowTurn = 1;
		lastTurn = 1;
		ranking = 1;
		lastTurnSubNameNumber = 1;
		ifFirstTurn = true;
		receiveSelectCard = new int[2];
		
		processCMD("CMD_pushStartBtn2", null);
		
//		sc.close();
	}
	
	public GameImpl getGi() {
		return this;
	}
	
	void processCMD(String msg, GamePlayerImpl gpi) {
		Iterator<GamePlayerImpl> gpiIt_STA;
	    GamePlayerImpl gpiItNext_STA;
	    Iterator<Integer> intIt_STA;
	    Integer intItNext_STA;
	    
	    String outInfo = "";
	    String oldInfo = "";
	    String newInfo = "";
	    boolean changeMaster = false;
	    
	    DataOutputStream dout;
	    ObjectOutputStream oout;
		
		try {
			if(msg.equals("CMD_pushStartBtn2")) {
				System.out.println("게임이 시작되었습니다.");
				String first = playersHm.get(Integer.valueOf(1)).getIPHName();
				String last = playersHm.get(Integer.valueOf(lastTurn)).getIPHName();
				intIt_STA = playersHm.keySet().iterator();
				while(intIt_STA.hasNext()) {
					intItNext_STA = intIt_STA.next();
					gpiItNext_STA = playersHm.get(intItNext_STA);
					
					dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
					dout.writeUTF("CMD_Server_pushStartBtn3");
					dout.writeUTF(first);
					dout.writeUTF(last);
					dout.writeUTF(Integer.toString(nowCardNumber));
					dout.writeUTF(Integer.toString(nowCardCount));
					dout.writeUTF(gpiItNext_STA.showhDeck());
					oout = new ObjectOutputStream(gpiItNext_STA.getOutputStream());
					oout.writeObject(gpiItNext_STA.getHDeck());
				}
			} // CMD_pushStartBtn2
			
			if(msg.contains("CMD_Client_pushSubmitBtn4")) {
				lastTurn=nowTurn;
				lastTurnSubNameNumber = playersHm.get(lastTurn).getSubNameNumber(); // 필요한가?
				
				if(msg.contains("jokerCount")) {
					jokerCount = Integer.parseInt(msg.substring(msg.indexOf("jokerCount")+10));
					receiveSelectCard[1] = Integer.parseInt(msg.substring(msg.indexOf("cardCount")+9,
							msg.indexOf(":jokerCount")));
				}else {
					receiveSelectCard[1] = Integer.parseInt(msg.substring(msg.indexOf("cardCount")+9));
				}
				receiveSelectCard[0] = Integer.parseInt(msg.substring(msg.indexOf("cardNumber")+10,
						msg.indexOf(":cardCount")));
				nowCardNumber = receiveSelectCard[0];
				
//				System.out.println("낸 카드(jokerCount, cardNumber, cardCount : "+jokerCount+","+receiveSelectCard[0]+","+receiveSelectCard[1]);
				
				if(ifFirstTurn) {
					nowCardCount = receiveSelectCard[1];
					ifFirstTurn = false;
				}
				
				if(jokerCount > 0) {
					playersHm.get(nowTurn).submitCardWithJoker(jokerCount);
					nowCardCount = receiveSelectCard[1] + jokerCount;
					jokerCount = 0;
				}

				String finishPlayer = "";
				playersHm.get(nowTurn).submitCard(receiveSelectCard);
				if(playersHm.get(nowTurn).isEmpty()) {
					System.out.println("playersHm.get(nowTurn).isEmpty()");
					playersHm.get(nowTurn).setPrivilege(ranking);
					if(ranking == 1) {
						playersHm.get(nowTurn).setWin();
					} else {
						playersHm.get(nowTurn).setLose();
					}
					
					dout = new DataOutputStream(playersHm.get(nowTurn).getOutputStream());
					dout.writeUTF("CMD_Server_deckClear4");
					
					finishPlayer = playersHm.get(nowTurn).getIPHName();
					finishPlayersHm.put(ranking++, playersHm.get(nowTurn));
					playersHm.remove(nowTurn);
				}
				
				nowTurn = nowTurn%playerCount+1;
				System.out.println("nowTurn = nowTurn%playerCount+1;");
				while((!playersHm.isEmpty())&&(!playersHm.containsKey(nowTurn))) {
					nowTurn = nowTurn%playerCount+1;
				}
				
				if(lastTurn == nowTurn) {
					nowCardNumber = 0;
					nowCardCount = 0;
					ifFirstTurn = true;
				}
				
				if(!playersHm.containsKey(lastTurn)) {
					lastTurn = lastTurn%playerCount+1;
					while((!playersHm.isEmpty())&&(!playersHm.containsKey(lastTurn)))
					{
						lastTurn = lastTurn%playerCount+1;
					}
				}
				
				if(playersHm.isEmpty()) {
					// 뭘 할 까. 순위발표?
					intIt_STA = finishPlayersHm.keySet().iterator();
					while(intIt_STA.hasNext()) {
						intItNext_STA = intIt_STA.next();
						gpiItNext_STA = finishPlayersHm.get(intItNext_STA);
						
						dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
						dout.writeUTF("CMD_Server_gameClear4");
					}
				}
				String nowPlayer = playersHm.get(Integer.valueOf(nowTurn)).getIPHName();
				String last = playersHm.get(Integer.valueOf(lastTurn)).getIPHName();
				
				System.out.println("submit 후 전송시작");
				intIt_STA = playersHm.keySet().iterator();
				while(intIt_STA.hasNext()) {
					intItNext_STA = intIt_STA.next();
					gpiItNext_STA = playersHm.get(intItNext_STA);
					
					if(gpiItNext_STA == gpi) {
						System.out.println("submit 후 낸 사람"+gpiItNext_STA.getIPHName()+"한테 전달");
						dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
						dout.writeUTF("CMD_Server_pushSubmitBtnMyTurn4");
						dout.writeUTF(nowPlayer); // nowPlayer in Client
						dout.writeUTF(last);
						dout.writeUTF(Integer.toString(nowCardNumber));
						dout.writeUTF(Integer.toString(nowCardCount));
						dout.writeUTF(gpiItNext_STA.showhDeck());
						dout.writeUTF(finishPlayer);
						oout = new ObjectOutputStream(gpiItNext_STA.getOutputStream());
						oout.writeObject(gpiItNext_STA.getHDeck());
						continue;
					}
					System.out.println("submit 후 기다린 사람"+gpiItNext_STA.getIPHName()+"한테 전달");
					dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
					dout.writeUTF("CMD_Server_pushSubmitBtn4");
					dout.writeUTF(nowPlayer); // nowPlayer in Client
					dout.writeUTF(last);
					dout.writeUTF(Integer.toString(nowCardNumber));
					dout.writeUTF(Integer.toString(nowCardCount));
					dout.writeUTF(finishPlayer);
				}
				
				intIt_STA = finishPlayersHm.keySet().iterator();
				while(intIt_STA.hasNext()) {
					intItNext_STA = intIt_STA.next();
					gpiItNext_STA = finishPlayersHm.get(intItNext_STA);
					
					dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
					dout.writeUTF("CMD_Server_pushSubmitBtn4");
					dout.writeUTF(nowPlayer); // nowPlayer in Client
					dout.writeUTF(last);
					dout.writeUTF(Integer.toString(nowCardNumber));
					dout.writeUTF(Integer.toString(nowCardCount));
					dout.writeUTF(finishPlayer);
				}
				
				finishPlayer = "";
			}
			
			if(msg.equals("CMD_pushPassBtn3")) {
				System.out.println(gpi.getIPHName()+" - 패스");
				String last = playersHm.get(Integer.valueOf(lastTurn)).getIPHName();
				nowTurn = nowTurn%playerCount+1;
				while((!playersHm.isEmpty())&&(!playersHm.containsKey(nowTurn))) {
					nowTurn = nowTurn%playerCount+1;
				}
				if(lastTurn == nowTurn) {
					nowCardNumber = 0;
					nowCardCount = 0;
					ifFirstTurn = true; // 여기서 무슨 역할을 할까
				}
				if(!playersHm.containsKey(lastTurn)) {
					lastTurn = lastTurn%playerCount+1;
					while((!playersHm.isEmpty())&&(!playersHm.containsKey(lastTurn))) {
						lastTurn = lastTurn%playerCount+1;
					}
				}
				String nowPlayer = playersHm.get(Integer.valueOf(nowTurn)).getIPHName();
				
				intIt_STA = playersHm.keySet().iterator();
				while(intIt_STA.hasNext()) {
					intItNext_STA = intIt_STA.next();
					gpiItNext_STA = playersHm.get(intItNext_STA);
					
					dout = new DataOutputStream(gpiItNext_STA.getOutputStream());
					dout.writeUTF("CMD_Server_pushPassBtn4");
					dout.writeUTF(nowPlayer); // nowPlayer in Client
					dout.writeUTF(last);
					dout.writeUTF(Integer.toString(nowCardNumber));
					dout.writeUTF(Integer.toString(nowCardCount));
				}
			} // CMD_pushPassBtn3
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
