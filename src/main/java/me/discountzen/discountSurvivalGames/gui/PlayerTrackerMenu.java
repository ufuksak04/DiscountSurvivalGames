package me.discountzen.discountSurvivalGames.gui;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GUIManager;
import me.discountzen.discountSurvivalGames.util.PlayerProfileCache;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerTrackerMenu {
    private final DiscountSurvivalGames plugin;
    public static Map<UUID, UUID> tracking = new HashMap<UUID, UUID>();

    public PlayerTrackerMenu(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    public Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUIManager.getGUICode("Player Tracker") + ChatColor.GREEN + "Player Tracker");
        //GUIManager.openGUIs.put(player.getUniqueId(), "PLAYER_TRACKER");

        ArrayList<ItemStack> trackerList = getTrackerList(player);
        if (!trackerList.isEmpty()) {
            for (int i = 0; i < trackerList.size(); i++) {
                inv.setItem(i, trackerList.get(i));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
//                if (!GUIManager.openGUIs.get(player.getUniqueId()).equalsIgnoreCase("PLAYER_TRACKER")) {
//                    this.cancel(); // stop when they close GUI
//                    return;
//                }
                if (!player.getOpenInventory().getTitle().startsWith(GUIManager.getGUICode("Player Tracker"))) {
                    this.cancel(); // stop when they close GUI
                    return;
                }



                ArrayList<ItemStack> trackerList = getTrackerList(player);
                if (!trackerList.isEmpty()) {
                    for (int i = 0; i < trackerList.size(); i++) {
                        inv.setItem(i, trackerList.get(i));
                    }
                } else { inv.clear(); }
                player.updateInventory(); // push update to client
            }
        }.runTaskTimer(plugin, 0L, 3L); // 20 ticks = 1s
        return inv;
    }

    public void handleClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player)e.getWhoClicked();

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) return;
        if (clickedItem.getItemMeta() == null) return;
        if (clickedItem.getType() == Material.LIME_DYE) {
            ItemMeta meta = clickedItem.getItemMeta();
            Player trackedPlayer;
            for (Player p : Bukkit.getOnlinePlayers()) {
                String displayName = ChatColor.GRAY + p.getName();
                if (displayName.equalsIgnoreCase(meta.getDisplayName())) {
                    trackedPlayer = p;
                    tracking.put(player.getUniqueId(), trackedPlayer.getUniqueId());
                    player.closeInventory();
                    //GUIManager.openGUIs.put(player.getUniqueId(), " ");
                    player.teleport(trackedPlayer);
                    player.sendMessage(ChatColor.YELLOW + "You are now tracking " + ChatColor.RED + trackedPlayer.getName() + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    private ArrayList<ItemStack> getTrackerList(Player player) {
        ArrayList<SGPlayer> alivePlayers = getPlayersWithState(PlayerState.ALIVE);
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        ArrayList<ItemStack> list = new ArrayList<>();
        for (SGPlayer p : alivePlayers) {
            ItemStack head = new ItemStack(Material.LIME_DYE);
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + Bukkit.getPlayer(p.uuid).getName());
            ArrayList<String> trackerLore = new ArrayList<>();
            double distanceSquared = plugin.getPlayerData().getPlayer(p).getLocation().distanceSquared(player.getLocation());
            int distance = (int)Math.sqrt(distanceSquared);
            trackerLore.add(ChatColor.GRAY + "Distance: " + distance);
            meta.setLore(trackerLore);
            head.setItemMeta(meta);
            //SkullMeta skullMeta = (SkullMeta) meta;
            //PlayerProfile profile = PlayerProfileCache.playerProfiles.get(player.getUniqueId());
            //skullMeta.setOwnerProfile(profile);
            //head.setItemMeta(skullMeta);
            list.add(head);
        }
        return list;
    }

    public ArrayList<SGPlayer> getPlayersWithState(PlayerState... pState) {
        PlayerData playerData = plugin.getPlayerData();
        ArrayList<SGPlayer> players = new ArrayList<>();

        for (PlayerState state : pState) {
            List<SGPlayer> matching = Bukkit.getOnlinePlayers().stream()
                    .map(playerData::getPlayer)
                    .filter(p -> p.state.equals(state))
                    .collect(Collectors.toList());
            players.addAll(matching);
        }
        return players;
    }
}
