package me.discountzen.discountSurvivalGames.tasks;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class ShowChestsTask extends BukkitRunnable {

    private final DiscountSurvivalGames plugin;
    private final Player player;
    public final Map<Block, ArmorStand> cache = new HashMap<>();
    private final double radius = 25.0;

    public ShowChestsTask(DiscountSurvivalGames plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        highlightNearbyChests();
        updateArmorStands();
    }

    @Override
    public void cancel() {
        for (ArmorStand stand : cache.values()) {
            if (stand != null && !stand.isDead()) stand.remove();
        }
        cache.clear();
        super.cancel();
    }

    private void highlightNearbyChests() {
        World world = player.getWorld();
        Location center = player.getLocation();

        for (Block block : getNearbyBlocks(center, radius)) {
            if (block.getType() != Material.CHEST && block.getType() != Material.ENDER_CHEST) continue;
            if (cache.containsKey(block)) continue;

            ArmorStand stand = spawnArmorStand(world, block.getLocation().add(0.5, 0.5, 0.5));
            cache.put(block, stand);
        }
    }

    private ArmorStand spawnArmorStand(World world, Location loc) {
        ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setGlowing(true);
        stand.setCustomNameVisible(true);
        return stand;
    }

    private void updateArmorStands() {
        WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(player.getWorld().getUID());

        Iterator<Map.Entry<Block, ArmorStand>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Block, ArmorStand> entry = it.next();
            Block block = entry.getKey();
            ArmorStand stand = entry.getValue();

            if (stand == null || stand.isDead()) {
                it.remove();
                continue;
            }

            // Remove if out of range
            if (stand.getLocation().distanceSquared(player.getLocation()) > radius * radius) {
                stand.remove();
                it.remove();
                continue;
            }

            // Update chest tier/name
            int tier = getTier(worldFile, block);
            switch (tier) {
                case 1 -> stand.setCustomName(ChatColor.GREEN + "1 | Chest");
                case 2 -> stand.setCustomName(ChatColor.BLUE + "2 | Chest");
                case 3 -> stand.setCustomName(ChatColor.GOLD + "3 | Chest");
                default -> stand.setCustomName(ChatColor.RED + "Unregistered | Chest");
            }
        }
    }

    private int getTier(WorldFile worldFile, Block block) {
        Location loc = block.getLocation();
        if (worldFile.getChests() == null) return -1;
        for (Map.Entry<Location, Integer> entry : worldFile.getChests().entrySet()) {
            Location entryLoc = entry.getKey();
            if (entryLoc.getBlockX() == loc.getBlockX() &&
                    entryLoc.getBlockY() == loc.getBlockY() &&
                    entryLoc.getBlockZ() == loc.getBlockZ()) {
                return entry.getValue();
            }
        }
        return -1; // unregistered
    }

    private List<Block> getNearbyBlocks(Location center, double radius) {
        List<Block> blocks = new ArrayList<>();
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    blocks.add(center.clone().add(x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }
}