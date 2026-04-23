package me.discountzen.discountSurvivalGames.tasks;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.classes.WorldFile;
import me.discountzen.discountSurvivalGames.storage.ConfigManager;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameControllerTask extends BukkitRunnable {
    private final DiscountSurvivalGames plugin;
    private ConfigManager config;
    private GameManager gameManager;
    private PlayerData data;

    public final int gameID;

    // Map & World
    public MapEntry map;
    public final World world;
    private final WorldFile worldFile;
    public Location mapMiddle;
    private ArrayList<Location> pedestals;
    public int timer_preGame = 0;
    public int timer_Game = 0;
    public int timer_Refill = 0;
    public boolean countdownStarted = false;

    public GameControllerTask(DiscountSurvivalGames plugin, int gameID) {
        this.plugin = plugin;
        this.gameID = gameID;
        this.world = Bukkit.getWorld("game_" + gameID);
        this.map = plugin.getGameManager().gameMaps.get(gameID);
        this.worldFile = plugin.getGameManager().gameWorldFiles.get(map.worldID);

        world.getEntities().stream().filter(e -> e instanceof Item).forEach(Entity::remove);
        WorldConfiguration();
        mapMiddle = worldFile.getMapMiddle();
        mapMiddle.setWorld(world);
        this.pedestals = new ArrayList<Location>();
        for (Location loc : worldFile.getPedestals()) {
            loc.setWorld(world);
            this.pedestals.add(loc);
        }

        config = plugin.config;
        gameManager = plugin.getGameManager();
        data = plugin.getPlayerData();
        plugin.getLogger().info("Game Task #" + gameID + " initialized.");

        timer_preGame = config.startCountdown;
        timer_Game = config.gameDuration;
        timer_Refill = config.refillCountdown;

        WorldBorder border = world.getWorldBorder();
        border.setCenter(mapMiddle);
        border.setSize(worldFile.getBorderStartSize());
        border.setDamageAmount(config.borderDamageAmount);
        border.setWarningDistance(0);
        border.setDamageBuffer(0);
        border.setWarningTime(1);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (data.getPlayer(player).state.equals(PlayerState.LOBBY)) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Game has been initialized on " + ChatColor.WHITE + map.name + ChatColor.LIGHT_PURPLE + "! The game is now ready to join. " + ChatColor.WHITE + "(/join [<game id>])");
            }
        }
    }

    public ArrayList<SGPlayer> players = new ArrayList<>();
    public GamePhase phase = GamePhase.LOBBY;

    private ArrayList<SGPlayer> lastPlayerCache = new ArrayList<>();
    private ArrayList<SGPlayer> topThree = new ArrayList<>();



    @Override
    public void run() {
        switch(phase) {
            case LOBBY:
                LobbyPhase();
                break;
            case GAME:
                GamePhase();
                break;
            case DEATHMATCH:
                DeathmatchPhase();
                break;
            default:
                break;
        }
        UpdateScoreboard();
    }

    private void LobbyPhase() {
        ArrayList<SGPlayer> lobbyPlayers = getPlayersWithState(PlayerState.WAITING);
        if (lobbyPlayers.size() >= config.minimumPlayers) {
            if (!countdownStarted) {
                countdownStarted = true;
                lobbyPlayers.forEach(p -> {
                    p.sendMessage(ChatColor.YELLOW + "Minimum number of players reached!");
                    p.sendMessage(ChatColor.YELLOW + "Starting game in " + ChatColor.WHITE + timer_preGame + ChatColor.YELLOW + " seconds!");
                    p.getPlayer().playSound(p.getPlayer(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 5f, 5f);
                });
            } else {
                timer_preGame--;
            }
        } else {
            if (countdownStarted) {
                timer_preGame = config.startCountdown;
                countdownStarted = false;
                lobbyPlayers.forEach(p -> { p.sendMessage(ChatColor.YELLOW + "Dropped below minimum player count, countdown stopped!"); });
            }
        }
        if (timer_preGame == 10 || (timer_preGame <= 5 && timer_preGame > 0)) {
            lobbyPlayers.forEach(p -> {
                p.sendMessage(ChatColor.YELLOW + "Starting game in " + ChatColor.WHITE
                    + timer_preGame + ChatColor.YELLOW + "...");
                p.getPlayer().playSound(p.getPlayer(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 5f, 5f);
            });
        }
        if (timer_preGame <= 0) {
            phase = GamePhase.GAME;
            ChestUtils.GenerateChestLoot(plugin, this, false, true);
            lobbyPlayers.forEach(p -> {
                p.state = PlayerState.ALIVE;
                p.sendMessage(ChatColor.YELLOW + "Game Start! Last one standing wins!");
                p.getPlayer().playSound(p.getPlayer(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5f, 1f);
            });
        }
    }

    private void GamePhase() {
        timer_Game--;
        timer_Refill--;
        ArrayList<SGPlayer> gamePlayers = getPlayersWithState(PlayerState.ALIVE, PlayerState.SPECTATOR);
        if (timer_Game == 60 || timer_Game == 30 || timer_Game == 10 || (timer_Game <= 5 && timer_Game > 0)) {
            gamePlayers.forEach(p -> {
                p.sendMessage(ChatColor.YELLOW
                        + "Deathmatch starting in " + ChatColor.RED + timer_Game + ChatColor.YELLOW + "...");
                p.getPlayer().playSound(p.getPlayer(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 5f, 5f);
            });
        }
        if (timer_Game == 0) {
            phase = GamePhase.DEATHMATCH;
            gamePlayers.forEach(p -> {
                        p.sendMessage(ChatColor.YELLOW + "Deathmatch started!");
                        p.getPlayer().playSound(p.getPlayer(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.3f);});
            WorldBorder border = world.getWorldBorder();
            border.setSize(worldFile.getBorderDeathmatchSize());
            border.setSize(1, config.deathmatchDuration);
            for (SGPlayer mem : getPlayersWithState(PlayerState.ALIVE)) {
                Player player = mem.getPlayer();
                Random r = new Random();
                int index = r.nextInt(pedestals.size());
                Location target = pedestals.get(index);
                pedestals.remove((int)index);
                target.setDirection(mapMiddle.toVector().subtract(target.toVector()).normalize());
                player.teleport(target);
            }
        }
        if (timer_Refill == 0) {
            gamePlayers.forEach(p -> {
                        p.sendMessage(ChatColor.YELLOW + "Chests have been refilled!");
                        p.sendMessage(ChatColor.RED + "Border will now begin to shrink!");
                        p.getPlayer().playSound(p.getPlayer(), Sound.BLOCK_CHEST_OPEN, 5f, 5f);});
            WorldBorder border = world.getWorldBorder();
            border.setSize(worldFile.getBorderEndSize(), config.gameDuration - config.refillCountdown);
            ChestRefill();
        }
    }

    private void DeathmatchPhase() {
        //
    }

    private void UpdateScoreboard() {
        String eventText = "";
        switch (phase) {
            case LOBBY:
                if (countdownStarted) {
                    eventText = ChatColor.GRAY + "Game Start: " + ChatColor.WHITE + TextUtils.PrettyTime(timer_preGame);
                } else {
                    eventText = ChatColor.GRAY + "Game Start: " + ChatColor.WHITE + players.size() + "/" + plugin.config.minimumPlayers;
                }
                break;
            case GAME:
                if (timer_Refill > 0) {
                    eventText = ChatColor.GREEN + "Chest Refill: " + TextUtils.PrettyTime(timer_Refill);
                } else {
                    eventText = ChatColor.RED + "Deathmatch: " + TextUtils.PrettyTime(timer_Game);
                }
                break;
            case DEATHMATCH:
                eventText = ChatColor.RED + "Deathmatch: In Progress";
                break;
            case POSTGAME:
                eventText = ChatColor.GREEN + "Send to hub: ";
                break;
        }
        for (SGPlayer mem : players) {
            mem.updateMatchBoard(getPlayersWithState(PlayerState.ALIVE).size(), eventText);
        }
    }

    public void acceptPlayer(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline()) {
            Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
            new Exception().printStackTrace();
            return;
        }
        Player player = (Player) offlinePlayer;
        Random r = new Random();
        int index = r.nextInt(pedestals.size());
        Location target = pedestals.get(index);
        pedestals.remove((int)index);
        target.setY(target.getY());
        player.getInventory().clear();
        target.setDirection(mapMiddle.toVector().subtract(target.toVector()).normalize());
        target.setWorld(world);
        player.teleport(target);
        player.setCollidable(true);
        /*
        if (!PlayerProfileCache.playerProfiles.containsKey(player.getUniqueId())) {
            PlayerProfile profile = player.getPlayerProfile();
            PlayerProfileCache.playerProfiles.put(player.getUniqueId(), profile);
        }

         */
        SGPlayer mem = data.getPlayer(uuid);
        mem.state = PlayerState.WAITING;
        mem.setGameID(gameID);
        player.setScoreboard(mem.m_board);
        players.add(mem);
    }

    public void ChestRefill() { ChestUtils.GenerateChestLoot(plugin, this, true, true); }

    public ArrayList<SGPlayer> getPlayersWithState(PlayerState... pState) {
        ArrayList<SGPlayer> result = new ArrayList<>();
        for (PlayerState state : pState) {
            List<SGPlayer> matching = players.stream()
                    .filter(p -> p.state.equals(state))
                    .collect(Collectors.toList());
            result.addAll(matching);
        }
        return result;
    }

    private void GameEnd(ArrayList<SGPlayer> topThree) {
        ArrayList<String> header = new ArrayList<>(), body = new ArrayList<>(), footer = new ArrayList<>();
        header.add(TextUtils.CenterString(ChatColor.GOLD + "" + ChatColor.BOLD + "Game End Summary"));
        body.add(TextUtils.CenterString(ChatColor.YELLOW + "" + ChatColor.BOLD + "Winner " + ChatColor.RESET
                + ChatColor.GRAY + "- " + ChatColor.GOLD + topThree.get(2).getName()));
        footer.add(TextUtils.CenterString(ChatColor.YELLOW + "" + ChatColor.BOLD + "1st " + ChatColor.RESET
                + ChatColor.GRAY + "- " + ChatColor.WHITE + topThree.get(2).getName() + ChatColor.GRAY
                + " - " + ChatColor.GREEN + topThree.get(2).kills + " kills"));
        footer.add(TextUtils.CenterString(ChatColor.YELLOW + "" + ChatColor.BOLD + "2nd " + ChatColor.RESET
                + ChatColor.GRAY + "- " + ChatColor.WHITE + topThree.get(1).getName() + ChatColor.GRAY
                + " - " + ChatColor.GREEN + topThree.get(1).kills + " kills"));
        footer.add(TextUtils.CenterString(ChatColor.YELLOW + "" + ChatColor.BOLD + "3rd " + ChatColor.RESET
                + ChatColor.GRAY + "- " + ChatColor.WHITE + topThree.get(0).getName() + ChatColor.GRAY
                + " - " + ChatColor.GREEN + topThree.get(0).kills + " kills"));
        for (SGPlayer mem : getPlayersWithState(PlayerState.ALIVE, PlayerState.SPECTATOR)) {
            Player p = mem.getPlayer();
            TextUtils.SendMessageArray(p, TextUtils.BoxedMessage(header, body, footer, ChatColor.translateAlternateColorCodes('&', "&e")));
        }
        OfflinePlayer offlineWinner = Bukkit.getOfflinePlayer(topThree.get(2).uuid);
        if (!offlineWinner.isOnline()) {
            Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
            new Exception().printStackTrace();
            return;
        }
        Player winner = (Player) offlineWinner;
        data.getPlayer(winner).wins++;
        world.getWorldBorder().setSize(worldFile.getBorderStartSize());
        phase = GamePhase.POSTGAME;
        for (SGPlayer mem : players) {
            Player player = mem.getPlayer();
            player.sendMessage(ChatColor.GREEN + "Sending to hub...");
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (SGPlayer mem : players) {
                Player player = mem.getPlayer();
                gameManager.SendPlayerToHub(player);
            }
        }, 20 * 5L);
        gameManager.games.remove(gameID);
        Bukkit.getScheduler().runTaskLater(plugin, () -> { gameManager.CleanupGame(gameID, this); }, 20 * 8L);
    }

    public void CheckAlivePlayerCount(SGPlayer dead) {
        ArrayList<SGPlayer> alivePlayers = getPlayersWithState(PlayerState.ALIVE);
        ArrayList<SGPlayer> gamePlayers = getPlayersWithState(PlayerState.ALIVE, PlayerState.SPECTATOR);

        int alive = alivePlayers.size();

        // 0. Announce when player count drops but game is not ending
        if (alive <= 5 && alive != 1) {
            String msg = ChatColor.YELLOW + "There are only "
                    + ChatColor.RED + alive
                    + ChatColor.YELLOW + " players remaining!";
            gamePlayers.forEach(p -> p.sendMessage(msg));
        }

        // 2. When dropping to 2: determine 3rd place
        if (alive == 2) {
            topThree.add(dead);
            return;
        }

        // 3. When dropping to 1: determine 2nd + 1st place, then end game
        if (alive == 1) {
            topThree.add(dead);
            SGPlayer winner = alivePlayers.get(0);
            if (!topThree.contains(winner)) {
                topThree.add(winner); // first place
            }

            // At this point: [third, second, first]
            sanitizeTopThree();

            GameEnd(topThree);
        }
    }

    private void sanitizeTopThree() {
        // remove duplicates while keeping order
        ArrayList<SGPlayer> clean = new ArrayList<>();
        for (SGPlayer p : topThree) {
            if (!clean.contains(p)) clean.add(p);
        }
        topThree = clean;

        // ensure no more than 3 players
        while (topThree.size() > 3) {
            topThree.remove(0);
        }
    }

    private SGPlayer findEliminated(List<SGPlayer> previous, List<SGPlayer> current) {
        for (SGPlayer p : previous) {
            if (!current.contains(p)) return p;
        }
        return null;
    }

    public void OverrideStartTimer() {
        timer_preGame = 1;
    }

    private void WorldConfiguration() {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.LOCATOR_BAR, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_VINES_SPREAD, false);
        world.setDifficulty(Difficulty.HARD);
        world.setTime(12000);
        world.setClearWeatherDuration(999999);
    }
}
