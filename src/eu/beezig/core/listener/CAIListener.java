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

package eu.beezig.core.listener;

import eu.beezig.core.ActiveGame;
import eu.beezig.core.BeezigMain;
import eu.beezig.core.IHive;
import eu.beezig.core.Log;
import eu.beezig.core.advancedrecords.AdvancedRecords;
import eu.beezig.core.autovote.AutovoteUtils;
import eu.beezig.core.games.CAI;
import eu.beezig.core.hiveapi.APIValues;
import eu.beezig.core.hiveapi.stuff.cai.CAIRank;
import eu.beezig.core.hiveapi.wrapper.APIUtils;
import eu.beezig.core.hiveapi.wrapper.NetworkRank;
import eu.beezig.core.modules.utils.RenderUtils;
import eu.beezig.core.settings.Setting;
import eu.beezig.core.utils.StreakUtils;
import eu.beezig.core.utils.rpc.DiscordUtils;
import eu.beezig.core.utils.tutorial.SendTutorial;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.util.minecraft.ChatColor;
import org.lwjgl.input.Mouse;
import pw.roccodev.beezig.hiveapi.wrapper.monthly.cai.CaiMonthlyProfile;
import pw.roccodev.beezig.hiveapi.wrapper.player.HivePlayer;
import pw.roccodev.beezig.hiveapi.wrapper.player.games.CaiStats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CAIListener extends AbstractGameListener<CAI> {

    @Override
    public Class<CAI> getGameMode() {
        return CAI.class;
    }

    @Override
    public boolean matchLobby(String arg0) {
        return arg0.equals("CAI");
    }

    @Override
    public void onGameModeJoin(CAI gameMode) {

        gameMode.setState(GameState.STARTING);
        ActiveGame.set("CAI");
        IHive.genericJoin();
        SendTutorial.send("cai_join");

        new Thread(() -> {
            try {
                try {
                    CAI.initDailyPointsWriter();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                Scoreboard sb = The5zigAPI.getAPI().getSideScoreboard();


                if (sb != null && sb.getTitle().contains("Your CAI Stats")) {
                    int points = sb.getLines().get(ChatColor.AQUA + "Points");
                    APIValues.CAIpoints = (long) points;
                }

                CaiStats api = new CaiStats(The5zigAPI.getAPI().getGameProfile().getId().toString().replace("-", ""));

                CAI.rankObject = CAIRank
                        .getFromDisplay(api.getTitle());
                CAI.rank = CAI.rankObject.getTotalDisplay();

                try {
                    if (CAI.attemptNew) {
                        CAI.monthly = api.getMonthlyProfile();
                        CAI.monthly.getPoints(); // Fetch (LazyObject)
                        CAI.hasLoaded = true;
                    }
                } catch (Exception e) {
                    CAI.attemptNew = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public boolean onServerChat(CAI gameMode, String message) {

        if (message.startsWith("§8▍ §bCAI§8 ▏ §3Voting has ended! §bThe map §f")) {
            The5zigAPI.getLogger().info("Voting ended, parsing map");
            String afterMsg = message.split("§8▍ §bCAI§8 ▏ §3Voting has ended! §bThe map ")[1];
            String map = "";
            Pattern pattern = Pattern.compile(Pattern.quote("§f") + "(.*?)" + Pattern.quote("§b"));
            Matcher matcher = pattern.matcher(afterMsg);
            while (matcher.find()) {
                map = matcher.group(1);
            }
            CAI.activeMap = map;
        }

        // Autovoting

        else if (message.startsWith("§8▍ §bCAI§8 ▏ §a§lVote received. §3Your map now has")
                && Setting.AUTOVOTE.getValue()) {
            CAI.hasVoted = true;
        } else if (message.startsWith("§8▍ §bCAI§8 ▏ §6§e§e§l6. §f§cRandom map") && !CAI.hasVoted
                && Setting.AUTOVOTE.getValue()) {

            new Thread(() -> {
                List<String> votesCopy = new ArrayList<>(CAI.votesToParse);

                List<String> parsedMaps = new ArrayList<>(AutovoteUtils.getMapsForMode("cai"));

                TreeMap<String, Integer> votesindex = new TreeMap<>();
                LinkedHashMap<String, Integer> finalvoting = new LinkedHashMap<>();

                for (String s : votesCopy) {
                    String[] data = s.split("\\.");
                    String index = ChatColor.stripColor(data[0]).replaceAll("§8▍ §bCAI§8 ▏ §6§e§e§l", "")
                            .replaceAll("▍ CAI ▏", "").trim();
                    String[] toConsider = ChatColor.stripColor(data[1]).split("\\[");
                    String consider = ChatColor.stripColor(toConsider[0]).trim().replaceAll(" ", "_").toUpperCase();
                    System.out.println("VoteCopy: " + consider);

                    finalvoting.put(consider, Integer.parseInt(index));
                }


                for (String s : parsedMaps) {
                    if (finalvoting.containsKey(s)) {
                        votesindex.put(s, finalvoting.get(s));
                        break;
                    }
                }

                if (votesindex.size() == 0 && Setting.AUTOVOTE_RANDOM.getValue()) {
                    The5zigAPI.getAPI().sendPlayerMessage("/v 6");
                    The5zigAPI.getAPI().messagePlayer("§8▍ §bCAI§8 ▏ " + "§eAutomatically voted for §cRandom map");
                } else {
                    System.out.println(votesindex.firstEntry().getKey());
                    The5zigAPI.getAPI().sendPlayerMessage("/v " + votesindex.firstEntry().getValue());
                    The5zigAPI.getAPI().messagePlayer("§8▍ §bCAI§8 ▏ " + "§eAutomatically voted for map §6#" + votesindex.firstEntry().getValue());
                }
                CAI.votesToParse.clear();
                CAI.hasVoted = true;

            }).start();
        } else if (message.startsWith("§8▍ §bCAI§8 ▏ §6§e§e§l") && !CAI.hasVoted && Setting.AUTOVOTE.getValue()) {
            CAI.votesToParse.add(message);
        } else if (message.equals("§8▍ §bCAI§8 ▏ §aYou have captured the enemy's team leader!")) {
            CAI.gamePoints += 5;
            APIValues.CAIpoints += 5;
            CAI.dailyPoints += 5;
        } else if (message.equals("§8▍ §bCAI§8 ▏ §cYou can't go invisible whilst capturing the leader!")) {
            CAI.invisCooldown = 0;
        } else if (message.endsWith("§cCowboys Leader§7.")) {

            CAI.team = "§eIndians";
            CAI.inGame = true;
            DiscordUtils.updatePresence("Battling in Cowboys and Indians", "Playing as I on " + CAI.activeMap, "game_cai");

        } else if (message.endsWith("§eIndians Leader§7.")) {
            CAI.team = "§cCowboys";
            CAI.inGame = true;
            DiscordUtils.updatePresence("Battling in Cowboys and Indians", "Playing as C on " + CAI.activeMap, "game_cai");
        } else if (message.equals("§8▍ §bCAI§8 ▏ §7You received §f10 points §7for your team's capture.")) {

            APIValues.tokens += 5;
            CAI.gamePoints += 10;
            APIValues.CAIpoints += 10;
            CAI.dailyPoints += 10;

        } else if (message.endsWith("§eIndians have won!") && CAI.team != null && CAI.team.equals("§eIndians")) {
            CAI.gamePoints += 50;
            APIValues.CAIpoints += 50;
            CAI.dailyPoints += 50;
            CAI.hasWon = true;
            System.out.println("Won!");
            CAI.winstreak++;
            if (CAI.winstreak > CAI.bestStreak)
                CAI.bestStreak = CAI.winstreak;
            StreakUtils.incrementWinstreakByOne("cai");
        } else if (message.endsWith("§cCowboys have won!") && CAI.team != null && CAI.team.equals("§cCowboys")) {
            CAI.gamePoints += 50;
            APIValues.CAIpoints += 50;
            CAI.dailyPoints += 50;
            CAI.hasWon = true;
            System.out.println("Won!");
            CAI.winstreak++;
            if (CAI.winstreak > CAI.bestStreak)
                CAI.bestStreak = CAI.winstreak;
            StreakUtils.incrementWinstreakByOne("cai");
        } else if (message.startsWith("§8▍ §bCAI§8 ▏ §7You gained §f5 points §7for killing")) {


            CAI.gamePoints += 5;
            APIValues.CAIpoints += 5;
            CAI.dailyPoints += 5;

        } else if (message.endsWith("§7[Leader Alive Bonus]")
                && message.startsWith("§8▍ §bCAI§8 ▏ §2+")) {

            String points = message.replace(" Points §7[Leader Alive Bonus]", "")
                    .replace("§8▍ §bCAI§8 ▏ §2+ §a", "");

            CAI.gamePoints += Long.parseLong(points.trim());
            APIValues.CAIpoints += Long.parseLong(points.trim());
            CAI.dailyPoints += Long.parseLong(points.trim());

        }

        // Advanced Records

        else if (message.contains("'s Stats §6§m                  ") && !message.startsWith("§f ") && Setting.ADVANCED_RECORDS.getValue()) {
            CAI.messagesToSend.add(message);
            The5zigAPI.getLogger().info("found header");
            return true;
        } else if (message.startsWith("§3 ") && Setting.ADVANCED_RECORDS.getValue()) {

            CAI.messagesToSend.add(message);
            The5zigAPI.getLogger().info("found entry");

            return true;
        } else if (message.contains(" §ahttp://hivemc.com/player/") && !message.startsWith("§f ") && Setting.ADVANCED_RECORDS.getValue()) {
            CAI.footerToSend.add(message);
            The5zigAPI.getLogger().info("Found Player URL");

            return true;
        } else if (message.startsWith("§8▍ §bCAI§8 ▏ §cYou have been captured")) {
            CAI.speedCooldown = 0;
            CAI.invisCooldown = 0;
            CAI.leaderItem0 = 0;
            CAI.leaderItem1 = 0;
            CAI.leaderItem2 = 0;
        } else if (message.startsWith("§8▍ §bCAI§8 ▏ §a§lYou Escaped!")) {
            CAI.speedCooldown = 0;
            CAI.invisCooldown = 0;
            CAI.leaderItem0 = 0;
            CAI.leaderItem1 = 0;
            CAI.leaderItem2 = 0;
        } else if ((message.equals("                      §6§m                  §6§m                  ")
                && !message.startsWith("§f ")) && Setting.ADVANCED_RECORDS.getValue()) {
            The5zigAPI.getLogger().info("found footer");
            CAI.footerToSend.add(message);
            The5zigAPI.getLogger().info("executed /records");
            if (CAI.footerToSend.contains("                      §6§m                  §6§m                  ")) {
                // Advanced Records - send
                The5zigAPI.getLogger().info("Sending adv rec");
                new Thread(() -> {
                    AdvancedRecords.isRunning = true;
                    The5zigAPI.getAPI().messagePlayer(Log.info + "Running Advanced Records...");
                    try {

                        CaiStats api = new CaiStats(AdvancedRecords.player, true);
                        HivePlayer global = api.getPlayer();
                        CAIRank rank = null;

                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                        DecimalFormat df = (DecimalFormat) nf;
                        df.setMaximumFractionDigits(2);
                        df.setMinimumFractionDigits(2);

                        DecimalFormat df1f = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
                        df1f.setMaximumFractionDigits(1);
                        df1f.setMinimumFractionDigits(1);

                        String rankTitle = Setting.SHOW_NETWORK_RANK_TITLE.getValue()
                                ? global.getRank().getHumanName()
                                : "";
                        ChatColor rankColor = null;
                        if (Setting.SHOW_NETWORK_RANK_COLOR.getValue()) {
                            rankColor = NetworkRank.fromDisplay(global.getRank().getHumanName()).getColor();
                        }
                        String rankTitleCAI = Setting.SHOW_RECORDS_RANK.getValue() ? api.getTitle() : null;
                        if (rankTitleCAI != null)
                            rank = CAIRank.getFromDisplay(rankTitleCAI);

                        int kills = 0;
                        long points = 0;
                        int deaths = 0;
                        int gamesPlayed = 0;
                        int victories = 0;

                        long timeAlive;

                        long catches = 0, captured = 0, caught = 0, captures = 0;

                        Date lastGame = Setting.SHOW_RECORDS_LASTGAME.getValue() ? api.getLastLogin() : null;
                        Integer achievements = Setting.SHOW_RECORDS_ACHIEVEMENTS.getValue() ? api.getUnlockedAchievements().size()
                                : null;

                        long monthlyRank = 0;
                        if (Setting.SHOW_RECORDS_MONTHLYRANK.getValue()) {
                            try {
                                CaiMonthlyProfile monthly = api.getMonthlyProfile();
                                if (monthly != null) {
                                    monthlyRank = monthly.getPlace();
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        List<String> messages = new ArrayList<>(CAI.messagesToSend);
                        for (String s : messages) {

                            if (s.trim().endsWith("'s Stats §6§m")) {
                                The5zigAPI.getLogger().info("Editing Header...");
                                StringBuilder sb = new StringBuilder();
                                String correctUser = global.getUsername();
                                if (correctUser.contains("nicked player"))
                                    correctUser = "Nicked/Not found";
                                sb.append("§f          §6§m                  §f ");
                                The5zigAPI.getLogger().info("Added base...");
                                if (rankColor != null) {
                                    sb.append(rankColor).append(correctUser);
                                    The5zigAPI.getLogger().info("Added colored user...");
                                } else {
                                    sb.append(correctUser);
                                    The5zigAPI.getLogger().info("Added white user...");
                                }
                                sb.append("§f's Stats §6§m                  ");
                                The5zigAPI.getLogger().info("Added end...");
                                The5zigAPI.getAPI().messagePlayer("§f " + sb.toString());

                                if (rankTitle != null && rankTitle.contains("nicked player"))
                                    rankTitle = "Nicked/Not found";
                                if (!rankTitle.equals("Nicked/Not found") && !rankTitle.isEmpty()) {
                                    if (rankColor == null)
                                        rankColor = ChatColor.WHITE;
                                    The5zigAPI.getAPI().messagePlayer("§f           " + "§6§m       §6" + " ("
                                            + rankColor + rankTitle + "§6) " + "§m       ");
                                }
                                continue;
                            }

                            String[] newData = s.split("\\: §b");
                            long currentValue = 0;
                            try {
                                currentValue = Long.parseLong(newData[1]);
                                newData[1] = Log.df(currentValue);
                                s = newData[0] + ": §b" + newData[1];
                            } catch (NumberFormatException ignored) {
                                s = newData[0] + ": §b" + newData[1];
                            }

                            if (s.startsWith("§3 Points: §b")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("§3 Points: §b");
                                points = currentValue;
                                sb.append(newData[1]);
                                if (rank != null)
                                    sb.append(" (").append(rank.getTotalDisplay());
                                if (Setting.SHOW_RECORDS_POINTSTONEXTRANK.getValue())
                                    sb.append(" / ").append(rank.getPointsToNextRank((int) points));
                                if (rank != null)
                                    sb.append("§b)");

                                // if(rank != null) sb.append(" (" + rank.getTotalDisplay() + "§b)");

                                The5zigAPI.getAPI().messagePlayer("§f " + sb.toString().trim());
                                continue;
                            } else if (s.startsWith("§3 Victories: §b")) {
                                victories = Math.toIntExact(currentValue);
                            } else if (s.startsWith("§3 Games Played: §b")) {
                                gamesPlayed = Math.toIntExact(currentValue);
                            }

                            The5zigAPI.getAPI().messagePlayer("§f " + s);

                        }

                        if (achievements != null) {
                            The5zigAPI.getAPI().messagePlayer("§f §3 Achievements: §b" + achievements + "/44");
                        }
                        if (Setting.SHOW_RECORDS_WINRATE.getValue()) {
                            double wr = (double) victories / (double) gamesPlayed;
                            The5zigAPI.getAPI()
                                    .messagePlayer("§f §3 Winrate: §b" + df1f.format(wr * 100) + "%");
                        }
                        if (monthlyRank != 0) {
                            The5zigAPI.getAPI().messagePlayer("§f §3 Monthly Place: §b#" + monthlyRank);
                        }
                        if (Setting.CAI_SHOW_CATCHES_CAUGHT.getValue()) {
                            if (catches == 0)
                                catches = api.getCatches();
                            if (caught == 0)
                                caught = api.getCaught();
                            The5zigAPI.getAPI().messagePlayer("§f §3 Cc/Ct: §b"
                                    + df.format((double) catches / (double) caught) + "");
                        }
                        if (Setting.SHOW_RECORDS_PPG.getValue()) {
                            double ppg = (double) points / (double) gamesPlayed;
                            The5zigAPI.getAPI().messagePlayer("§f §3 Points per Game: §b" + df1f.format(ppg));
                        }
                        if (Setting.CAI_SHOW_CAPTURES_GAME.getValue()) {
                            if (captures == 0) captures = api.getCaptures();
                            if (gamesPlayed == 0) gamesPlayed = Math.toIntExact(api.getGamesPlayed());
                            double cpg = (double) captures / (double) gamesPlayed;
                            The5zigAPI.getAPI().messagePlayer("§f §3 Captures per Game: §b" + df1f.format(cpg));
                        }


                        if (lastGame != null) {
                            Calendar lastSeen = Calendar.getInstance();
                            lastSeen.setTimeInMillis(lastGame.getTime());
                            The5zigAPI.getAPI().messagePlayer(
                                    "§f §3 Last Game: §b" + APIUtils.getTimeAgo(lastSeen.getTimeInMillis()));
                        }

                        for (String s : CAI.footerToSend) {
                            The5zigAPI.getAPI().messagePlayer("§f " + s);
                        }

                        CAI.messagesToSend.clear();
                        CAI.footerToSend.clear();
                        AdvancedRecords.isRunning = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getCause() instanceof FileNotFoundException) {
                            The5zigAPI.getAPI().messagePlayer(Log.error + "Player nicked or not found.");
                            CAI.messagesToSend.clear();
                            CAI.footerToSend.clear();
                            AdvancedRecords.isRunning = false;
                            return;
                        }
                        The5zigAPI.getAPI().messagePlayer(Log.error
                                + "Oops, looks like something went wrong while fetching the records, so you will receive the normal one!");

                        for (String s : CAI.messagesToSend) {
                            The5zigAPI.getAPI().messagePlayer("§f " + s);
                        }
                        for (String s : CAI.footerToSend) {
                            The5zigAPI.getAPI().messagePlayer("§f " + s);
                        }
                        The5zigAPI.getAPI().messagePlayer(
                                "§f " + "                      §6§m                  §6§m                  ");
                        CAI.messagesToSend.clear();
                        CAI.footerToSend.clear();
                        AdvancedRecords.isRunning = false;
                    }
                }).start();
                return true;

            }

        }

        return false;

    }

    @Override
    public void onTitle(CAI gameMode, String title, String subTitle) {
        if (subTitle != null && subTitle.equals("§r§fYou died§r")) {
            CAI.speedCooldown = 0;
            CAI.invisCooldown = 0;
        }
        if (title != null && title.endsWith(The5zigAPI.getAPI().getGameProfile().getName() + "' has§r§5 §r§5§lESCAPED!§r")) {
            CAI.speedCooldown = 0;
            CAI.invisCooldown = 0;
            CAI.leaderItem0 = 0;
            CAI.leaderItem1 = 0;
            CAI.leaderItem2 = 0;

        }
    }

    @Override
    public void onTick(CAI gameMode) {
        if (CAI.speedCooldown != 0) CAI.speedCooldown--;
        if (CAI.invisCooldown != 0) CAI.invisCooldown--;
        if (CAI.leaderItem0 != 0) CAI.leaderItem0--;
        if (CAI.leaderItem1 != 0) CAI.leaderItem1--;
        if (CAI.leaderItem2 != 0) CAI.leaderItem2--;
        if (Mouse.isButtonDown(1)) {
            if (The5zigAPI.getAPI().getItemInMainHand() == null) return;
            if (RenderUtils.getCurrentScreen() != null) return;
            switch (The5zigAPI.getAPI().getItemInMainHand().getDisplayName()) {
                case "§eSpeed Dust":
                    CAI.speedCooldown = 2400;
                    break;
                case "§eInvisibility Dust":
                    CAI.invisCooldown = 2400;
                    break;
                case "§3Attempt Escape":
                    CAI.leaderItem2 = 500;
                    break;
                case "§6Blind Carrier":
                    CAI.leaderItem0 = 500;
                    break;
                case "§cEmergency Flare":
                    CAI.leaderItem1 = 500;
                    break;
            }
        }
        if (CAI.gameId != null || The5zigAPI.getAPI().getSideScoreboard() == null) return;
        HashMap<String, Integer> lines = The5zigAPI.getAPI().getSideScoreboard().getLines();
        for (Map.Entry<String, Integer> e : lines.entrySet()) {
            if (e.getValue() == 4) {
                CAI.gameId = ChatColor.stripColor(e.getKey()).trim();
            }
        }
    }

    @Override
    public boolean onActionBar(CAI gameMode, String message) {
        if (BeezigMain.isColorDebug) {
            System.out.println("CAI ActionDebug: (" + message + ")");
        }
        return false;
    }


    @Override
    public void onServerConnect(CAI gameMode) {
        CAI.reset(gameMode);
    }

}
