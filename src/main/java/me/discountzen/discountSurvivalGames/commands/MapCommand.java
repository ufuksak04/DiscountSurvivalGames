package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapCommand implements CommandExecutor, TabCompleter {
    private final DiscountSurvivalGames plugin;

    public  MapCommand(DiscountSurvivalGames plugin) {this.plugin = plugin;}


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length >= 2) {
                    String formattedName = FormatName(args[1]);
                    if (MapEntry.addMap(plugin, formattedName, player.getWorld().getUID())) {
                        MapEntry.cacheMaps(plugin);
                        player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Map added!");
                    }
                    else {
                        player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "A map with that name already exists!");
                    }
                    return true;
                }
            }
            else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length >= 2) {
                    String formattedName = FormatName(args[1]);
                    if (MapEntry.deleteMap(plugin, formattedName)) {
                        MapEntry.cacheMaps(plugin);
                        player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Map deleted!");
                    }
                    else {
                        player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "A map with that name does not exist!");
                    }
                    return true;
                }
            }
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("add", "delete");
        }
        else if (args.length == 1) {
            return Arrays.asList("add", "delete")
                    .stream()
                    .filter(value -> value.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String FormatName(String name) {
        String formattedName = "";
        ArrayList<String> words = new ArrayList<>();
        words.addAll(List.of(name.split(" ")));
        for (String word : words) {
            word = Character.toTitleCase(word.charAt(0)) + word.toLowerCase().substring(1);
            formattedName += word;
        }
        return formattedName;
    }

}
