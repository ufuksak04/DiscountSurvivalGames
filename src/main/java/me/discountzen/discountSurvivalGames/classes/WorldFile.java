package me.discountzen.discountSurvivalGames.classes;


import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class WorldFile {

    public String name;
    public UUID worldID;
    private ArrayList<Loc> pedestals;
    private ArrayList<SGChest> chests;
    private Loc mapMiddle;
    private int borderStartSize;
    private int borderEndSize;
    private int borderDeathmatchSize;

    public WorldFile(UUID worldID, String name) {
        this.worldID = worldID;
        this.name = name;
        this.chests = new ArrayList<>();
        this.pedestals = new ArrayList<>();
    }

    public void setMapMiddle(Location mapMiddle) {
        this.mapMiddle = new Loc(mapMiddle.getX(), mapMiddle.getY(), mapMiddle.getZ());
    }

    public Location getMapMiddle() {
        return new Location(Bukkit.getWorld(worldID), mapMiddle.x, mapMiddle.y, mapMiddle.z);
    }

    public void setBorderStartSize(int size) {
        this.borderStartSize = size;
    }

    public int getBorderStartSize() {
        return this.borderStartSize;
    }

    public void setBorderEndSize(int size) {
        this.borderEndSize = size;
    }

    public int getBorderEndSize() {
        return this.borderEndSize;
    }

    public void setBorderDeathmatchSize(int size) {
        this.borderDeathmatchSize = size;
    }

    public int getBorderDeathmatchSize() {
        return this.borderDeathmatchSize;
    }

    public void addPedestal(Location xyz) {
        pedestals.add(new Loc(xyz.getX(), xyz.getY(), xyz.getZ()));
    }

    public ArrayList<Location> getPedestals() {
        ArrayList<Location> result = new ArrayList<>();
        for (Loc loc : pedestals) {
            result.add(new Location(Bukkit.getWorld(worldID), loc.x, loc.y, loc.z));
        }
        return new ArrayList<>(result);
    }

    public void addChest(Location xyz, Integer tier) {
        boolean found = false;
        if (chests == null) chests = new ArrayList<>();
        if (!chests.isEmpty()) {
            for (SGChest chest : chests) {
                if (chests.contains(chest)) {
                    found = true;
                    chests.get(chests.indexOf(chest)).tier = tier;
                    break;
                }
            }
        }
        chests.add(new SGChest(xyz.getX(), xyz.getY(), xyz.getZ(), tier));
    }

    public void removeChest(Location xyz) {
        if (chests == null) return;
        if (chests.isEmpty()) return;
        for (SGChest chest : chests) {
            if (chest.x == xyz.getX() && chest.y == xyz.getY() && chest.z == xyz.getZ()) {
                chests.remove(chest);
                return;
            }
        }
    }

    public Integer getTier(Location xyz) {
        for (SGChest chest : chests) {
            if (chest.x == xyz.getX() && chest.y == xyz.getY() && chest.z == xyz.getZ()) {
                chests.remove(chest);
                return chest.tier;
            }
        }
        return 1;
    }

    public Map<Location, Integer> getChests() {
        Map<Location, Integer> result = new HashMap<>();
        if (chests == null) return result;
        if (chests.isEmpty()) return result;
        for (SGChest chest : chests) {
            result.put(new Location(Bukkit.getWorld(worldID), chest.x, chest.y, chest.z), chest.tier);
        }
        return result;
    }

    private static class Loc {

        public double x;
        public double y;
        public double z;

        public Loc(double x, double y, double z) {
            this.x = FormatCoordinate(x);
            this.y = FormatCoordinate(y);
            this.z = FormatCoordinate(z);
            if (this.x > 0) { this.x += 0.5D; } else { this.x -= 0.5D; }
            if (this.z > 0) { this.z += 0.5D; } else { this.z -= 0.5D; }
        }

        private double FormatCoordinate(double val) {
            return ((Number)val).intValue();
        }
    }

    private static class SGChest {

        public int x;
        public int y;
        public int z;
        public int tier;

        public SGChest(double x, double y, double z, int tier) {
            this.x = FormatCoordinate(x);
            this.y = FormatCoordinate(y);
            this.z = FormatCoordinate(z);
            this.tier = tier;
        }

        private int FormatCoordinate(double val) {
            return ((Number)val).intValue();
        }
    }
}