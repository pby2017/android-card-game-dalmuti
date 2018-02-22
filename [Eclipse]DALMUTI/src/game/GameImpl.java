package game;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class GameImpl implements Game {

	private PlayerImpl[] playersArr;
	private PlayerManageImpl pm;
	private HashMap<Integer, PlayerImpl> playersHm, finishPlayersHm;
	private Deck gameDeck;
	private int nowCardNumber, nowCardCount, nowTurn, lastTurn, ranking, lastTurnSubNameNumber;
	private int playerCount;
	private boolean ifFirstTurn;
	
	private int[] receiveSelectCard;
	private int ifUseCardWithJoker;
	private int withJokerCount;
	private boolean usedCardWithJoker;
	private boolean wantToPass;
	
	Scanner sc;
	
	public GameImpl()
	{
		sc = new Scanner(System.in);
		// input player count & give space to players array
		
		while(true)
		{
			System.out.print("how many players? ");
			try {
				playerCount = sc.nextInt();
				if(playerCount<2 || playerCount > 8)
				{
					notCorrectInputMsg();
					continue;
				}
				else
				{
					break;
				}
			}catch(InputMismatchException e) {
				sc = new Scanner(System.in);
				notCorrectInputMsg();
			}
		}
		
		playersArr = new PlayerImpl[playerCount];
		
		// init deck
		gameDeck = new DeckImpl();
		
		// calculate each player card count
		int tmpDiv = gameDeck.getDeckLength();
		int tmpMod = tmpDiv%playerCount;
		tmpDiv = tmpDiv/playerCount;
		
		// card shuffle
		gameDeck.setAllDeck();
		
		// share card to players
		for(int i=0; i<(playerCount-tmpMod); ++i)
		{
			playersArr[i] = new PlayerImpl(tmpDiv,gameDeck.getAllDeck(),i);
		}
		for(int i=playerCount-tmpMod; i<playerCount; ++i)
		{
			playersArr[i] = new PlayerImpl(tmpDiv*(playerCount-tmpMod),tmpDiv+1,gameDeck.getAllDeck(),i-(playerCount-tmpMod), i);
		}
		
		// playerManage
		pm = new PlayerManageImpl(playersArr);
		playersHm = pm.getPlayers();
		finishPlayersHm = new HashMap<>();
		
		// init subValue
		nowCardNumber = 0;
		nowCardCount = 0;
		nowTurn = 1;
		lastTurn = 1;
		ranking = 1;
		lastTurnSubNameNumber = 1;
		ifFirstTurn = true;
		
		showPlayersDeck();
		
//		sc.close();
	}

	@Override
	public void play()
	{
		// selectCard manage array
		receiveSelectCard = new int[2];
		ifUseCardWithJoker = 0;
		withJokerCount = 0;
		usedCardWithJoker = false;
		wantToPass = false;
		
		// start game
		/*
		 * 1. exist deck?
		 * 2. first submit?
		 *  3. with Joker?
		 *  4. without Joker?
		 * 5. not first submt?
		 *  6. with Joker?
		 *  7. without Joker?
		 *  8. pass?
		 */
		while(!playersHm.isEmpty())
		{
			usedCardWithJoker = false;
			
			// ifFirstTurn == true
			if(ifFirstTurn)
			{
				while(true)
				{
					useJoker();
					
					// ifUseCardWithJoker == 1
					if(ifUseCardWithJoker == 1)
					{
						// error : if you have not Joker.
						if(!playersHm.get(nowTurn).isContainCard(13))
						{
							notEnoughJokerMsg();
							continue;
						}
						
						askWithJokerCount();
						
						if(playersHm.get(nowTurn).hasJoker(withJokerCount))
						{
							showNowTurn();
							askFirstTurn();
							selectCard();
							
							// error : if you have not card enough.
							if(!playersHm.get(nowTurn).hasCard(receiveSelectCard[0], receiveSelectCard[1]))
							{
								notCorrectInputMsg();
								continue;
							}
							
							// error : if you input incorrect value to receiveSelectCard.
							if((receiveSelectCard[0] == 0) || (receiveSelectCard[1] == 0))
							{
								notCorrectInputMsg();
								continue;
							}
							// success :  1 1 1 0 0 0 0 0
							else
							{
								usedCardWithJoker = true;
								wantToPass = false;
								break;
							}
						}
						// error : if you have not Joker enough.
						else
						{
							notEnoughJokerMsg();
							continue;
						}
					}
					// ifUseCardWithJoker == 0
					else if(ifUseCardWithJoker == 0)
					{
						withJokerCount = 0;
						
						showNowTurn();
						askFirstTurn();
						selectCard();
						
						// error : if you have not card enough.
						if(!playersHm.get(nowTurn).hasCard(receiveSelectCard[0], receiveSelectCard[1]))
						{
							notCorrectInputMsg();
							continue;
						}
						
						// error : if you input incorrect value to receiveSelectCard.
						if((receiveSelectCard[0] == 0) || (receiveSelectCard[1] == 0))
						{
							notCorrectInputMsg();
							continue;
						}
						// success :  1 1 0 1 0 0 0 0
						else
						{
							usedCardWithJoker = false;
							wantToPass = false;
							break;
						}
					}
					// error : if you input incorrect value to ifUseCardWithJoker.
					// ifUseCardWithJoker != 1 ||  ifUseCardWithJoker != 0
					else
					{
						notCorrectInputMsg();
						continue;
					}
				}
			}
			// ifFirstTurn == false
			else
			{
				useJoker();
				
				// ifUseCardWithJoker == 1
				if(ifUseCardWithJoker == 1)
				{
					// error : if you have not Joker.
					if(!playersHm.get(nowTurn).isContainCard(13))
					{
						notEnoughJokerMsg();
						continue;
					}
					
					askWithJokerCount();
					
					if(playersHm.get(nowTurn).hasJoker(withJokerCount))
					{
						showNowTurn();
						askSecondTurn();
						selectCard();
						
						// error : if you have not card enough.
						if(!playersHm.get(nowTurn).hasCard(receiveSelectCard[0], receiveSelectCard[1]))
						{
							notCorrectInputMsg();
							continue;
						}
						
						// error : if you input incorrect value to receiveSelectCard.
						if((receiveSelectCard[0] == 0) || (receiveSelectCard[1] == 0))
						{
							notCorrectInputMsg();
							continue;
						}
						else
						{
							// error : if you input incorrect value to receiveSelectCard.
							if((receiveSelectCard[0] >= nowCardNumber) && ((receiveSelectCard[1]+withJokerCount) != nowCardCount))
							{
								notCorrectInputMsg();
								continue;
							}
							// success :  1 0 0 0 1 1 0 0
							else
							{
								usedCardWithJoker = true;
								wantToPass = false;
							}
						}
					}
				}
				// ifUseCardWithJoker == 0
				else if(ifUseCardWithJoker == 0)
				{
					showNowTurn();
					askSecondTurn();
					selectCard();
					
					// error : if you have not card enough.
					if(!playersHm.get(nowTurn).hasCard(receiveSelectCard[0], receiveSelectCard[1]))
					{
						notCorrectInputMsg();
						continue;
					}
					
					// 1 0 0 0 1 0 0 0
					if((receiveSelectCard[0] == 0) && (receiveSelectCard[1] == 0))
					{
						usedCardWithJoker = false;
						wantToPass = true;						
					}
					else if((receiveSelectCard[0] > 0) && (receiveSelectCard[1] > 0))
					{
						// error : if you input incorrect value to receiveSelectCard.
						if((receiveSelectCard[0] >= nowCardNumber) || (receiveSelectCard[1] != nowCardCount))
						{
							notCorrectInputMsg();
							continue;
						}
						// 1 0 0 0 1 0 1 0
						else
						{
							usedCardWithJoker = false;
							wantToPass = false;
						}
					}
					// error : if you input incorrect value to receiveSelectCard.
					else
					{
						notCorrectInputMsg();
						continue;
					}
				}
				// error : if you input incorrect value to ifUseCardWithJoker.
				// ifUseCardWithJoker != 1 ||  ifUseCardWithJoker != 0
				else
				{
					notCorrectInputMsg();
					continue;
				}
			}
			
			/* Common Place Start***********************************************************************************************************************/
			
			if(!wantToPass)
			{
				lastTurn=nowTurn;
				lastTurnSubNameNumber = playersHm.get(lastTurn).getSubNameNumber();
				nowCardNumber = receiveSelectCard[0];
				
				if(ifFirstTurn)
				{
					nowCardCount = receiveSelectCard[1];
					ifFirstTurn = false;
				}
				
				if(usedCardWithJoker)
				{
					playersHm.get(nowTurn).submitCardWithJoker(withJokerCount);
					nowCardCount = receiveSelectCard[1] + withJokerCount;
					usedCardWithJoker = false;
				}
				
				playersHm.get(nowTurn).submitCard(receiveSelectCard);
				
				if(playersHm.get(nowTurn).isEmpty())
				{
					playersHm.get(nowTurn).setPrivilege(ranking);
					if(ranking == 1)
					{
						playersHm.get(nowTurn).setWin();
					}
					else
					{
						playersHm.get(nowTurn).setLose();
					}
					finishPlayersHm.put(ranking++, playersHm.get(nowTurn));
					playersHm.remove(nowTurn);
				}
			}
			
			nowTurn = nowTurn%playerCount+1;
			while((!playersHm.isEmpty())&&(!playersHm.containsKey(nowTurn)))
			{
				nowTurn = nowTurn%playerCount+1;
			}
			
			if(lastTurn == nowTurn)
			{
				nowCardNumber = 0;
				nowCardCount = 0;
				ifFirstTurn = true;
			}
			
			if(!playersHm.containsKey(lastTurn))
			{
				lastTurn = lastTurn%playerCount+1;
				while((!playersHm.isEmpty())&&(!playersHm.containsKey(lastTurn)))
				{
					lastTurn = lastTurn%playerCount+1;
				}
			}
			/* Common Place End***********************************************************************************************************************/
		}
		
		Iterator<Integer> finishIt = finishPlayersHm.keySet().iterator();
		while(finishIt.hasNext())
		{
			Integer keyTmp = (Integer)finishIt.next();
			playersHm.put(keyTmp, finishPlayersHm.get(keyTmp));
		}
		finishPlayersHm.clear();
//		sc.close();
	}

	@Override
	public void showNowTurn()
	{
		System.out.print("\n[Player"+playersHm.get(nowTurn).getSubNameNumber()+"] : ");
		playersHm.get(nowTurn).showhDeck();
		System.out.println("[now Card] : "+nowCardNumber+"("+nowCardCount+") - "+"[Player"+lastTurnSubNameNumber+"]'s card.");
	}
	
	@Override
	public void showPlayersDeck()
	{
		Iterator<Integer> playersHmIt = playersHm.keySet().iterator();
		while(playersHmIt.hasNext())
		{
			Integer playersHmItNext = (Integer)playersHmIt.next();
			System.out.print("[Player"+playersHm.get(playersHmItNext).getSubNameNumber()+"] : ");
			playersHm.get(playersHmItNext).showhDeck();
		}
	}
	
	@Override
	public void askFirstTurn()
	{
		System.out.println("choice your card for submit(card number, card count) like 5 6.");
		if(usedCardWithJoker)
		{
			System.out.println("<select "+withJokerCount+" Joker>.");
		}
	}

	@Override
	public void askSecondTurn()
	{
		System.out.println("choice your card for submit(card number, card count) like 5 6. if you want to pass, choice card like 0 0.");
		if(usedCardWithJoker)
		{
			System.out.println("<select "+withJokerCount+" Joker>.");
		}
	}
	
	@Override
	public void useJoker()
	{
		while(true)
		{
			showNowTurn();
			System.out.println("if you want to use Joker, input 1. if you not, input 0.");
			System.out.println("If you want to show players deck, input 2.");
			try {
				ifUseCardWithJoker = sc.nextInt();
				if(ifUseCardWithJoker == 2)
				{
					showPlayersDeck();
					continue;
				}
				if(ifUseCardWithJoker<0 || ifUseCardWithJoker > 1)
				{
					notCorrectInputMsg();
					continue;
				}
				else
				{
					if(ifUseCardWithJoker == 1)
					{
						usedCardWithJoker = true;
					}
					break;
				}
			}catch(InputMismatchException e) {
				sc = new Scanner(System.in);
				notCorrectInputMsg();
			}
		}
	}
	
	@Override
	public void askWithJokerCount()
	{
		while(true)
		{
			System.out.println("input count how many use Joker with other card.");
			try {
				withJokerCount = sc.nextInt();
				if(withJokerCount<0 || withJokerCount > 2)
				{
					notCorrectInputMsg();
					continue;
				}
				else
				{
					break;
				}
			}catch(InputMismatchException e) {
				sc = new Scanner(System.in);
				notCorrectInputMsg();
			}
		}
	}
	
	@Override
	public void selectCard()
	{
		while(true)
		{
			try {
				receiveSelectCard[0] = sc.nextInt();
				receiveSelectCard[1] = sc.nextInt();
				if((receiveSelectCard[0]<0) || (receiveSelectCard[0]>13) || 
						(receiveSelectCard[1]>receiveSelectCard[0]) || (receiveSelectCard[1]<0))
				{
					notCorrectInputMsg();
					continue;
				}
				else
				{
					break;
				}
			}catch(InputMismatchException e) {
				sc = new Scanner(System.in);
				notCorrectInputMsg();
			}
		}
	}

	@Override
	public void notEnoughJokerMsg()
	{
		System.out.println("you have not Joker enough!!!");
	}

	@Override
	public void notCorrectInputMsg()
	{
		System.out.println("input correct value!!!");
	}
}
