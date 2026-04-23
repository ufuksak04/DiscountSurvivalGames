package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.gui.PlayerTrackerMenu;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GUIManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerInteractListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public PlayerInteractListener(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
    }
    private final Map<UUID, Long> cache = new HashMap<>();
    private final List<Material> allowedBlocks = List.of(Material.STONE_BUTTON, Material.OAK_BUTTON, Material.OAK_TRAPDOOR, Material.OAK_DOOR, Material.LEVER, Material.CHEST);

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        PlayerData playerData = plugin.getPlayerData();
        Player p = e.getPlayer();
        SGPlayer mem = playerData.getPlayer(p);
        UUID uuid = e.getPlayer().getUniqueId();
        long now = System.currentTimeMillis();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (mem.state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    String name = meta.getDisplayName();
                    if (item.getType().equals(Material.STICK)) {
                        if (name.equals(ChatColor.LIGHT_PURPLE + "pedestal")) {
                            if (cache.containsKey(uuid) && now - cache.get(uuid) < 300) {
                                return;
                            }
                            p.performCommand("setspawnpedestal");
                            cache.put(uuid, now);
                            e.setCancelled(true);
                        }
                    }
                }
            }
            if (mem.state.equals(PlayerState.SPECTATOR)) {
                Block block = e.getClickedBlock();
                if (block != null && block.getType().name().endsWith("_BUTTON")) {
                    e.setCancelled(true);
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                String name = meta.getDisplayName();
                if (name.equalsIgnoreCase(ChatColor.GREEN + "Player Tracker")) {
                    PlayerTrackerMenu gui = new PlayerTrackerMenu(plugin);
                    Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(gui.getInventory(p)));
                }
            }
        }
        else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (mem.state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                String name = meta.getDisplayName();
                if (item.getType().equals(Material.STICK)) {
                    if (name.equals(ChatColor.GREEN + "1")) {
                        if (cache.containsKey(uuid) && now - cache.get(uuid) < 300) {
                            return;
                        }
                        p.performCommand("makesgchest 1");
                        cache.put(uuid, now);
                        e.setCancelled(true);
                    }
                    else if (name.equals(ChatColor.BLUE + "2")) {
                        if (cache.containsKey(uuid) && now - cache.get(uuid) < 300) {
                            return;
                        }
                        p.performCommand("makesgchest 2");
                        cache.put(uuid, now);
                        e.setCancelled(true);
                    }
                    else if (name.equals(ChatColor.GOLD + "3")) {
                        if (cache.containsKey(uuid) && now - cache.get(uuid) < 300) {
                            return;
                        }
                        p.performCommand("makesgchest 3");
                        cache.put(uuid, now);
                        e.setCancelled(true);
                    }
                    else if (name.equals(ChatColor.LIGHT_PURPLE + "Debug Stick")) {
                        if (cache.containsKey(uuid) && now - cache.get(uuid) < 300) {
                            return;
                        }
                        p.performCommand("dsg debug-stick");


                        cache.put(uuid, now);
                        e.setCancelled(true);
                    }
                }
            }
            Block block = e.getClickedBlock();
        }
        else if (e.getAction().equals(Action.PHYSICAL)) {
            Block block = e.getClickedBlock();
            if (block != null && block.getType().name().endsWith("_PRESSURE_PLATE")) {
                if (mem.state.equals(PlayerState.SPECTATOR)) e.setCancelled(true);
            }
        }
    }
}
