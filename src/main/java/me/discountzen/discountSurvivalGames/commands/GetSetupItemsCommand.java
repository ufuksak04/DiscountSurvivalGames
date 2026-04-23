package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GetSetupItemsCommand implements CommandExecutor {
    private final DiscountSurvivalGames plugin;

    public GetSetupItemsCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Inventory inv = player.getInventory();
        inv.addItem(ItemBuilder.makeItem(ChatColor.LIGHT_PURPLE + "pedestal", Material.STICK));
        inv.addItem(ItemBuilder.makeItem(ChatColor.GREEN + "1", Material.STICK));
        inv.addItem(ItemBuilder.makeItem(ChatColor.BLUE + "2", Material.STICK));
        inv.addItem(ItemBuilder.makeItem(ChatColor.GOLD + "3", Material.STICK));
        inv.addItem(ItemBuilder.makeItem(ChatColor.LIGHT_PURPLE + "Debug Stick", Material.STICK));

        return true;
    }
}
