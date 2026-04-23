package me.discountzen.discountSurvivalGames.listeners;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {
    private final DiscountSurvivalGames plugin;
    private final double EPSILON = 1e-4;

    public PlayerMoveListener(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player =  event.getPlayer();
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        if (mem.state.equals(PlayerState.WAITING)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null) return;

            // check position change (x,y,z)
            boolean posChanged = Math.abs(from.getX() - to.getX()) > EPSILON
                    || Math.abs(from.getY() - to.getY()) > EPSILON
                    || Math.abs(from.getZ() - to.getZ()) > EPSILON;

            boolean rotChanged = Math.abs(from.getYaw() - to.getYaw()) > 0.0001f
                    || Math.abs(from.getPitch() - to.getPitch()) > 0.0001f;

            if (!posChanged && rotChanged) {
                // only changed look: allow it by letting the 'to' keep yaw/pitch,
                // but keep position identical
                Location keep = from.clone();
                keep.setYaw(to.getYaw());
                keep.setPitch(to.getPitch());
                event.setTo(keep);
                return;
            }

            if (posChanged) {
                // Player tried to move: prevent micro-movement by forcing them back.
                // Build the locked location with player's new look
                Location locked = from.clone();
                locked.setYaw(to.getYaw());
                locked.setPitch(to.getPitch());

                // First, set the event 'to' so server won't move them this tick
                event.setTo(locked);

                // Clear any velocity immediately to avoid momentum-based slipping
                event.getPlayer().setVelocity(new Vector(0, 0, 0));

                // Teleport on next tick to guarantee server-client sync (fixes edge cases)
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!event.getPlayer().isOnline()) return;
                    // Only teleport if player is still slightly off the locked pos
                    Location now = event.getPlayer().getLocation();
                    if (now.getWorld().equals(locked.getWorld())
                            && (Math.abs(now.getX() - locked.getX()) > EPSILON
                            || Math.abs(now.getY() - locked.getY()) > EPSILON
                            || Math.abs(now.getZ() - locked.getZ()) > EPSILON)) {
                        event.getPlayer().teleport(locked);
                        event.getPlayer().setVelocity(new Vector(0, 0, 0));
                    }
                });
            }
        }

    }
}
