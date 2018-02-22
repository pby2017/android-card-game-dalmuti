package game;

import java.util.HashMap;
import java.util.Iterator;

public class PlayerImpl implements Player {

	private int subNameNumber;
	private int[] pDeck;
	private HashMap<Integer,Integer> hDeck;
	private int privilege, win, lose;
	private final Integer saveJokerCount = Integer.valueOf(13);
	
	public PlayerImpl()
	{
		
	};
	
	public PlayerImpl(int length, int[] contents, int index)
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
	
	public PlayerImpl(int start, int length, int[] contents, int startIndex, int index)
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

	@Override
	public void submitCard(int[] submitCard) {
		// TODO Auto-generated method stub
		
		hDeck.put(Integer.valueOf(submitCard[0]), Integer.valueOf(hDeck.get(submitCard[0]).intValue()-submitCard[1]));
		if(hDeck.get(submitCard[0]).intValue() <= 0)
		{
			hDeck.remove(submitCard[0]);
		}
	}

	@Override
	public void submitCardWithJoker(int withJokerCount)
	{
		hDeck.put(Integer.valueOf(saveJokerCount), Integer.valueOf(hDeck.get(saveJokerCount).intValue() - withJokerCount));
		if(hDeck.get(saveJokerCount).intValue() <= 0)
		{
			hDeck.remove(saveJokerCount);
		}
	}

	@Override
	public void showhDeck()
	{
		int haveCardCount = 0;
		Iterator<Integer> itDeck = hDeck.keySet().iterator();
		System.out.print("[");
		while(itDeck.hasNext())
		{
			int keyValue = itDeck.next().intValue();
			System.out.print(keyValue+"("+hDeck.get(keyValue)+")");
			haveCardCount += hDeck.get(keyValue).intValue();
			if(itDeck.hasNext())
			{
				System.out.print(",");
			}
		}
		System.out.println("] - "+haveCardCount);
	}

	@Override
	public boolean isEmpty()
	{
		if(hDeck.size() <= 0) return true;
		
		return false;
	}

	@Override
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

	@Override
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
	
	@Override
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

	@Override
	public void setPrivilege(int privilege)
	{
		this.privilege = privilege;
	}

	@Override
	public int getPrivilege()
	{
		return this.privilege;
	}

	@Override
	public int getSubNameNumber()
	{
		return this.subNameNumber;
	}

	@Override
	public void setWin()
	{
		this.win++;
	}
	
	@Override
	public int getWin()
	{
		return this.win;
	}
	
	@Override
	public void setLose()
	{
		this.lose++;
	}
	
	@Override
	public int getLose()
	{
		return this.lose;
	}
}
