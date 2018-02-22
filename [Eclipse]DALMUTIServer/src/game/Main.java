package game;

public class Main {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Main().start();
	}
	
	public void start()
	{
		new ServerImpl().start();
	}
}
