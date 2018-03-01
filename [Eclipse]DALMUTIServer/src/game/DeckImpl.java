package game;

public class DeckImpl {

	static private int[] allDeck;
	private int deckLength = 80;
	final private int deckLimit = 13;
	
	public DeckImpl()
	{
		int tmpIndex=0;
		deckLength = 80;
		allDeck = new int[deckLength];
		for(int i=1; i<=12; ++i)
		{
			for(int j=1; j<=i; ++j)
			{
				allDeck[tmpIndex++] = i;
			}
		}
		allDeck[78]=13;
		allDeck[79]=13;
	}
	
	public DeckImpl(int deckLimit)
	{
		int tmpIndex=0;
		deckLength = (deckLimit*(deckLimit+1))/2 + 2;
		allDeck = new int[deckLength];
		for(int i=1; i<=deckLimit-2; ++i)
		{
			for(int j=1; j<=i; ++j)
			{
				allDeck[tmpIndex++] = i;
			}
		}
		allDeck[(deckLength-2-1)+1]=13;
		allDeck[(deckLength-2-1)+2]=13;
	}

	public void showAllDeck()
	{
		System.out.print("[");
		for(int i=0; i<deckLength; ++i)
		{
			System.out.print(allDeck[i]);
			if(i<deckLength-1)
			{
				System.out.print(",");
			}
		}
		System.out.println("]");
	}

	public void setAllDeck()
	{
		int tmpPos, tmpIndex, tmpCase;
		
		tmpCase=(int)Math.random()*deckLength+10;
		for(int i=0; i<tmpCase; ++i)
		{
			for(int j=0; j<deckLength; ++j)
			{
				tmpIndex = (int)(Math.random()*deckLength);
				tmpPos = allDeck[j];
				allDeck[j] = allDeck[tmpIndex];
				allDeck[tmpIndex] = tmpPos;
				
				tmpIndex = (int)(Math.random()*deckLength);
				tmpPos = allDeck[deckLength-j-1];
				allDeck[deckLength-j-1] = allDeck[tmpIndex];
				allDeck[tmpIndex] = tmpPos;
			}
		}
	}

	public int[] getAllDeck()
	{
		return DeckImpl.allDeck;
	}

	public int getDeckLength()
	{
		return this.deckLength;
	}

	public int getDeckLimit()
	{
		return this.deckLimit;
	}
}
