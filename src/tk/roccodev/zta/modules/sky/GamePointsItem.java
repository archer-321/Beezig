package tk.roccodev.zta.modules.sky;

import eu.the5zig.mod.modules.GameModeItem;
import tk.roccodev.zta.games.SKY;

public class GamePointsItem extends GameModeItem<SKY> {

	public GamePointsItem() {
		super(SKY.class);
	}

	@Override
	protected Object getValue(boolean dummy) {
		try {
			return SKY.gamePoints;

		} catch (Exception e) {
			e.printStackTrace();
			return "Server error";
		}
	}

	@Override
	public String getName() {
		return "Game";
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		try {
			if (!(getGameMode() instanceof SKY))
				return false;
			if (SKY.gamePoints == 0)
				return false;
			return dummy || (SKY.shouldRender(getGameMode().getState()));
		} catch (Exception e) {
			return false;
		}
	}

}