package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.gui.PlayerTrackerMenu;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GUIManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {
    private final DiscountSurvivalGames plugin;
    private final PlayerTrackerMenu playerTrackerMenu;

    public GUIListener(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
        this.playerTrackerMenu = new PlayerTrackerMenu(plugin);
    }

    @EventHandler
    public void onClick(InventoryInteractEvent event) {

        if (event instanceof InventoryClickEvent e) {
            Player player = (Player)event.getWhoClicked();
            if (event.getView().getTitle().startsWith(GUIManager.getGUICode("Player Tracker"))) {
                playerTrackerMenu.handleClick(e);
                return;
            }
            SGPlayer mem = plugin.getPlayerData().getPlayer(player);
            if (mem.state.equals(PlayerState.DEV)) return;
            if (!mem.state.equals(PlayerState.ALIVE)) {
                event.setCancelled(true);
            }
        }
        else {
            event.setCancelled(true);
        }
        /*
        Player player = (Player)event.getWhoClicked();
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        if (!mem.state.equals(PlayerState.ALIVE)) {
            if (mem.state.equals(PlayerState.DEV) && player.hasPermission("dsg.admin")) {
                return;
            }
            else {
                event.setCancelled(true);
            }
        }
        if (event.getView().getTopInventory().getType() == InventoryType.CHEST) {
            if (event.getView().getTitle().equals("Chest") || event.getView().getTitle().equals("Large Chest")) {
                return;
            }
        }
        String guiType = GUIManager.openGUIs.get(player.getUniqueId());
        if (player.getGameMode() == GameMode.CREATIVE && event.getView().getType() == InventoryType.CREATIVE) return;
        if (guiType==null) return;
        Bukkit.getLogger().info(player.getName() + " " + guiType);
        if (guiType.equalsIgnoreCase(" ")) return;
        //if (event.getView().getTopInventory() != event.getInventory()) return;
        if (guiType.equalsIgnoreCase("PLAYER_TRACKER")) {
            playerTrackerMenu.handleClick(event);
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String name = meta.getDisplayName();
        if (name.equalsIgnoreCase(ChatColor.GREEN + "Player Tracker")) {
            PlayerTrackerMenu gui = new PlayerTrackerMenu(plugin);
            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(gui.getInventory(player)));
        }
         */
    }
}
