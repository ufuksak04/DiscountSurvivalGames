package me.discountzen.discountSurvivalGames.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Object writeJson(File file, Object data) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readJson(File file, Type type) {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

}
