/*
 * Copyright (C) 2019 Beezig (RoccoDev, ItsNiklass)
 *
 * This file is part of Beezig.
 *
 * Beezig is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Beezig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Beezig.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.beezig.core.command;

import eu.beezig.core.IHive;
import eu.beezig.core.Log;
import eu.beezig.core.hiveapi.wrapper.APIUtils;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import pw.roccodev.beezig.hiveapi.wrapper.player.HivePlayer;

import java.util.Calendar;

public class SeenCommand implements Command {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "seen";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"/seen"};
    }

    @Override
    public boolean execute(String[] args) {
        if (!(The5zigAPI.getAPI().getActiveServer() instanceof IHive)) return false;
        if (args.length == 1) {

            String ign = args[0];
            HivePlayer api = new HivePlayer(ign);
            new Thread(() -> {
                if (api.getStatus().isOnline()) {
                    The5zigAPI.getAPI().messagePlayer(Log.info + ChatColor.AQUA + api.getUsername() + "§3 is online and in §b" + api.getStatus().getGame());
                } else {
                    Calendar lastSeen = Calendar.getInstance();
                    lastSeen.setTimeInMillis(api.getLastLogout().getTime());

                    String minute = Integer.toString(lastSeen.get(Calendar.MINUTE));
                    if (lastSeen.get(Calendar.MINUTE) < 10) {
                        minute = "0" + minute;
                    }
                    String hour = Integer.toString(lastSeen.get(Calendar.HOUR_OF_DAY));
                    if (lastSeen.get(Calendar.HOUR_OF_DAY) < 10) {
                        hour = "0" + hour;
                    }
                    // Never again
                    The5zigAPI.getAPI().messagePlayer(Log.info + ChatColor.AQUA + api.getUsername() + "§3 was last seen on §b" + lastSeen.get(Calendar.DAY_OF_MONTH) + "." + (lastSeen.get(Calendar.MONTH) + 1) + "." + lastSeen.get(Calendar.YEAR) + " " + hour + ":" + minute
                            + "§b (§b" + APIUtils.getTimeAgo(lastSeen.getTimeInMillis()) + ".§b)");
                }
            }).start();

        } else {
            The5zigAPI.getAPI().messagePlayer(Log.info + "Usage: /seen [player]");
        }

        return true;
    }


}
