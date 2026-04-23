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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetSGWorldBorderCommand implements CommandExecutor, TabCompleter {
    private final DiscountSurvivalGames plugin;

    public SetSGWorldBorderCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
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
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                try {
                    WorldFile worldFile = (WorldFile) JsonUtils.readJson(file, WorldFile.class);
                    int size = Integer.parseInt(args[1]);
                    worldFile.setBorderStartSize(size);
                    JsonUtils.writeJson(file, worldFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (args[0].equalsIgnoreCase("end")) {
                try {
                    WorldFile worldFile = (WorldFile) JsonUtils.readJson(file, WorldFile.class);
                    int size = Integer.parseInt(args[1]);
                    worldFile.setBorderEndSize(size);
                    JsonUtils.writeJson(file, worldFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if (args[0].equalsIgnoreCase("deathmatch")) {
                try {
                    WorldFile worldFile = (WorldFile) JsonUtils.readJson(file, WorldFile.class);
                    int size = Integer.parseInt(args[1]);
                    worldFile.setBorderDeathmatchSize(size);
                    JsonUtils.writeJson(file, worldFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Invalid args");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("start", "end", "deathmatch");
        }
        else if (args.length == 1) {
            return Arrays.asList("start", "end", "deathmatch")
                    .stream()
                    .filter(value -> value.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
