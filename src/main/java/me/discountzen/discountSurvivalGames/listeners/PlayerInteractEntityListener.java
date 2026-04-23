package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerInteractEntityListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (plugin.getPlayerData().getPlayer(p).state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) return;
        if (!(e.getRightClicked() instanceof ItemFrame) && !(e.getRightClicked() instanceof GlowItemFrame)) return;
        if (e.getRightClicked().getType().equals(EntityType.PLAYER)) return;

        e.setCancelled(true);
    }
}
