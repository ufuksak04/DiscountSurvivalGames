package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MakeSGChestCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public MakeSGChestCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid args! Usage: /makesgchest <tier>");
            return true;
        }
        else {
            Location playerLocation = player.getLocation();
            Block chest = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
            if (chest != null) {
                if (chest.getType() == Material.CHEST) {
                    WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(playerLocation.getWorld().getUID());
                    worldFile.addChest(chest.getLocation(), Integer.parseInt(args[0]));
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        JsonUtils.writeJson(new File(plugin.getDataFolder(), "sg maps/" + playerLocation.getWorld().getUID() + ".json"), worldFile);
                    });
                }
                else {
                    player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "You are not looking at a chest!");
                    return true;
                }
            }
            else {
                player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "You are not looking at a chest!");
                return true;
            }
        }
        return true;
    }
}
