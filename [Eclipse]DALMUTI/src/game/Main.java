package game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	
	static Scanner sc;
	
	public static void showMenu()
	{
		System.out.println("*------------*");
		System.out.println("1. New Game");
		System.out.println("2. Help");
		System.out.println("3. Exit");
		System.out.println("*------------*");
		System.out.print("What is your choice? ");
	}

	public static void notCorrectChoiceMsg()
	{
		System.out.println("Incorrect choice");
	}
	
	
	public static void showHelp()
	{
		System.out.println("*----------------------------------------------------------------------------------------*");
		System.out.println("This is help message.");
		System.out.println();
		System.out.println("-----Notice");
		System.out.println("1. Tax is not exist.");
		System.out.println("2. Cannot save game.");
		System.out.println("3. Can use Joker 1 or 2 with other card.");
		System.out.println("4. If you want to exit, press Ctrl+C.");
		System.out.println();
		System.out.println("-----How to play");
		System.out.println("1. Select New game.");
		System.out.println("2. Input number of players");
		System.out.println("3. Game start in order of privilege.");
		System.out.println("4. You can decide to use Joker first.");
		System.out.println("          4.1. If you want to use Joker, input 1. But, If you not, input 0.");
		System.out.println("                    4.1.1. If you input 1, you can input number of Joker cards.");
		System.out.println("5. You can input 2 number, card number and number of cards.");
		System.out.println("          5.1. If you want to use 1 Dalmuti(1), input 1 1.");
		System.out.println("          5.2. If you want to use 5 something(7), input 7 5.");
		System.out.println("          5.3. However, If you input 1 for using 1 or 2 Joker card before, (5.2)'s result 7 4 or 7 3.");
		System.out.println("                    5.3.1. Substract number of Joker from number of need card.");
		System.out.println("*----------------------------------------------------------------------------------------*");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		sc = new Scanner(System.in);
		
		Game game;
		int selectMenu;
		
		while(true)
		{
			while(true)
			{
				System.out.println("Choice Menu");
				showMenu();
				try {
					selectMenu = sc.nextInt();
					if(selectMenu<1 || selectMenu > 3)
					{
						notCorrectChoiceMsg();
						continue;
					}
					else
					{
						break;
					}
				}catch(InputMismatchException e) {
					sc = new Scanner(System.in);
					notCorrectChoiceMsg();
				}
			}
			
			switch(selectMenu)
			{
			case 1:
				game = new GameImpl();
				System.out.println("@@finish game setting");
				game.play();
				System.out.println("@@finish game");
				break;
			case 2:
				showHelp();
				break;
			case 3:
				System.out.println("Exit program.");
				System.exit(0);
				break;
			default:
				break;
			}
		}
	}

}
