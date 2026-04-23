package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.FakeDeathSystem;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LeaveCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public LeaveCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = plugin.getPlayerData();
        SGPlayer mem = playerData.getPlayer(player);
        if (mem.state.equals(PlayerState.ALIVE) || mem.state.equals(PlayerState.WAITING) || mem.state.equals(PlayerState.SPECTATOR)) {
            if (!mem.state.equals(PlayerState.SPECTATOR)) {
                FakeDeathSystem.Trigger(plugin, player, mem.getGameID(), FakeDeathSystem.DeathCause.QUIT);
            }
            else {
                player.sendMessage(ChatColor.GREEN + "Sending to hub...");
                plugin.getGameManager().SendPlayerToHub(player);
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "You are not currently in a game!");
        }
        return true;
    }
}
