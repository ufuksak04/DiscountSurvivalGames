package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockDropItemListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public BlockDropItemListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }
    private final List<Material> allowedBlocks = List.of(Material.OAK_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES, Material.JUNGLE_LEAVES);

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        Bukkit.getLogger().info(event.getBlock().getType().toString() + " dropped: " + String.join(" ", new ArrayList<String>(Arrays.<String>asList(String.valueOf(event.getItems())))));
        Block block = event.getBlock();
        if (block.getType().equals(Material.OAK_LEAVES)) event.setCancelled(true);
    }
}
