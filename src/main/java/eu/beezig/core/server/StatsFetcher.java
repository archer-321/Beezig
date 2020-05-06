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

package eu.beezig.core.server;

import eu.beezig.core.Beezig;
import eu.beezig.core.util.UUIDUtils;
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility responsible for fetching stats when the user joins a game lobby.
 * This interface supports two producers: an API-backed one and one based on the in-game scoreboard.
 * When declaring a constructor for a GameMode class, you can either run {@code getJob().complete(null)} to disable
 * this interface, or set up the producers accordingly.
 */
public class StatsFetcher {
    private Class<? extends HiveMode> gamemode;
    private CompletableFuture<HiveMode.GlobalStats> job;
    private Pattern scoreboardTitle;
    private Function<String, HiveMode.GlobalStats> apiComputer;
    private Function<HashMap<String, Integer>, HiveMode.GlobalStats> scoreboardComputer;
    private long firstCheck = -1, timeout = 2000L;

    StatsFetcher(Class<? extends HiveMode> mode) {
        gamemode = mode;
        job = new CompletableFuture<>();
    }

    public void setApiComputer(Function<String, HiveMode.GlobalStats> apiComputer) {
        this.apiComputer = apiComputer;
    }

    public void setScoreboardComputer(Function<HashMap<String, Integer>, HiveMode.GlobalStats> scoreboardComputer) {
        this.scoreboardComputer = scoreboardComputer;
    }

    public void setScoreboardTitle(String scoreboardTitle) {
        this.scoreboardTitle = Pattern.compile(scoreboardTitle);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public CompletableFuture<HiveMode.GlobalStats> getJob() {
        return job;
    }

    void attemptCompute(HiveMode mode, Scoreboard board) {
        if(job.isDone()) return;
        if(!gamemode.isAssignableFrom(mode.getClass())) return;
        if(board != null && scoreboardTitle.matcher(ChatColor.stripColor(board.getTitle()).trim()).matches()) {
            Beezig.logger.debug("Found matching scoreboard using title backend");
            HashMap<String, Integer> normalized = board.getLines().entrySet().stream()
                    .map(e -> new HashMap.SimpleEntry<>(ChatColor.stripColor(e.getKey()), e.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
            job.complete(scoreboardComputer.apply(normalized));
        }
        else {
            if(firstCheck == -1) {
                firstCheck = System.currentTimeMillis();
                return;
            }
            if(System.currentTimeMillis() - firstCheck >= timeout) {
                Beezig.logger.debug("Scoreboard not found and timeout reached, querying API");
                Beezig.get().getAsyncExecutor().submit(() -> job.complete(apiComputer.apply(UUIDUtils.strip(Beezig.user().getId()))));
            }
        }
    }
}