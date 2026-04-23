package me.discountzen.discountSurvivalGames.util;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.listeners.PlayerQuitListener;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeDeathSystem {
    public static void Trigger(DiscountSurvivalGames plugin, Player player, int gameID, DeathCause deathCause) {
        String message = "";
        if (deathCause.equals(DeathCause.QUIT)) {
            message = ChatColor.RED + player.getName() + ChatColor.YELLOW + " quit";
            ProcessDeath(plugin, player, gameID, message, false);
        }
        else if (deathCause.equals(DeathCause.BORDER)) {
            message = ChatColor.RED + player.getName()
                    + ChatColor.YELLOW + " succumbed to " + ChatColor.RED + "the border";
            ProcessDeath(plugin, player, gameID, message, true);
        }
        else {
            message = ChatColor.RED + player.getName()
                    + ChatColor.YELLOW + " died";
            ProcessDeath(plugin, player, gameID, message, true);
        }
    }
    public static void Trigger(DiscountSurvivalGames plugin, Player player, int gameID, EntityDamageEvent event) {
        String message = "";
        if (event instanceof EntityDamageByEntityEvent en) {
            Entity damager = en.getDamager();
            if (damager instanceof Player attacker) {
                message = ChatColor.RED + player.getName()
                        + ChatColor.YELLOW + " was slain by " + ChatColor.RED + attacker.getName();
            }
            else if (damager instanceof Arrow arrow) {
                Player attacker = (Player) arrow.getShooter();
                message = ChatColor.RED + player.getName()
                        + ChatColor.YELLOW + " was shot by " + ChatColor.RED + attacker.getName();
            }
            else {
                String name = damager.getType().name().replace("_" ," ");
                message = ChatColor.RED + player.getName()
                        + ChatColor.YELLOW + " was slain by " + ChatColor.RED + TextUtils.TitleCase(name);
            }
        }
        else {
            String name = event.getCause().name().replace("_" ," ");
            message = ChatColor.RED + player.getName()
                        + ChatColor.YELLOW + " succumbed to " + ChatColor.RED + TextUtils.TitleCase(name);
        }
        ProcessDeath(plugin, player, gameID, message, true);
    }

    private static void ProcessDeath(DiscountSurvivalGames plugin, Player player, int gameID, String deathMessage, boolean makeSpectator) {
        Location deathLocation = player.getLocation();
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        GameManager gameManager = plugin.getGameManager();
        ItemStack[] items = player.getInventory().getContents();
        if (makeSpectator) {
            mem.MakeSpectator(plugin, true);
        }
        else {
            if (player.isOnline()) gameManager.SendPlayerToHub(player);
        }
        player.setHealth(player.getMaxHealth());
        GameControllerTask game = gameManager.games.get(gameID);
        ArrayList<SGPlayer> gamePlayers = game.getPlayersWithState(PlayerState.ALIVE, PlayerState.SPECTATOR);
        World world = deathLocation.getWorld();
        world.strikeLightningEffect(deathLocation);
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;
            world.dropItemNaturally(deathLocation, item.clone());
        }
        for (SGPlayer sg : gamePlayers) {
            sg.sendMessage(deathMessage);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> { game.CheckAlivePlayerCount(mem); }, 20L);

    }

    public enum DeathCause {
        BORDER,
        FALL,
        BURN,
        QUIT,
        KILL
    }
}
