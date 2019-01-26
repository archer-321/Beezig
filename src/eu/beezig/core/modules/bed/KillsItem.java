package eu.beezig.core.modules.bed;

import eu.beezig.core.games.BED;
import eu.the5zig.mod.modules.GameModeItem;

public class KillsItem extends GameModeItem<BED> {

    public KillsItem() {
        super(BED.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        try {
            return BED.kills;
        } catch (Exception e) {
            e.printStackTrace();
            return "Server error";
        }
    }

    @Override
    public String getTranslation() {
        return "beezig.module.kills";
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        try {
            if (getGameMode() == null) return false;
            return dummy || (BED.shouldRender(getGameMode().getState()) && BED.kills != 0);
        } catch (Exception e) {
            return false;
        }
    }

}