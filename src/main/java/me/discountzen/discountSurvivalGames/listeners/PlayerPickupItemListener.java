package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerPickupItemListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        PlayerData playerData = plugin.getPlayerData();
        Player p = e.getPlayer();
        if (playerData.getPlayer(p).state.equals(PlayerState.SPECTATOR)) {
            e.setCancelled(true);
        }
    }
}
