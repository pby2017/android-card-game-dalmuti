package game;

import java.util.HashMap;

public class PlayerManageImpl implements PlayerManage {

	HashMap<Integer, PlayerImpl> players;
	
	public PlayerManageImpl()
	{
		
	}
	
	public PlayerManageImpl(final PlayerImpl[] players)
	{
		int playersLength = players.length;
		this.players = new HashMap<>();
		for(int i=0; i<playersLength; ++i)
		{
			this.players.put(players[i].getPrivilege(), players[i]);
		}
	}

	@Override
	public HashMap<Integer, PlayerImpl> getPlayers()
	{
		return this.players;
	}
}
