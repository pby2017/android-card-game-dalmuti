package game;

import java.util.HashMap;

public class PlayerManageImpl {

	HashMap<Integer, GamePlayerImpl> players;
	
	public PlayerManageImpl()
	{
		
	}
	
	public PlayerManageImpl(final GamePlayerImpl[] players)
	{
		int playersLength = players.length;
		this.players = new HashMap<>();
		for(int i=0; i<playersLength; ++i)
		{
			this.players.put(players[i].getPrivilege(), players[i]);
		}
	}

	public HashMap<Integer, GamePlayerImpl> getPlayers()
	{
		return this.players;
	}
}
