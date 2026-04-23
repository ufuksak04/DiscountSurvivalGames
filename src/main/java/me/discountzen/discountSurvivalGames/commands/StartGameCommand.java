package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.GamePhase;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class StartGameCommand implements CommandExecutor, TabCompleter {
    private final DiscountSurvivalGames plugin;

    public StartGameCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }
    private GameManager gameManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        gameManager = plugin.getGameManager();
        if (args.length == 0) {
            boolean gameCreated = gameManager.CreateGame();
            if (gameCreated) {
                sender.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Initializing a game on a random map...");
            }
            else {
                sender.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Game creation failed! Check logs or ask an administrator.");
            }
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                boolean gameCreated = gameManager.CreateGame();
                if (gameCreated) {
                    sender.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Initializing a game on a random map...");
                }
                else {
                    sender.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Game creation failed! Check logs or ask an administrator.");
                }
            }
            else {
                ArrayList<MapEntry> maps = MapEntry.getMaps();
                if (!maps.isEmpty()) {
                    for (MapEntry map : maps) {
                        if (map.name.equalsIgnoreCase(args[0])) {
                            boolean gameCreated = gameManager.CreateGame(map);
                            if (gameCreated) {
                                sender.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Initializing a game on " + ChatColor.WHITE + map.name + ChatColor.GREEN + "...");
                            }
                            else {
                                sender.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Game creation failed! Check logs or ask an administrator.");
                            }
                        }
                    }
                }
            }
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        ArrayList<MapEntry> maps = MapEntry.getMaps();
        ArrayList<String> mapNames = new ArrayList<>();
        if (maps.isEmpty()) {
            return Collections.emptyList();
        }
        for (MapEntry map : maps) {
            mapNames.add(map.name);
        }
        if (args.length == 0) {
            return mapNames;
        }
        else if (args.length == 1) {
            return mapNames
                    .stream()
                    .filter(value -> value.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
