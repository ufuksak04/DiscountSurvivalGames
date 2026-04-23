package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetWorldSpawnCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public SetWorldSpawnCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Location playerLocation = player.getLocation().clone();
        World world = player.getWorld();
        float roundUp;
        float yaw = playerLocation.getYaw();
        if (Math.abs(yaw % 90) > 45) roundUp = 1;
        else roundUp = 0;
        yaw = 90 * (Math.round(yaw / 90) + roundUp);
        playerLocation.setYaw(yaw);
        playerLocation.setPitch(0f);
        world.setSpawnLocation(playerLocation);
        player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "World spawn Set!");

        return true;
    }
}
