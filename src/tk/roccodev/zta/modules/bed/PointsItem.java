package tk.roccodev.zta.modules.bed;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.util.minecraft.ChatColor;
import tk.roccodev.zta.games.BED;
import tk.roccodev.zta.hiveapi.HiveAPI;

public class PointsItem extends GameModeItem<BED>{

	public PointsItem(){
		super(BED.class);
	}
	
	private String getMainFormatting(){
		if(this.getProperties().getFormatting() != null){
			if(this.getProperties().getFormatting().getMainColor() != null && this.getProperties().getFormatting().getMainFormatting() == null){
				return The5zigAPI.getAPI().getFormatting().getMainFormatting().toString().replace((The5zigAPI.getAPI().getFormatting().getMainFormatting().toString()).charAt(1), this.getProperties().getFormatting().getMainColor().toString().charAt(1));
				//Replaces Char at index 1 (ColorTag) of the Main formatting with the custom one.
			}
			if(this.getProperties().getFormatting().getMainColor() == null && this.getProperties().getFormatting().getMainFormatting() != null){
				return The5zigAPI.getAPI().getFormatting().getMainFormatting().toString().replace((The5zigAPI.getAPI().getFormatting().getMainFormatting().toString()).charAt(3), this.getProperties().getFormatting().getMainFormatting().toString().charAt(3));
				//Replaces Char at index 3 (FormattingTag) of the Main formatting with the custom one.
			}
			if(this.getProperties().getFormatting().getMainColor() != null && this.getProperties().getFormatting().getMainFormatting() != null){
				return this.getProperties().getFormatting().getMainColor() +""+ this.getProperties().getFormatting().getMainFormatting();
			}
		}
		return The5zigAPI.getAPI().getFormatting().getMainFormatting();	
	}
	
	@Override
	protected Object getValue(boolean dummy) {
		try{
			if((boolean) getProperties().getSetting("showrank").get()){
				StringBuilder sb = new StringBuilder();
				if((boolean) getProperties().getSetting("showcolor").get()){
					sb.append(HiveAPI.BEDpoints + " (" + BED.rank + getMainFormatting());
					
				}else{
				
					sb.append(HiveAPI.BEDpoints + " (" + ChatColor.stripColor(BED.rank));
				}
				
				if((boolean)getProperties().getSetting("showpointstonextrank").get()){
					if(BED.rankObject == null) return HiveAPI.BEDpoints;
					sb.append((boolean)getProperties().getSetting("showcolor").get() ? " / " + BED.rankObject.getPointsToNextRank((int)HiveAPI.BEDpoints) : " / " + ChatColor.stripColor(BED.rankObject.getPointsToNextRank((int)HiveAPI.BEDpoints)));
						
				}
				sb.append(
						
						(boolean)getProperties().getSetting("showcolor").get() ?
						
								getMainFormatting() + ")" :
						")");
				return sb.toString().trim();
				}
			return HiveAPI.BEDpoints;
		}catch(Exception e){
			e.printStackTrace();
			return "Server error";
		}
	}
	
	@Override
	public String getName() {
		return "Points";
	}
	
	@Override
	public void registerSettings() {
		getProperties().addSetting("showrank", false);
		getProperties().addSetting("showcolor", true);
		getProperties().addSetting("showpointstonextrank", false);
	}
	
	@Override
	public boolean shouldRender(boolean dummy){		
		try{
			if(!(getGameMode() instanceof BED)) return false;
		return dummy || (BED.shouldRender(getGameMode().getState()));
		}catch(Exception e){
			return false;
		}
	}

}