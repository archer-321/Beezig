package tk.roccodev.beezig.modules.dr;

import eu.the5zig.mod.modules.GameModeItem;
import tk.roccodev.beezig.Log;
import tk.roccodev.beezig.games.DR;

public class DeathsItem extends GameModeItem<DR>{

	public DeathsItem(){
		super(DR.class);
	}

	@Override
	protected Object getValue(boolean dummy) {
		return DR.deaths;
	}
	
	@Override
	public String getName() {
		return Log.t("beezig.module.deaths");
	}
	@Override
	public boolean shouldRender(boolean dummy){		
		try{
			if(!(getGameMode() instanceof DR)) return false;
			return dummy || (DR.shouldRender(getGameMode().getState()) && DR.role.equals("Runner") && DR.deaths != 0);
		}
		catch(Exception e){
			return false;
		}
	}

}