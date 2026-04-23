package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.TextUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GamesCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public GamesCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameManager gameManager = plugin.getGameManager();
        String padding = "";
        for (int i = 0; i < 8; i++) padding += " ";
        if (gameManager.games.size() != 0) {
            player.sendMessage(ChatColor.WHITE + "Active games:");
            int totalPlayersCount = 0;
            for (GameControllerTask game : gameManager.games.values()) {
                totalPlayersCount += game.players.size();
                player.sendMessage(padding + ChatColor.GOLD + game.gameID + " - " + ChatColor.RESET + "" + ChatColor.WHITE + game.map.name);
            }
            player.sendMessage(ChatColor.GRAY + "Total players in all active games: " + totalPlayersCount);
        }
        else {
            player.sendMessage(ChatColor.RED + "There are currently no active games.");
        }

        return true;
    }
}
