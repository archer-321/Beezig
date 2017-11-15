package tk.roccodev.zta.hiveapi.wrapper.modes;

import eu.the5zig.mod.server.GameMode;
import tk.roccodev.zta.games.CAI;
import tk.roccodev.zta.hiveapi.wrapper.PvPMode;

public class ApiCAI extends PvPMode {

	public ApiCAI(String playerName) {
		super(playerName);
		
	}

	
	
	@Override
	public Class<? extends GameMode> getGameMode() {
		// TODO Auto-generated method stub
		return CAI.class;
	}

	@Override
	public String getShortcode() {
		// TODO Auto-generated method stub
		return "CAI";
	}



	@Override
	public long getKills() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getDeaths() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public long getCaptures() {
		return (long) object("captures");
	}
	
	
	public long getCaptured() {
		return (long) object("captured");
	}
	
	public long getCatches() {
		return (long) object("catches");
	}
	
	public long getCaught() {
		return (long) object("caught");
	}

	
	
	
	
	

}
