package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class DevCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public DevCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    private ArrayList<UUID> devs = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        if (!devs.contains(player.getUniqueId())) {
            if (mem.state.equals(PlayerState.LOBBY)) {
                devs.add(player.getUniqueId());
                mem.state = PlayerState.DEV;
                player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "You are now in dev mode.");
            }
            else {
                player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "You must be in the lobby to enter dev mode.");
            }
        }
        else {
            devs.remove(player.getUniqueId());
            mem.state = PlayerState.LOBBY;
            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "You are now back in lobby mode.");
        }

        return true;
    }
}
