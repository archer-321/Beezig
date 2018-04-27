package tk.roccodev.zta.listener;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;
import tk.roccodev.zta.games.GNT;
import tk.roccodev.zta.games.GNTM;
import tk.roccodev.zta.games.TIMV;
import tk.roccodev.zta.hiveapi.HiveAPI;
import tk.roccodev.zta.utils.rpc.DiscordUtils;

public class HiveListener extends AbstractGameListener<GameMode>{

	@Override
	public Class<GameMode> getGameMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matchLobby(String lobby) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGameModeJoin(GameMode gameMode){
		
		
	}
	
	
	
	@Override
	public boolean onServerChat(GameMode gameMode, String message) {
		if(message.contains("§eGold Medal Awarded!")){
			HiveAPI.medals++;
		}
		else if(message != null && ChatColor.stripColor(message).contains("▍ Tokens ▏ You earned")){
			
			String[] data = ChatColor.stripColor(message).replaceAll("▍ Tokens ▏ You earned", "").split("tokens");
			
			int tokens = Integer.parseInt(data[0].trim());
			
			HiveAPI.tokens += tokens;
			
		}
		else if(message != null && message.contains("§b EXTRA tokens this round!")){
			//§8▍ §3§lBed§b§lWars§8 ▏ §bThanks to the §dultimate§b member §dGryffin§b you gained §a25§b EXTRA tokens this round!
			//idk if thats how it works
			HiveAPI.tokens += Integer.parseInt(ChatColor.stripColor(message.split("EXTRA")[0].split("you gained ")[1].trim()));
		}
		return false;
	}

	@Override
    public void onMatch(GameMode gameMode, String key, IPatternResult match) {
		 if (gameMode != null && gameMode.getState() != GameState.FINISHED) {
	            return;
	        }
		 
		 if(key.equals(TIMV.joinMessage)){
				getGameListener().switchLobby("TIMV");
				
				The5zigAPI.getLogger().info("Connected to TIMV! -Hive");
				DiscordUtils.updatePresence("Investigating in Trouble in Mineville");
			}
		 
		if(key.equals("timv.welcome")){
			getGameListener().switchLobby("TIMV");
			
			The5zigAPI.getLogger().info("Connected to TIMV! -Hive");
			DiscordUtils.updatePresence("Investigating in Trouble in Mineville");
		}
		
		else if(key.equals("dr.welcome")){
			getGameListener().switchLobby("DR");
			
			The5zigAPI.getLogger().info("Connected to DR! -Hive");
			DiscordUtils.updatePresence("Running in DeathRun");
		}
		else if(key.equals("bed.welcome") || (key.equals("bed.spectator") && gameMode == null)){
			getGameListener().switchLobby("BED");
			
			The5zigAPI.getLogger().info("Connected to BED/BEDT! -Hive");
			DiscordUtils.updatePresence("Housekeeping in BedWars");
		}
		else if(key.equals("gntm.welcome")){
			
			
			The5zigAPI.getLogger().info("Connected to GNTM! -Hive");
			DiscordUtils.updatePresence("Slaying Giants in SkyGiants:Mini");
			
			
			GiantListener.listener.setGameMode(GNTM.class, GNTM.instance);
			The5zigAPI.getLogger().info(GNTM.instance.getClass());
			getGameListener().switchLobby("GNTM");
		}
		else if(key.equals("gnt.welcome")){
			
			
			The5zigAPI.getLogger().info("Connected to GNT! -Hive");
			DiscordUtils.updatePresence("Slaying Giants in SkyGiants");
			The5zigAPI.getLogger().info(GNT.instance.getClass());
			
			GiantListener.listener.setGameMode(GNT.class, GNT.instance);
			
			getGameListener().switchLobby("GNT");
		}
		else if(key.equals("hide.welcome")){		
			getGameListener().switchLobby("HIDE");			
			The5zigAPI.getLogger().info("Connected to HIDE! -Hive");
			DiscordUtils.updatePresence("Seeking in Hide&Seek");
		}
		else if(key.equals("cai.welcome")){		
			getGameListener().switchLobby("CAI");			
			The5zigAPI.getLogger().info("Connected to CAI! -Hive");
			DiscordUtils.updatePresence("Battling in Cowboys and Indians");
		}
		
		
	}

	

}
