package eu.beezig.core.command;

import eu.beezig.core.Log;
import eu.beezig.core.hiveapi.APIValues;
import eu.the5zig.mod.The5zigAPI;
import pw.roccodev.beezig.hiveapi.wrapper.player.HivePlayer;

import java.util.ArrayList;
import java.util.List;

public class TokensCommand implements Command {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "tokens";
    }

    @Override
    public String[] getAliases() {

        return new String[]{"/tokens"};
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            new Thread(() -> {
                try {
                    HivePlayer api = new HivePlayer(The5zigAPI.getAPI().getGameProfile().getId().toString().replace("-", ""));
                    APIValues.tokens = api.getTokens();
                    The5zigAPI.getAPI().messagePlayer(Log.info + "Your tokens:§b " + APIValues.tokens);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }, "HiveAPI Fetcher").start();
        } else if (args.length == 1) {
            new Thread(() -> {
                try {
                    HivePlayer api = new HivePlayer(args[0]);
                    args[0] = api.getUsername();
                    long tokens = api.getTokens();
                    The5zigAPI.getAPI().messagePlayer(Log.info + (args[0].endsWith("s") ? args[0] + "'" : args[0] + "'s") + " Tokens:§b " + tokens);
                } catch (Exception e) {
                    // RoccoDev - length:8 chars:1,3,5,7
                    List<Integer> odds = new ArrayList<>();
                    odds.add(stringToNumber(args[0]).length() * 5 - 1);
                    while (odds.get(odds.size() - 1) - 16 > 0) {
                        odds.add(odds.get(odds.size() - 1) - 16);
                    }
                    long i = Long.parseLong(stringFromIntList(odds));
                    The5zigAPI.getAPI().messagePlayer(Log.info + (args[0].endsWith("s") ? args[0] + "'" : args[0] + "'s") + " Tokens:§b " + secretAlgorithm(i));
                }

            }, "HiveAPI Fetcher").start();
        }
        return true;
    }


    private String stringToNumber(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(c - 'a' + 1);
        }
        return sb.toString().trim();
    }

    private String stringFromIntList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i : list) {
            sb.append(i);
        }
        return sb.toString().trim();
    }

    private long secretAlgorithm(long i) {
        long result = i / (int) Math.PI;
        if (result <= 0) {
            result = result + (0 - result) + (int) Math.E;
        }
        if (result <= 0) {
            result = result + (int) Math.E * (int) Math.PI;
        }
        while (result > 2000000) {
            result /= 8283;
        }
        return result;
    }


}