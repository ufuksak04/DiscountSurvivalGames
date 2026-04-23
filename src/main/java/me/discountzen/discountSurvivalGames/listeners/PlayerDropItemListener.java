package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerDropItemListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        if (mem.state.equals(PlayerState.ALIVE) || mem.state.equals(PlayerState.ALIVE)) return;
        e.setCancelled(true);
    }
}
