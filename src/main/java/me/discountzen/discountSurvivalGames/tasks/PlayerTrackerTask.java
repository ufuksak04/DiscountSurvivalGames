package me.discountzen.discountSurvivalGames.tasks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.gui.PlayerTrackerMenu;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.ItemBuilder;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PlayerTrackerTask extends BukkitRunnable {
    private final DiscountSurvivalGames plugin;


    public PlayerTrackerTask(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
        data = plugin.getPlayerData();
        gameManager = plugin.getGameManager();



    }
    private final PlayerData data;
    private final GameManager gameManager;

    @Override
    public void run() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for (Player player : Bukkit.getOnlinePlayers()) {

            Location target;
            GameControllerTask game;
            if (data.getPlayer(player).getGameID() != 999999) {
                game = gameManager.games.get(data.getPlayer(player).getGameID());
            } else game = null;
            if (game == null) return;


            if (PlayerTrackerMenu.tracking.containsKey(player.getUniqueId())) {
                UUID targetUUID = PlayerTrackerMenu.tracking.get(player.getUniqueId());
                OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUUID);
                if (offlineTarget.isOnline()) {
                    if (offlineTarget.getPlayer().getWorld().equals(player.getWorld())) {
                        target = offlineTarget.getPlayer().getLocation();
                    } else { target = game.mapMiddle; }
                } else { target = game.mapMiddle; }
            } else { target = game.mapMiddle; }

            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.getType().equals(Material.AIR)) continue;
                if (!item.getType().equals(Material.COMPASS)) continue;
                if (!item.hasItemMeta()) continue;
                if (!item.getItemMeta().hasDisplayName()) continue;
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Player Tracker")) {
                    if (i < 9) i += 36;
                    pointCompass(target, protocolManager, player, i);
                }
            }
        }
    }

    private void pointCompass(Location target, ProtocolManager protocolManager, Player player, int slot) {
        ItemStack compass = ItemBuilder.makeItem(ChatColor.GREEN + "Player Tracker", Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        meta.setLodestone(target);
        meta.setLodestoneTracked(false);
        meta.setEnchantmentGlintOverride(false);
        compass.setItemMeta(meta);

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);
        packet.getIntegers().writeSafely(0, 0);
        packet.getIntegers().writeSafely(1, 0);
        packet.getIntegers().writeSafely(2, slot);
        packet.getItemModifier().writeSafely(0, compass);

        protocolManager.sendServerPacket(player, packet);
    }
}
