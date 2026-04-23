package me.discountzen.discountSurvivalGames.util;

import org.bukkit.profile.PlayerProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfileCache {
    public static Map<UUID, PlayerProfile> playerProfiles = new HashMap<UUID, PlayerProfile>();
}
