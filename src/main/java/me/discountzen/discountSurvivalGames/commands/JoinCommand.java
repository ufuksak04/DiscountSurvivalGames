package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.GamePhase;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class JoinCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public JoinCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameManager manager = plugin.getGameManager();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        if (!mem.state.equals(PlayerState.LOBBY)) return true;
        if (args.length == 0) {
            manager.JoinGame(player.getUniqueId());
        } else {
            int gameID = Integer.parseInt(args[0]);
            manager.JoinGame(player.getUniqueId(), gameID);
        }
        return true;
    }
}
