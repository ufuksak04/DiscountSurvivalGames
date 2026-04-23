package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class BlockBreakListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public BlockBreakListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        PlayerData playerData = plugin.getPlayerData();
        Player p = e.getPlayer();
        SGPlayer mem = playerData.getPlayer(p);

        if (mem.state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) return;
        if (!mem.state.equals(PlayerState.ALIVE)) {
            e.setCancelled(true);
        }
        else {
            Block brokenBlock = e.getBlock();
            if (brokenBlock.getType() == null) e.setCancelled(true);
            if (brokenBlock.getType() != Material.OAK_LEAVES) e.setCancelled(true);
        }
    }

}
