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

package eu.beezig.core.modules.lab;

import eu.beezig.core.Log;
import eu.beezig.core.games.LAB;
import eu.beezig.core.hiveapi.APIValues;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.util.minecraft.ChatColor;

public class PointsItem extends GameModeItem<LAB> {

    public PointsItem() {
        super(LAB.class);
    }

    private String getMainFormatting() {
        if (this.getProperties().getFormatting() != null) {
            if (this.getProperties().getFormatting().getMainColor() != null
                    && this.getProperties().getFormatting().getMainFormatting() == null) {
                return The5zigAPI.getAPI().getFormatting().getMainFormatting().replace(
                        (The5zigAPI.getAPI().getFormatting().getMainFormatting()).charAt(1),
                        this.getProperties().getFormatting().getMainColor().toString().charAt(1));
                // Replaces Char at index 1 (ColorTag) of the Main formatting with the custom
                // one.
            }
            if (this.getProperties().getFormatting().getMainColor() == null
                    && this.getProperties().getFormatting().getMainFormatting() != null) {
                return The5zigAPI.getAPI().getFormatting().getMainFormatting().replace(
                        (The5zigAPI.getAPI().getFormatting().getMainFormatting()).charAt(3),
                        this.getProperties().getFormatting().getMainFormatting().toString().charAt(3));
                // Replaces Char at index 3 (FormattingTag) of the Main formatting with the
                // custom one.
            }
            if (this.getProperties().getFormatting().getMainColor() != null
                    && this.getProperties().getFormatting().getMainFormatting() != null) {
                return this.getProperties().getFormatting().getMainColor() + ""
                        + this.getProperties().getFormatting().getMainFormatting();
            }
        }
        return The5zigAPI.getAPI().getFormatting().getMainFormatting();
    }

    @Override
    protected Object getValue(boolean dummy) {
        try {
            if ((boolean) getProperties().getSetting("showrank").get()) {
                StringBuilder sb = new StringBuilder();
                if ((boolean) getProperties().getSetting("showcolor").get()) {
                    sb.append(Log.df(APIValues.LABpoints)).append(" (").append(LAB.rank).append(getMainFormatting());

                } else {

                    sb.append(Log.df(APIValues.LABpoints)).append(" (").append(ChatColor.stripColor(LAB.rank));
                }

                if ((boolean) getProperties().getSetting("showpointstonextrank").get()) {
                    if (LAB.rankObject == null)
                        return Log.df(APIValues.LABpoints);
                    sb.append((boolean) getProperties().getSetting("showcolor").get()
                            ? " / " + LAB.rankObject.getPointsToNextRank((int) APIValues.LABpoints)
                            : " / " + ChatColor
                            .stripColor(LAB.rankObject.getPointsToNextRank((int) APIValues.LABpoints)));

                }
                sb.append(

                        (boolean) getProperties().getSetting("showcolor").get() ?

                                getMainFormatting() + ")" : ")");
                return sb.toString().trim();
            }
            return Log.df(APIValues.LABpoints);
        } catch (Exception e) {
            e.printStackTrace();
            return "Server error";
        }
    }

    @Override
    public String getTranslation() {
        return "beezig.module.lab.atoms";
    }

    @Override
    public void registerSettings() {
        getProperties().addSetting("showrank", false);
        getProperties().addSetting("showcolor", true);
        getProperties().addSetting("showpointstonextrank", false);
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        try {
            if (getGameMode() == null)
                return false;
            return dummy || LAB.shouldRender(getGameMode().getState());
        } catch (Exception e) {
            return false;
        }
    }

}
