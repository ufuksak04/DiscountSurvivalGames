package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.FakeDeathSystem;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.ItemBuilder;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EntityDamageListener implements Listener {
    private final DiscountSurvivalGames plugin;
    private final GameManager gameManager;
    private final PlayerData data;

    public EntityDamageListener(DiscountSurvivalGames plugin) { this.plugin = plugin; this.gameManager = plugin.getGameManager(); this.data = plugin.getPlayerData(); }



    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent en) {
            DamageByEntity(en);
        }
        else {
            Damage(e);
        }
    }

    private void DamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame frame) {
            Player p = null;
            if (e.getDamager() instanceof Player pl) {
                p = pl;
            } else {
                if (e.getDamager() instanceof Projectile proj) {
                    if (proj.getShooter() instanceof Player shooter) {
                        p = shooter;
                        proj.remove();
                    }
                }
            }
            if (p != null) {
                if (data.getPlayer(p).state.equals(PlayerState.DEV) && p.hasPermission("dsg.admin.configure.map")) return;
                if (frame.getItem().getType() != Material.AIR) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (!(e.getEntity() instanceof Player victim)) return;

        Player attacker;
        if (e.getDamager() instanceof Player p) attacker = p;
        else if (e.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter) attacker = shooter;
        else return;
        SGPlayer vMem = data.getPlayer(victim);
        SGPlayer aMem =  data.getPlayer(attacker);
        if (vMem.state != PlayerState.ALIVE || aMem.state != PlayerState.ALIVE) {
            e.setCancelled(true);
            return;
            }
        double finalHealth = victim.getHealth() - e.getFinalDamage();
        if (finalHealth <= 0) {
            e.setCancelled(true);
            FakeDeathSystem.Trigger(plugin, victim, vMem.getGameID(), e);
            aMem.kills++;
            aMem.totalKills++;
        }
    }

    private void Damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;
        SGPlayer mem = data.getPlayer(victim);
        if (!mem.state.equals(PlayerState.ALIVE)) {
            e.setCancelled(true);
            return;
        }

        double finalHealth = victim.getHealth() - e.getFinalDamage();
        if (finalHealth <= 0) {
            e.setCancelled(true);
            if (e.getDamageSource().getDamageType().equals(DamageType.OUTSIDE_BORDER)) {
                FakeDeathSystem.Trigger(plugin, victim, mem.getGameID(), FakeDeathSystem.DeathCause.BORDER);
            }
            else {
                FakeDeathSystem.Trigger(plugin, victim, mem.getGameID(), e);
            }
        }
    }
}
