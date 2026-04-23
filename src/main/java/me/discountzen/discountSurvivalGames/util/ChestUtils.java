package me.discountzen.discountSurvivalGames.util;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.LootTableJson;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChestUtils {

    public static void GenerateChestLoot(DiscountSurvivalGames plugin, GameControllerTask game, boolean refill, boolean distanceMode) {
        World world = Bukkit.getWorld("game_" + game.gameID);
        WorldFile worldFile = plugin.getGameManager().gameWorldFiles.get(game.map.worldID);
        if (distanceMode) {
            List<Chest> found = new ArrayList<>();
            Location mapMiddle = worldFile.getMapMiddle();
            mapMiddle.setWorld(world);
            int radius = 250;

            int centerChunkX = mapMiddle.getBlockX() >> 4;
            int centerChunkZ = mapMiddle.getBlockZ() >> 4;
            int chunkRadius = radius >> 4; // ~15 for radius 250

            for (int cx = centerChunkX - chunkRadius; cx <= centerChunkX + chunkRadius; cx++) {
                for (int cz = centerChunkZ - chunkRadius; cz <= centerChunkZ + chunkRadius; cz++) {

                    if (!world.isChunkLoaded(cx, cz)) {
                        // Load if you want, or skip to avoid lag:
                        // world.loadChunk(cx, cz);
                        continue;
                    }

                    Chunk chunk = world.getChunkAt(cx, cz);

                    for (BlockState state : chunk.getTileEntities()) {
                        if (state instanceof Chest chest) {
                            Location chestLoc = chest.getLocation();

                            if (chestLoc.getWorld().equals(world)
                                    && chestLoc.distanceSquared(mapMiddle) <= radius * radius) {
                                found.add(chest);
                            }
                        }
                    }
                }
            }
            for (Chest chest : found) {
                double distance = Math.sqrt(chest.getLocation().distanceSquared(mapMiddle));
                int tier = 1;
                if (distance > 60) tier = 2;
                if (distance > 200) tier = 3;
                if (refill) tier++;
                tier = Math.clamp(tier, 1, 3);
                PopulateChest(plugin, chest, tier, plugin.config.chestFillAmount);
            }
        }
        else {
            Iterator<Map.Entry<Location, Integer>> it = worldFile.getChests().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Location, Integer> entry = it.next();
                Location loc = entry.getKey();
                int tier = entry.getValue();

                if (!(world.getBlockAt(loc).getState() instanceof Chest)) {
                    try {
                        File file = new File(plugin.getDataFolder(), "sg maps/" + worldFile.worldID + ".json");
                        worldFile.removeChest(loc);
                        JsonUtils.writeJson(file, worldFile);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                Chest chest = (Chest) world.getBlockAt(loc).getState();
                if (refill) tier++;
                tier = Math.clamp(tier, 0, 3);
                PopulateChest(plugin, chest, tier, plugin.config.chestFillAmount);
            }
        }
    }

    public static void PopulateChest(DiscountSurvivalGames plugin, Chest chest, int tier, int percent) {
        chest.getInventory().clear();
        Inventory inv = chest.getInventory();
        File file = new File(plugin.getDataFolder(), " loot tables/tier_" + tier + ".json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                JsonUtils.writeJson(file, new LootTableJson());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        LootTableJson lootTable = (LootTableJson) JsonUtils.readJson(file, LootTableJson.class);
        int i = 1;
        ArrayList<Integer> usedSlots = new ArrayList<>();
        while ((i*100)/inv.getSize() < percent) {
            i++;
            int slot = new Random().nextInt(inv.getSize());
            if (usedSlots.contains(slot)) continue;
            usedSlots.add(slot);
            inv.setItem(slot, lootTable.getRandomItem());
        }
    }
}
