package eu.beezig.core.command;

import eu.beezig.core.utils.ws.Connector;
import eu.the5zig.mod.The5zigAPI;

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

        new Thread(() -> Connector.client.send("Hello there pls get online people")).start();

        The5zigAPI.getAPI().getRenderHelper().drawLargeText("Test");

        return true;

    }
}
