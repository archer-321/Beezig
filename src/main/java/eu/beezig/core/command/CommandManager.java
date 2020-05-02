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
package eu.beezig.core.command;

import eu.beezig.core.Beezig;
import eu.beezig.core.command.commands.BeezigCommand;
import eu.beezig.core.util.Message;
import eu.the5zig.mod.event.ChatSendEvent;
import eu.the5zig.mod.event.EventHandler;

import java.util.*;

public class CommandManager {

    private static Set<Command> commandExecutors = new HashSet<>();

    private static void registerCommands() {
        commandExecutors.add(new BeezigCommand());
    }

    @EventHandler
    public void onClientChat(ChatSendEvent event) {
        String message = event.getMessage();
        if(!message.startsWith("/")) return;
        event.setCancelled(dispatchCommand(message));
    }

    public static void init(Beezig plugin) {
        registerCommands();
        Beezig.api().getPluginManager().registerListener(plugin, new CommandManager());
    }

    /**
     * Dispatches a command.
     *
     * @return whether the command was found
     */
    private static boolean dispatchCommand(String str) {
        String[] data = str.split(" ");
        String alias = data[0];
        Command cmdFound = null;
        for (Command cmd : commandExecutors) {
            for (String s : cmd.getAliases()) {
                if (s.equalsIgnoreCase(alias)) {
                    cmdFound = cmd;
                    break;
                }
            }
        }
        if (cmdFound == null) return false;

        List<String> dataList = new ArrayList<>(Arrays.asList(data));
        dataList.remove(0); // Remove alias

        try {
            if (!cmdFound.execute(dataList.toArray(new String[0]))) {
                return false; // Skip the command
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message.error("An error occurred while attempting to perform this command.");
        }
        return true;
    }
}
