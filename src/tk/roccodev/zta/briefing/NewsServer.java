package tk.roccodev.zta.briefing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import tk.roccodev.zta.Log;
import tk.roccodev.zta.briefing.lergin.NewMap;
import tk.roccodev.zta.briefing.lergin.StaffChangeType;
import tk.roccodev.zta.briefing.lergin.StaffUpdate;
import tk.roccodev.zta.settings.Setting;

public class NewsServer {

	public static void serveNews(ArrayList<News> news, ArrayList<NewMap> maps, ArrayList<StaffUpdate> staff) {
		if (!Setting.BRIEFING.getValue()) {
			
			System.out.println("Briefing is disabled.");
			
			return;
		}
			
		
		if(Pools.error && news.size() == 0) {
			The5zigAPI.getAPI().messagePlayer(Log.error + "An error has occurred while attempting to load your Briefing. This may be caused by Minecraft using the wrong Java installation. Please follow this guide: https://github.com/RoccoDev/5zig-TIMV-Plugin/wiki/Fixing-the-Issue-with-WR-for-Deathrun");
			return;
		}
		else if(Pools.error) {
			The5zigAPI.getAPI().messagePlayer(Log.error + "An error has occurred while attempting to load your Briefing. This may be caused by Minecraft using the wrong Java installation. Please follow this guide: https://github.com/RoccoDev/5zig-TIMV-Plugin/wiki/Fixing-the-Issue-with-WR-for-Deathrun");
			The5zigAPI.getAPI().messagePlayer(Log.info + "However, we're still able to serve you our latest news!");
			
		}
		
		if (news.size() == 0 && maps.size() == 0 && staff.size() == 0)
			return;
		

		The5zigAPI.getAPI().messagePlayer(
				"§f                     §b§m                  §f §f§lBeezig Briefing§f §b§m                  "
						+ (news.size() == 0 ? "" : "- " + "\n\n§f - " + ChatColor.ITALIC + "Our news:"));
		if (news.size() != 0) {
			for (News n : news) {
				The5zigAPI.getAPI().messagePlayer("\n§e" + ChatColor.UNDERLINE + n.getTitle());
				The5zigAPI.getAPI().messagePlayer("§e" + n.getContent());

			}
		}
		if (maps.size() != 0) {

			The5zigAPI.getAPI().messagePlayer("\n - " + ChatColor.ITALIC + "New maps:");
			StringBuilder sb = new StringBuilder();
			HashMap<String, ArrayList<NewMap>> grouped = new HashMap<String, ArrayList<NewMap>>();
			for (NewMap m : maps) {

				if (!grouped.containsKey(m.getGameMode())) {
					grouped.put(m.getGameMode(), new ArrayList<NewMap>(Arrays.asList(m)));
				} else {
					ArrayList<NewMap> tmp = grouped.get(m.getGameMode());
					tmp.add(m);
					grouped.put(m.getGameMode(), tmp);
				}

			}
			for (Map.Entry<String, ArrayList<NewMap>> e : grouped.entrySet()) {
				sb.append("§a" + e.getKey() + ":§e ");
				for (NewMap m : e.getValue()) {
					sb.append(m.getName() + ", ");
				}
				sb.trimToSize();
				sb.deleteCharAt(sb.length() - 2);
				sb.deleteCharAt(sb.length() - 1);
				sb.append(".");
				sb.append("\n");
			}

			The5zigAPI.getAPI().messagePlayer("§e" + sb.toString().trim());

		}
		if (staff.size() != 0) {
			The5zigAPI.getAPI().messagePlayer("\n - " + ChatColor.ITALIC + "Staff changes:");
			StringBuilder sb = new StringBuilder();
			HashMap<StaffChangeType, ArrayList<StaffUpdate>> grouped = new HashMap<StaffChangeType, ArrayList<StaffUpdate>>();
			for (StaffUpdate s : staff) {

				if (!grouped.containsKey(s.getType())) {

					grouped.put(s.getType(), new ArrayList<StaffUpdate>(Arrays.asList(s)));

				} else {
					ArrayList<StaffUpdate> tmp = grouped.get(s.getType());
					tmp.add(s);
					grouped.put(s.getType(), tmp);

				}

			}

			for (Map.Entry<StaffChangeType, ArrayList<StaffUpdate>> e : grouped.entrySet()) {
				sb.append(e.getKey().getDisplay() + ":§e ");
				for (StaffUpdate s : e.getValue()) {
					sb.append(s.getStaffName() + ", ");
				}
				sb.trimToSize();
				sb.deleteCharAt(sb.length() - 2);
				sb.deleteCharAt(sb.length() - 1);
				sb.append(".");
				sb.append("\n");
			}

			The5zigAPI.getAPI().messagePlayer("§e" + sb.toString().trim());

		}
		The5zigAPI.getAPI().messagePlayer(
				"\n§f     §b§m            §f §7Map & staff data gently provided by Lergin§f §b§m            ");
		The5zigAPI.getAPI().messagePlayer(
				"§f       §b§m                                  §f §7hive.lergin.de§f §b§m                                   ");
	}

}