package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerChatListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerChatListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        GameManager gameManager = plugin.getGameManager();
        PlayerData playerData = plugin.getPlayerData();
        SGPlayer mem = playerData.getPlayer(e.getPlayer());
        String prefix;
        String lpPrefix;
        String msg = ChatColor.translateAlternateColorCodes('&', e.getMessage());
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(mem.uuid);
        if (user == null) {
            lpPrefix = "";
        }
        else {
            CachedMetaData meta = user.getCachedData().getMetaData();
            if (meta.getPrefix() != null) {
                lpPrefix = meta.getPrefix() + " ";
                lpPrefix = ChatColor.translateAlternateColorCodes('&', lpPrefix);
            }
            else lpPrefix = "";
        }
        ChatColor textColor;
        if (mem.getGameID() == 999999) {
            prefix = ChatColor.GRAY + "[" + mem.state.toString() + "] ";
            textColor = ChatColor.GRAY;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (playerData.getPlayer(player).state.equals(PlayerState.LOBBY)) {
                    player.sendMessage(prefix + lpPrefix + ChatColor.GRAY + e.getPlayer().getDisplayName() + ": " + textColor + msg);
                }
            }
        }
        else {
            GameControllerTask game = gameManager.games.get(mem.getGameID());
            ArrayList<SGPlayer> players = game.players;

            if (mem.state.equals(PlayerState.ALIVE) || mem.state.equals(PlayerState.WAITING)) {
                prefix = ChatColor.YELLOW + "[" + ChatColor.RED + mem.state.toString() + ChatColor.YELLOW + "] ";
                textColor = ChatColor.WHITE;
                for (SGPlayer p : players) p.sendMessage(prefix + lpPrefix + ChatColor.WHITE + e.getPlayer().getDisplayName() + ": " + textColor + msg);
            }
            else {
                prefix = ChatColor.GRAY + "[" + mem.state.toString() + "] ";
                textColor = ChatColor.GRAY;
                for (SGPlayer p : game.getPlayersWithState(PlayerState.SPECTATOR)) p.sendMessage(prefix + lpPrefix + ChatColor.GRAY + e.getPlayer().getDisplayName() + ": " + textColor + msg);
            }
        }
        if (mem.state.equals(PlayerState.LOBBY)) {
            Bukkit.getConsoleSender().sendMessage("[LOBBY] " + lpPrefix + ChatColor.GRAY + mem.getPlayer().getDisplayName() + ": " + msg);
        }
        else {
            Bukkit.getConsoleSender().sendMessage("[GAME_" + mem.getGameID() + "] " + lpPrefix + ChatColor.GRAY + mem.getPlayer().getDisplayName() + ": " + msg);
        }
    }
}
