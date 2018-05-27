package tk.roccodev.zta.modules.mimv;

import eu.the5zig.mod.modules.GameModeItem;
import tk.roccodev.zta.games.MIMV;

public class DailyItem extends GameModeItem<MIMV> {

	public DailyItem() {
		super(MIMV.class);
	}

	@Override
	protected Object getValue(boolean dummy) {

		return MIMV.dailyPoints + " Karma";

	}

	@Override
	public String getName() {
		return "Daily";
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		try {
			if (!(getGameMode() instanceof MIMV))
				return false;
			return dummy || (MIMV.shouldRender(getGameMode().getState()));
		} catch (Exception e) {
			return false;
		}
	}

}