package tk.roccodev.beezig.hiveapi.wrapper.modes;

import eu.the5zig.mod.server.GameMode;
import tk.roccodev.beezig.games.SKY;
import tk.roccodev.beezig.hiveapi.wrapper.PvPMode;

public class ApiSKY extends PvPMode {

    public ApiSKY(String playerName, String... UUID) {
        super(playerName, UUID);

    }


    @Override
    public Class<? extends GameMode> getGameMode() {
        // TODO Auto-generated method stub
        return SKY.class;
    }

    @Override
    public String getShortcode() {
        // TODO Auto-generated method stub
        return "SKY";
    }


}