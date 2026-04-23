package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public FlyCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "You have disabled flight.");
        }
        else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "You have enabled flight.");
        }

        return true;
    }
}
