package me.discountzen.discountSurvivalGames.classes;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MapEntry {

    private static ArrayList<MapEntry> maps = new ArrayList<MapEntry>();
    public String name;
    public UUID worldID;

    public MapEntry(String name, UUID worldID) {
        this.name = name;
        this.worldID = worldID;
    }

    public static boolean deleteMap(DiscountSurvivalGames plugin, String name) {
        File file = new File(plugin.getDataFolder(), "map entries/" + name + ".json");
        return file.delete();
    }

    public static boolean addMap(DiscountSurvivalGames plugin, String name, UUID worldID) {
        File file = new File(plugin.getDataFolder(), "map entries/" + name + ".json");
        if (file.exists()) {
            return false;
        }
        try {
            file.getParentFile().mkdir();
            file.createNewFile();
            JsonUtils.writeJson(file, new MapEntry(name, worldID));
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void cacheMaps(DiscountSurvivalGames plugin) {
        File dir = new File(plugin.getDataFolder(), "map entries");
        maps.clear();
        for (File file : dir.listFiles()) {
            maps.add((MapEntry)JsonUtils.readJson(file, MapEntry.class));
        }
    }

    public static ArrayList<MapEntry> getMaps() {
        return maps;
    }
}
