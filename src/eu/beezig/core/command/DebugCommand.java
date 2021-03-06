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

import eu.beezig.core.utils.ws.Connector;
import eu.beezig.core.utils.ws.api.PacketOpcodes;
import eu.the5zig.mod.The5zigAPI;
import org.json.simple.JSONObject;

public class DebugCommand implements Command {
    public static boolean go = false;

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "bdev";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"/bdev"};
    }


    @Override
    public boolean execute(String[] args) {
        //some debug code here v

        JSONObject packet = new JSONObject();
        packet.put("opcode", PacketOpcodes.S_REQUEST_ONLINE_USERS);

        new Thread(() -> Connector.client.sendJson(packet)).start();

        The5zigAPI.getAPI().getRenderHelper().drawLargeText("Test");

        return true;

    }
}

