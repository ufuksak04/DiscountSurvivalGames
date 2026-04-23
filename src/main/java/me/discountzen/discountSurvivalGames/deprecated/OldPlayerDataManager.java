package me.discountzen.discountSurvivalGames.deprecated;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class OldPlayerDataManager {
    private final DiscountSurvivalGames plugin;

    public OldPlayerDataManager(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    public OldPlayerData getPlayerData(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        return (OldPlayerData) JsonUtils.readJson(playerFile, OldPlayerData.class);
    }

    public void updatePlayerData(OldPlayerData playerData) {
        File playerFile = getPlayerFile(playerData.uuid);
        JsonUtils.writeJson(playerFile, playerData);
    }

    private File getPlayerFile(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "players/" + uuid.toString() + ".json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                JsonUtils.writeJson(file, new OldPlayerData(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public boolean playerExists(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "players/" + uuid.toString() + ".json");
        return file.exists();
    }


}
