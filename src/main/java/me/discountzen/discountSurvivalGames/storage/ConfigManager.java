package me.discountzen.discountSurvivalGames.storage;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ConfigManager {
    private final DiscountSurvivalGames plugin;

    public String hubMap;
    public int minimumPlayers;
    public int maximumPlayers;
    public int startCountdown;
    public int refillCountdown;
    public int chestFillAmount;
    public int gameDuration;
    public int deathmatchDuration;
    public double borderDamageAmount;
    public boolean gameQueuerEnabled;


    public ConfigManager(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
    }

    private <T> T read(String path, Class<T> type) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Object value = config.get(path);
        if (value == null) {
            for (ConfigValues val : ConfigValues.values()) {
                if (val.getKey().equalsIgnoreCase(path)) {
                    return (T) val.getDefaultValue();
                }
            }
            return null;
        }

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        if (type == Integer.class && value instanceof Number)
            return type.cast(((Number) value).intValue());
        else if (type == Double.class && value instanceof Number)
            return type.cast(((Number) value).doubleValue());
        else if (type == Long.class && value instanceof Number)
            return type.cast(((Number) value).longValue());
        else if (type == Boolean.class && value instanceof String)
            return type.cast(Boolean.parseBoolean((String) value));

        for (ConfigValues val : ConfigValues.values()) {
            if (val.getKey().equalsIgnoreCase(path)) {
                return type.cast(val.getDefaultValue());
            }
        }

        throw new IllegalArgumentException("Cannot cast value at " + path + " to " + type.getSimpleName());

    }

    public void ReloadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile);
                writer.write("# Good morning\n");
                writer.write("# Discount Survival Games Plugin!! v0.1\n");
                writer.write("minimum-players: " + ConfigValues.MinimumPlayers.getDefaultValue() + "\n");
                writer.write("maximum-players: " + ConfigValues.MaximumPlayers.getDefaultValue() + "\n");
                writer.write("start-countdown: " + ConfigValues.StartCountdown.getDefaultValue() + "\n");
                writer.write("game-duration: " + ConfigValues.GameDuration.getDefaultValue() +"\n");
                writer.write("refill-countdown: " + ConfigValues.RefillCountdown.getDefaultValue() + "\n");
                writer.write("chest-fill-amount: " + ConfigValues.ChestFillAmount.getDefaultValue() + "\n");
                writer.write("hub-map: " + ConfigValues.HubMap.getDefaultValue().toString() + "\n");
                writer.write("deathmatch-duration: " + ConfigValues.DeathmatchDuration.getDefaultValue() + "\n");
                writer.write("border-damage-amount: " + ConfigValues.BorderDamageAmount.getDefaultValue() + "\n");
                writer.write("game-queuer-enabled: " + ConfigValues.GameQueuerEnabled.getDefaultValue() + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        minimumPlayers = read("minimum-players", Integer.class);
        maximumPlayers = read("maximum-players", Integer.class);
        startCountdown = read("start-countdown", Integer.class);
        gameDuration = read("game-duration", Integer.class);
        refillCountdown = read("refill-countdown", Integer.class);
        chestFillAmount = read("chest-fill-amount", Integer.class);
        hubMap = read("hub-map", String.class);
        deathmatchDuration = read("deathmatch-duration", Integer.class);
        borderDamageAmount = read("border-damage-amount", Double.class);
        gameQueuerEnabled = read("game-queuer-enabled", Boolean.class);
    }

    private enum ConfigValues {
        MinimumPlayers("minimum-players", 3),
        MaximumPlayers("minimum-players", 24),
        StartCountdown("start-countdown", 20),
        GameDuration("game-duration", 300),
        RefillCountdown("refill-countdown", 210),
        ChestFillAmount("chest-fill-amount", 15),
        HubMap("hub-map", "hub"),
        DeathmatchDuration("deathmatch-duration", 60),
        BorderDamageAmount("border-damage-amount", 1D),
        GameQueuerEnabled("game-queuer-enabled", true);

        private String key;
        private Object defaultValue;

        ConfigValues(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }





}
