package me.discountzen.discountSurvivalGames.storage;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.util.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerJsonWriter {

    public static void writePlayerData(DiscountSurvivalGames plugin, SerializedPlayerData data) {
        File file = new File(plugin.getDataFolder(), "players/" + data.getUUID() + ".json");
        JsonUtils.writeJson(file, data);
    }

    public static SerializedPlayerData readPlayerData(DiscountSurvivalGames plugin, UUID uuid) {
        File file = new File(plugin.getDataFolder(), "players/" + uuid + ".json");

        if (!file.exists()) {
            if (!(file.getParentFile().exists())) file.getParentFile().mkdirs();
            SerializedPlayerData fresh = new SerializedPlayerData(uuid);
            writePlayerData(plugin, fresh);

            return fresh;
        }

        SerializedPlayerData loaded = (SerializedPlayerData) JsonUtils.readJson(file, SerializedPlayerData.class);
        return loaded;
    }

    public static class SerializedPlayerData {

        private final UUID uuid;
        private int totalKills;
        private int wins;

        public SerializedPlayerData(UUID uuid) {
            this.uuid = uuid;
            totalKills = 0;
            wins = 0;
        }

        public UUID getUUID() {
            return uuid;
        }

        public int getTotalKills() {
            return totalKills;
        }
        public void setTotalKills(int totalKills) {
            this.totalKills = totalKills;
        }
        public int getWins() {
            return wins;
        }
        public void setWins(int wins) {
            this.wins = wins;
        }
    }

}
