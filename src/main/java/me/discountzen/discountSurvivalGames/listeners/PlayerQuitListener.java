package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.storage.PlayerJsonWriter;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.FakeDeathSystem;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerQuitListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerQuitListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        if (mem.state.equals(PlayerState.ALIVE)) {
            GameControllerTask game = plugin.getGameManager().games.get(mem.getGameID());
            FakeDeathSystem.Trigger(plugin, player, mem.getGameID(), FakeDeathSystem.DeathCause.QUIT);
        }

        PlayerJsonWriter.SerializedPlayerData jsonData = new PlayerJsonWriter.SerializedPlayerData(mem.uuid);
        jsonData.setWins(mem.wins);
        jsonData.setTotalKills(mem.totalKills);
        PlayerJsonWriter.writePlayerData(plugin, jsonData);
        /*
        if (plugin.databaseExists) {
            plugin.getSQL().updateAsync(
                    "INSERT INTO player_data (uuid, kills, wins) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE kills = ?, wins = ?;",
                    player.getUniqueId().toString(), playerData.getPlayer(player).totalKills, playerData.getPlayer(player).wins,
                    playerData.getPlayer(player).totalKills,  playerData.getPlayer(player).wins
            );
        }
         */
    }
}
