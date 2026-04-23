package me.discountzen.discountSurvivalGames.classes;

import me.discountzen.discountSurvivalGames.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootTableJson {

    private ArrayList<Map<String, String>> entries = new ArrayList<>();

    public ItemStack getRandomItem() {
        Map<String, String> entry = getRandomEntry();
        String upperName = entry.get("material").toUpperCase(Locale.ROOT).trim();
        for (CustomItems custom : CustomItems.values()) {
            if (upperName.equalsIgnoreCase(custom.name())) {
                return getCustomItem(custom);
            }
        }
        Material material = Material.getMaterial(upperName);
        ItemStack item;
        if (material == null) {
            Bukkit.getLogger().warning("[DiscountSurvivalGames] Invalid material: '" + upperName + "' in entry: " + entry);
            item = new ItemStack(Material.DIRT);
            return item;
        }
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants = parseEnchants(entry.get("enchantments"));
        item = new ItemStack(material);
        item.setAmount(parseAmount(entry.get("amount")));
        if (enchants != null) {
            item.addEnchantments(enchants);
        }
        return item;
    }

    private Map<String, String> getRandomEntry() {
        int totalWeight = entries.stream().mapToInt(i -> Integer.parseInt(i.get("weight"))).sum();
        int roll = new Random().nextInt(totalWeight);
        int current = 0;
        for (Map<String, String> i : entries) {
            int weight = Integer.parseInt(i.get("weight"));
            current += weight;
            if (roll < current) return i;
        }

        return entries.getLast();

    }

    private int parseAmount(String val) {
        int min, max;
        if (val.split("-").length == 2) {
            min = Integer.parseInt(val.substring(0, val.lastIndexOf('-')));
            max = Integer.parseInt(val.substring(val.lastIndexOf('-')+1));
            return new Random().nextInt(max-min+1) + min;
        }
        else {
            return Integer.parseInt(val);
        }
    }

    private Map<Enchantment, Integer> parseEnchants(String val) {
        if (val.equals("")) {
            return null;
        }
        else {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            for (String s : val.split(",")) {
                String enchantment = s.substring(0, s.lastIndexOf(':'));
                int level = Integer.parseInt(s.substring(s.lastIndexOf(':')+1));
                enchantments.put(Enchantment.getByName(enchantment), level);
            }
            return enchantments;
        }
    }

    private ItemStack getCustomItem(CustomItems custom) {
        switch(custom) {
            case CustomItems.PLAYER_TRACKER:
                ItemStack item = ItemBuilder.makeItem(ChatColor.GREEN + "Player Tracker", Material.COMPASS);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Player Tracker");
                return item;
            default:
                return null;
        }
    }

    private enum CustomItems {
        PLAYER_TRACKER
    }
}
