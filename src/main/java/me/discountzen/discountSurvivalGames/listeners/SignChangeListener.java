package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;

public class SignChangeListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public SignChangeListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        if (mem.state.equals(PlayerState.DEV) && player.hasPermission("dsg.admin.configure.map")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSignChange(PlayerSignOpenEvent e) {
        Player player = e.getPlayer();
        PlayerData data = plugin.getPlayerData();
        SGPlayer mem = data.getPlayer(player);
        if (mem.state.equals(PlayerState.DEV) && player.hasPermission("dsg.admin.configure.map")) return;
        e.setCancelled(true);
    }

}
