package me.discountzen.discountSurvivalGames.storage;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PlayerData {
    private final DiscountSurvivalGames plugin;

    public PlayerData(DiscountSurvivalGames plugin) { this.plugin = plugin; players.clear(); }

    public Map<UUID, SGPlayer> players = new HashMap<UUID, SGPlayer>();

    public SGPlayer getPlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        }
        return createMemory(uuid);
    }

    public SGPlayer getPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        }
        return createMemory(uuid);
    }

    public Player getPlayer(SGPlayer data) {
        for (Map.Entry<UUID, SGPlayer> entry : players.entrySet()) {
            if (entry.getValue().equals(data)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
                if (!offlinePlayer.isOnline()) {
                    Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
                    new Exception().printStackTrace();
                    return null;
                }
                return (Player) offlinePlayer;
            }
        }
        return null;
    }

    private SGPlayer createMemory(UUID uuid) {
        SGPlayer newPlayer = new SGPlayer(uuid, plugin.getGameManager());
        players.put(uuid, newPlayer);
        PlayerJsonWriter.SerializedPlayerData data = PlayerJsonWriter.ReadPlayerData(plugin, uuid);
        newPlayer.totalKills = data.getTotalKills();
        newPlayer.wins = data.getWins();
        return newPlayer;
    }




}
