package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class DiscountSurvivalGamesCommand implements CommandExecutor, TabCompleter {
    private final DiscountSurvivalGames plugin;
    private boolean guisRunning = false;

    public DiscountSurvivalGamesCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length == 0) {
            SendHelpList(player);
            return true;
        }
        else {
            if (args[0].equalsIgnoreCase("config")) {
                if (args.length == 1) {
                    player.sendMessage("not args");
                    return true;
                } else {
                    if (args[1].equalsIgnoreCase("reload")) {
                        if (args.length > 2) {
                            player.sendMessage("not args");
                            return true;
                        }
                        else {
                            plugin.config.ReloadConfig();
                            plugin.getGameManager().ReadWorldFiles(plugin);
                            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Config successfully reloaded.");
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("debug-stick")) {
                if (args.length == 1) {
                    GameManager mgr = plugin.getGameManager();
                    World world = player.getWorld();
                    if (world.getName().startsWith("game_")) {
                        int gameID = Integer.parseInt(player.getWorld().getName().split("_")[1]);
                        MapEntry map = mgr.gameMaps.get(gameID);
                        WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(map.worldID);
                        Block block = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
                        if (block != null) {
                            Location mapMiddle = worldFile.getMapMiddle();
                            mapMiddle.setWorld(world);
                            player.sendMessage(ChatColor.DARK_AQUA + "Distance to map middle: " + Math.sqrt(block.getLocation().distanceSquared(mapMiddle)));
                        }
                    }
                    else {
                        WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(player.getWorld().getUID());
                        Block block = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
                        if (block != null) {
                            player.sendMessage(ChatColor.DARK_AQUA + "Distance to map middle: " + Math.sqrt(block.getLocation().distanceSquared(worldFile.getMapMiddle())));
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("tp")) {
                if (args.length == 2) {
                    World world = Bukkit.getWorld("game_" + Integer.parseInt(args[1]));
                    MapEntry map = plugin.getGameManager().gameMaps.get(Integer.parseInt(args[1]));
                    WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(map.worldID);
                    Location dest = worldFile.getPedestals().get(0);
                    dest.setWorld(world);
                    player.teleport(dest);
                }
                else {

                }
            }
            else if (args[0].equalsIgnoreCase("help")) {
                SendHelpList(player);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("config", "help");
        }
        else if (args.length == 1) {
            return Arrays.asList("config", "help")
                    .stream()
                    .filter(value -> value.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("config")) {
                return Arrays.asList("reload")
                        .stream()
                        .filter(value -> value.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());

            }
        }
        return Collections.emptyList();
    }

    private void SendHelpList(Player player) {

    }
}
