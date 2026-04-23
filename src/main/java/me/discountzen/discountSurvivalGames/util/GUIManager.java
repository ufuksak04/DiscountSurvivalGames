package me.discountzen.discountSurvivalGames.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    @NotNull
    public static String guiCodePrefix(int code) {
        if ((float)code / 100000f > 1f) {
            char[] converted = String.valueOf(code).toCharArray();
            String result = "";
            for (int i = 0; i < 6; i++) {
                result += "§" + converted[i];
            }
            return result;
        }
        throw new IllegalStateException();
    }

    @NotNull
    public static String getGUICode(String name) {
        for (GUITypes t : GUITypes.values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return guiCodePrefix(t.getCode());
            }
        }
        throw new IllegalStateException();
    }

    public enum GUITypes {
        PlayerTracker("Player Tracker",111111);

        private String name;
        private int code;

        GUITypes(String name, int code) {
            this.name = name;
            this.code = code;
        }

        public String getName() { return this.name; }
        public int getCode() { return this.code; }

    }
}
