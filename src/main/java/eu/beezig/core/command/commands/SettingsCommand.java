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

package eu.beezig.core.command.commands;

import eu.beezig.core.Beezig;
import eu.beezig.core.command.Command;
import eu.beezig.core.config.Settings;
import eu.beezig.core.util.Color;
import eu.beezig.core.util.Message;

import java.io.IOException;
import java.util.Locale;

public class SettingsCommand implements Command {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"/bsettings"};
    }

    @Override
    public boolean execute(String[] args) {
        Settings setting;
        if(args.length == 0) {
            Message.info(Message.translate("msg.config.list"));
            for(Settings key : Settings.values()) {
                Beezig.api().messagePlayer(String.format("%s- %s%s §7[%s] %s(%s): §a%s",
                        Color.primary(), Color.accent(), key.getName(), key.name().toLowerCase(Locale.ROOT),
                        Color.primary(), key.getDescription(), key.get().toString()));
            }
        }
        else if(args.length == 1) {
            if((setting = getSetting(args[0])) == null) return true;
            Message.info(String.format("%s%s %s(%s):%s %s", Color.accent(), setting.getName(), Color.primary(),
                    setting.getDescription(), Color.primary(), setting.get().getString()));
        }
        else if(args.length == 2) {
            if((setting = getSetting(args[0])) == null) return true;
            if(!Beezig.cfg().set(setting, args[1])) return true;
            try {
                Beezig.cfg().save();
                Message.info(Message.translate("msg.config.save"));
            } catch (IOException e) {
                Message.error(Message.translate("error.data_read"));
                e.printStackTrace();
            }
        }
        else {
            sendUsage("/bsettings (setting_id) (value)");
        }
        return true;
    }

    private Settings getSetting(String name) {
        try {
            return Settings.valueOf(name.toUpperCase(Locale.ROOT).replace(".", "_"));
        }
        catch (IllegalArgumentException ex) {
            Message.error(Message.translate("error.setting_not_found"));
            return null;
        }
    }
}
