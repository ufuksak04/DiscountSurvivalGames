package me.discountzen.discountSurvivalGames.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.gui.PlayerTrackerMenu;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;

public class GameManager {
    private final DiscountSurvivalGames plugin;
    public final ScoreboardManager scoreboardManager;

    public GameManager(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
        this.scoreboardManager = Bukkit.getScoreboardManager();
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().startsWith("game_")) {
                worldDeletionQueue.add(file);
            }
        }
        invis = scoreboardManager.getMainScoreboard().getTeam("invis");
        if (invis == null) invis = scoreboardManager.getMainScoreboard().registerNewTeam("invis");
        invis.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER); // players don’t collide
        invis.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER); // hide nametag
        invis.setCanSeeFriendlyInvisibles(true);
    }

    public Team invis;

    // game id - task
    public Map<Integer, GameControllerTask> games = new HashMap<>();
    // game id - world
    public Map<Integer, World> gameWorlds = new HashMap<>();
    // game id - map entry
    public Map<Integer, MapEntry> gameMaps = new HashMap<>();
    // world uuid - world file
    public Map<UUID, WorldFile> gameWorldFiles = new HashMap<>();

    private ArrayList<File> worldDeletionQueue = new ArrayList<>();
    private long start;

    public boolean CreateGame() {
        return InitializeGame(GetRandomMap());
    }

    public boolean CreateGame(MapEntry map) {
        return InitializeGame(map);
    }

    public boolean InitializeGame(MapEntry map) {
        int gameID = generateGameID();
        if (gameID == 105) {
            return false;
        }
        gameMaps.put(gameID, map);
        start = System.currentTimeMillis();
        new File(Bukkit.getWorldContainer(), "game_" + gameID).mkdirs();
        plugin.getLogger().info("World directory created in " + (System.currentTimeMillis() - start) + "ms");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File templateMapFile = new File(Bukkit.getWorldContainer(), map.name + "/region");
            File gameMapFile = new File(Bukkit.getWorldContainer(), "game_" + gameID + "/region");
            gameMapFile.mkdirs();
            copyFileStructure(templateMapFile, gameMapFile);
            templateMapFile = new File(Bukkit.getWorldContainer(), map.name + "/entities");
            gameMapFile = new File(Bukkit.getWorldContainer(), "game_" + gameID + "/entities");
            gameMapFile.mkdirs();
            copyFileStructure(templateMapFile, gameMapFile);
            templateMapFile = new File(Bukkit.getWorldContainer(), map.name + "/poi");
            gameMapFile = new File(Bukkit.getWorldContainer(), "game_" + gameID + "/poi");
            gameMapFile.mkdirs();
            copyFileStructure(templateMapFile, gameMapFile);

            Bukkit.getScheduler().runTask(plugin, () -> {
                start = System.currentTimeMillis();
                WorldCreator creator = new WorldCreator("game_" + gameID)
                        .generator(new ChunkGenerator() {
                            @Override
                            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                                return createChunkData(world);
                            }
                        });
                creator.generateStructures(false);
                creator.keepSpawnInMemory(false);
                plugin.getLogger().info("Chunks generated in " + (System.currentTimeMillis() - start) + "ms");
                start = System.currentTimeMillis();
                World world = Bukkit.createWorld(creator);
                world.setKeepSpawnInMemory(false);
                world.setAutoSave(false);
                plugin.getLogger().info("World created in " + (System.currentTimeMillis() - start) + "ms");
                start = System.currentTimeMillis();
                int radius = 7;
                List<int[]> chunkCoords = new ArrayList<>();
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        chunkCoords.add(new int[]{x, z});
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int batch = 5; // number of chunks per tick
                        for (int i = 0; i < batch && !chunkCoords.isEmpty(); i++) {
                            int[] coords = chunkCoords.remove(0);
                            world.getChunkAt(coords[0], coords[1]).load(true);
                        }
                        if (chunkCoords.isEmpty()) {
                            cancel();
                            plugin.getLogger().info("Chunks pre-generated in " + (System.currentTimeMillis() - start) + "ms");
                        }
                    }
                }.runTaskTimer(plugin, 0L, 4L);
                LoadGame(gameID);
            });
            //copyRegions(templateMapFile, gameMapFile);
            //copyFileStructure(templateMapFile, gameMapFile);
        });
        return true;
    }

    public void LoadGame(int gameID) {
        gameWorlds.put(gameID, Bukkit.getWorld("game_" + gameID));
        GameControllerTask task = new GameControllerTask(plugin, gameID);
        task.runTaskTimer(plugin, 0L, 20L);
        games.put(gameID, task);
    }

    private int generateGameID() {
        int tries = 5;
        for (int i = 0; i < tries; i++) {
            int random = new Random().nextInt(101);
            boolean found = false;
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (file.getName().startsWith("game_")) {
                    int fileID = Integer.parseInt(file.getName().split("_")[1]);
                    if (fileID == random) {
                        found = true;
                    }
                }
            }
            if (!found) {
                return random;
            }
        }
        return 105;
    }

    public void JoinGame(UUID uuid, int gameID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline()) {
            Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
            new Exception().printStackTrace();
            return;
        }
        Player player = (Player) offlinePlayer;
        if (games.containsKey(gameID)) {
            GameControllerTask game = games.get(gameID);
            if (game.phase.equals(GamePhase.LOBBY)) {
                games.get(gameID).acceptPlayer(uuid);
            } else {
                player.sendMessage(ChatColor.RED + "This game has already started!");
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "There is no valid game with this ID.");
        }
    }

    public void JoinGame(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline()) {
            Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
            new Exception().printStackTrace();
            return;
        }
        Player player = (Player) offlinePlayer;
        ArrayList<GameControllerTask> availableGames = new ArrayList<>();
        ArrayList<GameControllerTask> startingGames = new ArrayList<>();
        for (GameControllerTask game : games.values()) {
            if (game.phase.equals(GamePhase.LOBBY) && game.players.size() < plugin.config.maximumPlayers) {
                if (game.countdownStarted) {
                    startingGames.add(game);
                }
                availableGames.add(game);
            }
        }
        if (!startingGames.isEmpty()) {
            PrioritySystem(startingGames, player);
        } else {
            if (!availableGames.isEmpty()) {
                RandomSystem(availableGames, player);
            } else {
                player.sendMessage(ChatColor.RED + "There are currently no available games!");
            }
        }
    }

    private void PrioritySystem(ArrayList<GameControllerTask> priorityList, Player player) {
        GameControllerTask priorityGame = priorityList.getFirst();
        for (GameControllerTask game : priorityList) {
            if (game.timer_preGame < priorityGame.timer_preGame) {
                priorityGame = game;
            }
        }
        games.get(priorityGame.gameID).acceptPlayer(player.getUniqueId());
    }

    private void RandomSystem(ArrayList<GameControllerTask> availableList, Player player) {
        int playerCount = 0;
        GameControllerTask highestPlayerCount = availableList.getFirst();
        for (GameControllerTask game : availableList) {
            playerCount += game.players.size();
            if (game.players.size() > highestPlayerCount.players.size()) {
                highestPlayerCount = game;
            }
        }
        if (playerCount == 0) {
            GameControllerTask randomGame = availableList.get(new Random().nextInt(availableList.size()));
            games.get(randomGame.gameID).acceptPlayer(player.getUniqueId());
        }
        else {
            games.get(highestPlayerCount.gameID).acceptPlayer(player.getUniqueId());
        }
    }

    public void AbortGame(int gameID) {
        GameControllerTask game = games.get(gameID);
        for (SGPlayer p : game.players) {
            p.sendMessage(ChatColor.RED + "This game has been aborted.");
            p.sendMessage(ChatColor.GREEN + "Sending to hub...");
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (SGPlayer p : game.players) SendPlayerToHub(p);
            }, 20*2L);
        games.remove(gameID);
        Bukkit.getScheduler().runTaskLater(plugin, () -> { CleanupGame(gameID, game); }, 20*5L);
    }

    public void CleanupGame(int gameID, GameControllerTask gameTask) {
        File gameFile = new File(Bukkit.getWorldContainer(), "game_" + gameID);
        World gameWorld = gameTask.world;
        gameMaps.remove(gameID);
        gameWorlds.remove(gameID);
        Bukkit.unloadWorld(gameWorld, false);
        worldDeletionQueue.add(gameFile);
        Bukkit.getScheduler().runTaskLater(plugin, () -> { gameTask.cancel(); }, 100L);
    }

    public void CleanupQueue() {
        if (!worldDeletionQueue.isEmpty()) {
            File file = worldDeletionQueue.get(new Random().nextInt(worldDeletionQueue.size()));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("dsg.admin")).forEach(p -> {
                    p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "DSG" + ChatColor.RESET + ChatColor.GRAY + "» " + ChatColor.DARK_AQUA +
                            "Beginning world deletion for world " + ChatColor.WHITE + file.getName() + ChatColor.DARK_AQUA + " and removing it from queue.");
                });
                Bukkit.getLogger().info("Beginning world deletion for world " + file.getName() + " and removing it from queue.");
                RecursiveDelete(file);
                worldDeletionQueue.remove(file);
            });
        }
    }

    public void SendPlayerToHub(SGPlayer mem) {
        Player player = mem.getPlayer();
        SendPlayerToHub(player, mem);

    }
    public void SendPlayerToHub(Player player) {
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        SendPlayerToHub(player, mem);

    }
    private void SendPlayerToHub(Player player, SGPlayer mem) {
        Location target = Bukkit.getWorld(plugin.config.hubMap).getSpawnLocation().clone();
        if (target.getX() < 0) { target.add(-0.5, 0, 0); } else { target.add(0.5, 0, 0); }
        if (target.getY() < 0) { target.add(0, 0, -0.5); } else { target.add(0, 0, 0.5); }
        if (mem.getGameID() != 999999) {
            GameControllerTask game = games.get(mem.getGameID());
            if (game != null) game.players.remove(mem);
        }
        PlayerTrackerMenu.tracking.remove(player.getUniqueId());
        mem.kills = 0;
        mem.MakeSpectator(plugin, false);
        mem.state = PlayerState.LOBBY;
        mem.setGameID(999999);
        player.setScoreboard(mem.l_board);
        player.getInventory().clear();
        player.setCollidable(false);
        player.teleport(target);
        mem.updateLobbyBoard();
    }

    private MapEntry GetRandomMap() {
        Random r = new Random();
        ArrayList<MapEntry> maps = MapEntry.getMaps();
        boolean mapFound = false;
        while (!mapFound) {
            int num = r.nextInt(maps.size());
            MapEntry map = maps.get(num);
            mapFound = true;
            return map;
        }
        return null;
    }

    public void ReadWorldFiles(DiscountSurvivalGames plugin) {
        gameWorldFiles.clear();
        File dir = new File(plugin.getDataFolder(), "sg maps");
        for (File f : dir.listFiles()) {
            UUID worldID = UUID.fromString(f.getName().split(".json")[0]);
            gameWorldFiles.put(worldID, (WorldFile) JsonUtils.readJson(f, WorldFile.class));
        }
    }

    private void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean RecursiveDelete(File file) {
        if (!file.exists()) return true;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                RecursiveDelete(f);
            }
        }

        boolean result = file.delete();
        if (!result) {
            Bukkit.getLogger().warning("[DiscountSurvivalGames] Failed to delete: " + file.getAbsolutePath());
        }
        return result;
    }
}
