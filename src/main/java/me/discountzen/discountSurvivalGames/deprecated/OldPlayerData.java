package me.discountzen.discountSurvivalGames.deprecated;

import java.util.UUID;

public class OldPlayerData {

    UUID uuid;
    String username;
    public long join_date;

    public OldPlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.join_date = 0L;
    }

}
