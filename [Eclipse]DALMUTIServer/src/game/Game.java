package game;

public interface Game {

	void play();
	void showNowTurn();
	void showPlayersDeck();
	void askFirstTurn();
	void askSecondTurn();
	void useJoker();
	void askWithJokerCount();
	void selectCard();
	void notEnoughJokerMsg();
	void notCorrectInputMsg();

}