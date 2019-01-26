package eu.beezig.core.modules.gnt;

import eu.beezig.core.ActiveGame;
import eu.beezig.core.IHive;
import eu.beezig.core.games.Giant;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;

import java.text.DecimalFormat;

public class KDRChangeItem extends GameModeItem<Giant> {

    public KDRChangeItem() {
        super(Giant.class);
    }


    @Override
    protected Object getValue(boolean dummy) {
        try {
            double kdr = Giant.gameKdr;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            return df.format(kdr) + " (" + (Giant.gameKdr - Giant.totalKdr > 0 ? "+" : "") + df.format((Giant.gameKdr - Giant.totalKdr)) + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return "Server error";
        }
    }

    @Override
    public String getTranslation() {
        return "beezig.module.gnt.kdchange";
    }


    @Override
    public boolean shouldRender(boolean dummy) {
        try {

            return dummy || (The5zigAPI.getAPI().getActiveServer() instanceof IHive && (ActiveGame.is("gnt") || ActiveGame.is("gntm")) && (Giant.totalKdr - Giant.gameKdr != 0));
        } catch (Exception e) {
            return false;
        }
    }

}