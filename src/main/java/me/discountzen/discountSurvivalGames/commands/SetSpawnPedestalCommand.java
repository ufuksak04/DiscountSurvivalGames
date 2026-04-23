package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SetSpawnPedestalCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public SetSpawnPedestalCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player =  (Player) sender;
        Location playerLocation = player.getLocation();
        File file = new File(plugin.getDataFolder(), "sg maps/" + playerLocation.getWorld().getUID() + ".json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                World world = playerLocation.getWorld();
                JsonUtils.writeJson(file, new WorldFile(world.getUID(), world.getName()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            WorldFile worldFile = (WorldFile) JsonUtils.readJson(file, WorldFile.class);
            worldFile.addPedestal(playerLocation);
            JsonUtils.writeJson(file, worldFile);
            player.sendMessage(plugin.pluginPrefix + plugin.primaryColor + "Spawn Pedestal set.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
