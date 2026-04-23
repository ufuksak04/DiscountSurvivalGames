package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.GamePhase;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbortGameCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public AbortGameCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        PlayerData playerData = plugin.getPlayerData();
        Player player = (Player) sender;
        if (args.length == 0) {
            if (!(playerData.getPlayer(player).state.equals(PlayerState.LOBBY) || playerData.getPlayer(player).state.equals(PlayerState.LOBBY))) {
                if (playerData.getPlayer(player).getGameID() != 999999) {
                    Abort(playerData.getPlayer(player).getGameID());
                }
                else {
                    player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Invalid arguments.");
                }
            }
            else {
                player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "You are not in a game, please enter a game ID.");
            }
        }
        else if (args.length == 1) {
            Abort(Integer.parseInt(args[0]));
        }
        else {
            player.sendMessage(plugin.pluginPrefix + ChatColor.RED + "Invalid arguments.");
        }
        return true;
    }

    private void Abort(int gameID) {
        GameManager manager = plugin.getGameManager();
        manager.AbortGame(gameID);
    }
}
