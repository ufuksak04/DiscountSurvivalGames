package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public BlockPlaceListener(DiscountSurvivalGames plugin) { this.plugin = plugin;}

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        PlayerData playerData = plugin.getPlayerData();
        Player p = e.getPlayer();
        SGPlayer mem = playerData.getPlayer(p);

        if (mem.state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) return;
        e.setCancelled(true);
    }
}
