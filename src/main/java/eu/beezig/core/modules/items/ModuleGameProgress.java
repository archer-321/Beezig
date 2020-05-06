/*
 * Copyright (C) 2019 Beezig Team
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

package eu.beezig.core.modules.items;

import eu.beezig.core.modules.Modules;
import eu.beezig.core.server.HiveMode;
import eu.beezig.core.server.modes.TIMV;
import eu.beezig.core.util.Message;
import eu.the5zig.mod.modules.GameModeItem;

public class ModuleGameProgress extends GameModeItem<HiveMode> {
    public ModuleGameProgress() {
        super(HiveMode.class);
    }

    @Override
    protected Object getValue(boolean b) {
        if(b) return "123 Points";
        return String.format("%s %s", getGameMode().getPoints(), Message.translate(getGameMode() instanceof TIMV
                ? "beezig.module.timv.karma"
                : "modules.item.hive_points"));
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return dummy || Modules.render() && (getGameMode() != null && getGameMode().getPoints() != 0);
    }

    @Override
    public String getTranslation() {
        return "beezig.module.game";
    }
}