package game;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GamePlayerImpl {

	private String nickName;
	private int ip_port_hash;
	private boolean isReady;
	private GameRoomImpl gri;
	private boolean isMaster;
	
	private Socket s;
	private InetAddress inetAddress;
	private int port;
	
	private int subNameNumber;
	private int[] pDeck;
	private HashMap<Integer,Integer> hDeck;
	private int privilege, win, lose;
	private final Integer saveJokerCount = Integer.valueOf(13);
	
	public GamePlayerImpl() {
		
	};
	
	public GamePlayerImpl(String nickName, Socket s) {
		this.nickName = nickName;
		
		this.s = s;
		this.inetAddress = s.getInetAddress();
		this.port = s.getPort();
		
		
		this.ip_port_hash = 
				(this.inetAddress.toString()+":"+
						Integer.toString(this.port))
				.substring(1).hashCode();
		
		initClass();
	}
	
	public void shareCard(int length, int[] contents, int index)
	{
		win = 0;
		lose = 0;
		privilege = index+1;
		subNameNumber = index+1;
		pDeck = new int[length];
		System.arraycopy(contents, length*index, pDeck, 0, length);
		hDeck = new HashMap<>();
		for(int i=0; i<length; ++i)
		{
			if(hDeck.containsKey(Integer.valueOf(pDeck[i])))
			{
				hDeck.put(Integer.valueOf(pDeck[i]), Integer.valueOf(hDeck.get(pDeck[i]).intValue()+1));
			}
			else
			{
				hDeck.put(Integer.valueOf(pDeck[i]), Integer.valueOf(1));
			}
		}
	}
	
	public void shareCard(int start, int length, int[] contents, int startIndex, int index)
	{
		win = 0;
		lose = 0;
		privilege = index+1;
		subNameNumber = index+1;
		pDeck = new int[length];
		System.arraycopy(contents, start+(length*startIndex), pDeck, 0, length);
		hDeck = new HashMap<>();
		for(int i=0; i<length; ++i)
		{
			if(hDeck.containsKey(Integer.valueOf(pDeck[i])))
			{
				hDeck.put(Integer.valueOf(pDeck[i]), Integer.valueOf(hDeck.get(pDeck[i]).intValue()+1));
			}
			else
			{
				hDeck.put(Integer.valueOf(pDeck[i]), Integer.valueOf(1));
			}
		}
	}
	
	public void initClass() {
		this.isReady = false;
		this.isMaster = false;
	}
	
	public String getNickName() {
		return this.nickName;
	}
	
	public int getIpPortHash() {
		return this.ip_port_hash;
	}
	
	public Boolean getIsReady() {
		return isReady;
	}
	
	public String checkIsReady() {
		return isReady ? "ready" : "";
	}
	
	public void setIsReady() {
		isReady = !isReady;
	}
	
	public Boolean IsMaster() {
		return isMaster;
	}
	
	public String getIsMaster() {
		return isMaster ? "<Master>" : "";
	}
	
	public void setIsMaster() {
		isMaster = !isMaster;
	}
	
	public GameRoomImpl getGri() {
		return this.gri;
	}
	
	public void setGri(GameRoomImpl gri) {
		this.gri = gri;
	}
	
	public String getIPHName() {
		return "["+Integer.toString(getIpPortHash())+
				"]"+getNickName();
	}
	
	public String getGamerListFormat() {
		return getIsMaster()+getIPHName()+"_"+checkIsReady();
	}
	
	public OutputStream getOutputStream() {
		try {
			return s.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap<Integer, Integer> getHDeck(){
		return this.hDeck;
	}

	public void submitCard(int[] submitCard) {
		// TODO Auto-generated method stub
		
		hDeck.put(Integer.valueOf(submitCard[0]), Integer.valueOf(hDeck.get(submitCard[0]).intValue()-submitCard[1]));
		if(hDeck.get(submitCard[0]).intValue() <= 0)
		{
			hDeck.remove(submitCard[0]);
		}
	}

	public void submitCardWithJoker(int jokerCount)
	{
		hDeck.put(saveJokerCount, Integer.valueOf(hDeck.get(saveJokerCount).intValue() - jokerCount));
		if(hDeck.get(saveJokerCount).intValue() <= 0)
		{
			hDeck.remove(saveJokerCount);
		}
	}

	public String showhDeck()
	{
		String showhDeck = "";
		int haveCardCount = 0;
		Iterator<Integer> itDeck = hDeck.keySet().iterator();
		showhDeck += "[";
		while(itDeck.hasNext())
		{
			int keyValue = itDeck.next().intValue();
			showhDeck += Integer.toString(keyValue)+"("+
					Integer.toString(hDeck.get(keyValue))+")";
			haveCardCount += hDeck.get(keyValue).intValue();
			if(itDeck.hasNext())
			{
				showhDeck += ",";
			}
		}
		showhDeck += "] - "+Integer.toString(haveCardCount);
		return showhDeck;
	}

	public boolean isEmpty()
	{
		if(hDeck.size() <= 0) return true;
		
		return false;
	}

	public boolean isContainCard(int cardNumber)
	{
		if(hDeck.containsKey(Integer.valueOf(cardNumber)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean hasJoker(int withJokerCount)
	{
		if(hDeck.get(Integer.valueOf(13)).intValue() >= withJokerCount)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean hasCard(int cardNumber, int cardCount)
	{
		if(!hDeck.containsKey(Integer.valueOf(cardNumber)))
		{
			return false;
		}
		if(hDeck.get(Integer.valueOf(cardNumber)).intValue() < cardCount)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void setPrivilege(int privilege)
	{
		this.privilege = privilege;
	}

	public int getPrivilege()
	{
		return this.privilege;
	}

	public int getSubNameNumber()
	{
		return this.subNameNumber;
	}

	public void setWin()
	{
		this.win++;
	}
	
	public int getWin()
	{
		return this.win;
	}
	
	public void setLose()
	{
		this.lose++;
	}
	
	public int getLose()
	{
		return this.lose;
	}
}
