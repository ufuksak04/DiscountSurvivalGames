package me.discountzen.discountSurvivalGames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.commands.*;
import me.discountzen.discountSurvivalGames.listeners.*;
import me.discountzen.discountSurvivalGames.storage.ConfigManager;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.storage.PlayerJsonWriter;
import me.discountzen.discountSurvivalGames.tasks.MainThreadTask;
import me.discountzen.discountSurvivalGames.tasks.PlayerTrackerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import me.discountzen.discountSurvivalGames.util.SQLUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public final class DiscountSurvivalGames extends JavaPlugin {
    private Connection connection;
    private SQLUtils sqlUtils;
    private PlayerData data;
    private GameManager gameManager;
    public ConfigManager config;
    public ArrayList<BukkitTask> tasksToCancel = new ArrayList<>();
    public boolean databaseExists = false;
    public ChatColor primaryColor = ChatColor.GOLD;
    public ChatColor accentColor = ChatColor.YELLOW;
    public String pluginPrefix = primaryColor + "" + ChatColor.BOLD + "DSG" + ChatColor.RESET + "" + ChatColor.GRAY + "» ";

    @Override
    public void onEnable() {
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");
        if (dbUser == null || dbPass == null) {
            getLogger().severe("Could not connect to database!");
            databaseExists = false;
        }
        else {
            try {
                dbConnect("localhost", 3306, "mcserver", dbUser, dbPass);
                databaseExists = true;
            } catch (SQLException e) {
                getLogger().severe("Could not connect to database!");
                databaseExists = false;
            }
        }

        sqlUtils = new SQLUtils(connection, this);
        gameManager = new GameManager(this);
        data = new PlayerData(this);
        config = new ConfigManager(this);
        config.ReloadConfig();
        MapEntry.cacheMaps(this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            gameManager.ReadWorldFiles(this);
        });

        getCommand("discountsurvivalgames").setExecutor(new DiscountSurvivalGamesCommand(this));
        getCommand("discountsurvivalgames").setTabCompleter(new DiscountSurvivalGamesCommand(this));

        getCommand("makesgchest").setExecutor(new MakeSGChestCommand(this));

        getCommand("setsgworldborder").setExecutor(new SetSGWorldBorderCommand(this));
        getCommand("setsgworldborder").setTabCompleter(new SetSGWorldBorderCommand(this));

        getCommand("startgame").setExecutor(new StartGameCommand(this));
        getCommand("startgame").setTabCompleter(new StartGameCommand(this));

        getCommand("game").setExecutor(new GameCommand(this));
        getCommand("game").setTabCompleter(new GameCommand(this));

        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("map").setTabCompleter(new MapCommand(this));


        getCommand("setspawnpedestal").setExecutor(new SetSpawnPedestalCommand(this));
        getCommand("setmapmiddle").setExecutor(new SetMapMiddleCommand(this));
        getCommand("setworldspawn").setExecutor(new SetWorldSpawnCommand(this));
        getCommand("join").setExecutor(new JoinCommand(this));
        getCommand("leave").setExecutor(new LeaveCommand(this));
        getCommand("games").setExecutor(new GamesCommand(this));
        getCommand("abortgame").setExecutor(new AbortGameCommand(this));
        getCommand("showchests").setExecutor(new ShowChestsCommand(this));
        getCommand("getsetupitems").setExecutor(new GetSetupItemsCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("dev").setExecutor(new DevCommand(this));

        new MainThreadTask(this).runTaskTimer(this, 0L, 20L);
        new PlayerTrackerTask(this).runTaskTimer(this, 0L, 3L);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(this), this);

        Bukkit.getLogger().info("[DiscountSurvivalGames] Plugin has been enabled!");

    }

    @Override
    public void onDisable() {
        tasksToCancel.forEach(BukkitTask::cancel);
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerJsonWriter.SerializedPlayerData jsonData = new PlayerJsonWriter.SerializedPlayerData(p.getUniqueId());
            SGPlayer m = data.getPlayer(p);
            jsonData.setWins(m.wins);
            jsonData.setTotalKills(m.totalKills);
            PlayerJsonWriter.WritePlayerData(this, jsonData);
        }
        /*
        if (databaseExists) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                sqlUtils.flushMemory(
                        "INSERT INTO player_data (uuid, kills, wins) VALUES (?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE kills = ?, wins = ?;",
                        p.getUniqueId().toString(), playerData.getPlayer(p).totalKills, playerData.getPlayer(p).wins,
                        playerData.getPlayer(p).totalKills,  playerData.getPlayer(p).wins
                );
            }
            dbDisconnect();
        }
         */
        Bukkit.getLogger().info("[DiscountSurvivalGames] Plugin has been disabled!");


    }

    public SQLUtils getSQL() {
        return sqlUtils;
    }


    public void dbConnect(String host, int port, String database, String username, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) return;

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("[DiscountSurvivalGames] Database connection successful!");
    }

    public void dbDisconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DiscountSurvivalGames] Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerData getPlayerData() {
        return data;
    }

}
