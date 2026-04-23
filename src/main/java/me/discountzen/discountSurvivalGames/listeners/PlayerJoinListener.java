package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerJoinListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        player.closeInventory();
        //GUIManager.openGUIs.put(player.getUniqueId(), " ");
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        if (mem.state.equals(PlayerState.ALIVE)) {
            GameControllerTask game = plugin.getGameManager().games.get(mem.getGameID());
            player.setScoreboard(mem.m_board);
            mem.MakeSpectator(plugin, true);
        } else {
            plugin.getGameManager().SendPlayerToHub(mem);
        }
        /*
        if (plugin.databaseExists) {
            plugin.getSQL().queryAsync(
                    "SELECT kills, wins FROM player_data WHERE uuid = ?;",
                    rs -> {
                        try {
                            if (rs.next()) {
                                int kills = rs.getInt("kills");
                                int wins = rs.getInt("wins");
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                    playerData.getPlayer(player).totalKills = kills;
                                    playerData.getPlayer(player).wins = wins;
                                });
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return;
                        }
                    },
                    player.getUniqueId().toString()
            );
        }
        else {
            playerData.getPlayer(player).totalKills = 0;
            playerData.getPlayer(player).wins = 0;
        }
         */
    }
}
