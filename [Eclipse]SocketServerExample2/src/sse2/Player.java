package sse2;

public interface Player {

	void submitCard(int[] submitCard);
	void submitCardWithJoker(int withJokerCount);
	void showhDeck();
	boolean isEmpty();
	boolean isContainCard(int cardNumber);
	boolean hasJoker(int withJokerCount);
	boolean hasCard(int cardNumber, int cardCount);
	void setPrivilege(int privilege);
	int getPrivilege();
	int getSubNameNumber();
	void setWin();
	int getWin();
	void setLose();
	int getLose();
	
}