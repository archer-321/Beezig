package tk.roccodev.zta.modules.cai;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import tk.roccodev.zta.ActiveGame;
import tk.roccodev.zta.IHive;
import tk.roccodev.zta.games.CAI;

public class MapItem extends GameModeItem<CAI>{

		public MapItem(){
			super(CAI.class);
		}


		@Override
		protected Object getValue(boolean dummy) {
			try{
				if(CAI.activeMap == null) return "No Map";
				return CAI.activeMap;
			}catch(Exception e){
				e.printStackTrace();
				return "No Map";
			}
		}

		@Override
		public String getName() {
			return "Map";
		}



		@Override
		public boolean shouldRender(boolean dummy){
			try{
				return dummy || (The5zigAPI.getAPI().getActiveServer() instanceof IHive && ActiveGame.is("CAI") && CAI.activeMap != null);
			}catch(Exception e){
				return false;
			}
		}

}