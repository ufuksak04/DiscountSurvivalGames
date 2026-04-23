package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.tasks.ShowChestsTask;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class ShowChestsCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public ShowChestsCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    public Collection<Entity> spawnedMarkers = new ArrayList<>();
    private BukkitTask task;
    private Map<UUID, Boolean> cache = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            if (cache.containsKey(player.getUniqueId())) {
                if (!cache.get(player.getUniqueId())) {
                    cache.put(player.getUniqueId(), true);
                    EnableVisuals(player);
                }
                else {
                    cache.put(player.getUniqueId(), false);
                    DisableVisuals(player);
                }
            } else {
                cache.put(player.getUniqueId(), true);
                EnableVisuals(player);
            }
        } else {
            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Invalid arguments.");
        }
        return true;
    }

    private void EnableVisuals(Player player) {
        task = new ShowChestsTask(plugin, player).runTaskTimer(plugin, 0L, 5L);
        plugin.tasksToCancel.add(task);
        player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Chest visuals enabled.");
    }

    private void DisableVisuals(Player player) {
        task.cancel();
        plugin.tasksToCancel.remove(task);
        ShowChestsTask temp = (ShowChestsTask) task;
        for (ArmorStand stand : temp.cache.values()) {
            if (stand != null && !stand.isDead()) stand.remove();
            temp.cache.clear();
        }
        player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Chest visuals disabled.");
    }

}
