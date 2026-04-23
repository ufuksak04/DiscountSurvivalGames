package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.util.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    private final DiscountSurvivalGames plugin;

    public InventoryCloseListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        //GUIManager.openGUIs.put(event.getPlayer().getUniqueId(), " ");
    }
}
