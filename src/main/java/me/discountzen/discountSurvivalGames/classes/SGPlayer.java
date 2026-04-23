package me.discountzen.discountSurvivalGames.classes;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.ItemBuilder;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class SGPlayer {

    public UUID uuid;
    public PlayerState state = PlayerState.LOBBY;
    public int totalKills;
    public int kills;
    public int wins;

    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    public Scoreboard m_board;
    private Objective m;
    private Team m_kills;
    private Team m_players;
    private Team m_event;
    public Scoreboard l_board;
    private Objective l;
    private Team l_kills;
    private Team l_wins;

    private int gameID = 999999;

    public SGPlayer(UUID uuid, GameManager gameManager) {
        this.uuid = uuid;
        this.gameManager = gameManager;
        this.scoreboardManager = gameManager.scoreboardManager;
        m_board = scoreboardManager.getNewScoreboard();
        m = m_board.registerNewObjective("game", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "GAME");
        m.setDisplaySlot(DisplaySlot.SIDEBAR);

        m.getScore(" ").setScore(8);
        m.getScore("§eNext Event:").setScore(6);
        m_event = m_board.registerNewTeam("m_event");
        m_event.addEntry("§1");
        m.getScore("§1").setScore(5);
        m.getScore("  ").setScore(4);
        m_players = m_board.registerNewTeam("m_players");
        m_players.addEntry("§2");
        m.getScore("§2").setScore(3);
        m_kills = m_board.registerNewTeam("m_kills");
        m_kills.addEntry("§3");
        m.getScore("§3").setScore(2);
        m.getScore("   ").setScore(1);
        m.getScore(ChatColor.GOLD + "discord.gg/bXZx8GyeW8").setScore(0);

        l_board = scoreboardManager.getNewScoreboard();
        l = l_board.registerNewObjective("lobby", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "LOBBY");
        l.setDisplaySlot(DisplaySlot.SIDEBAR);

        l.getScore("   ").setScore(6);
        l.getScore("§ePlayer Stats").setScore(5);
        l.getScore("  ").setScore(4);
        l_kills = l_board.registerNewTeam("l_kills");
        l_kills.addEntry("§3");
        l.getScore("§3").setScore(3);
        l_wins = l_board.registerNewTeam("l_wins");
        l_wins.addEntry("§4");
        l.getScore("§4").setScore(2);
        l.getScore(" ").setScore(1);
        l.getScore(ChatColor.GOLD + "discord.gg/bXZx8GyeW8").setScore(0);
    }

    public String getName() {
        return getPlayer().getName();
    }
    public Player getPlayer() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline()) {
            Bukkit.getLogger().warning("GHOST PLAYER ACCESS DETECTED:");
            new Exception().printStackTrace();
            return null;
        }
        return (Player) offlinePlayer;
    }

    public void setGameID(int gameID) { this.gameID = gameID; }
    public int getGameID() { return this.gameID; }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }


    public void MakeSpectator(DiscountSurvivalGames plugin, boolean value) {
        Player player = getPlayer();
        player.setAllowFlight(value);
        player.setFlying(value);
        if (value) {
            state = PlayerState.SPECTATOR;
            player.getInventory().clear();
            ItemStack tracker = ItemBuilder.makeItem(ChatColor.GREEN + "Player Tracker", Material.COMPASS);
            CompassMeta trackerMeta = (CompassMeta) tracker.getItemMeta();
            trackerMeta.setEnchantmentGlintOverride(false);
            tracker.setItemMeta(trackerMeta);
            player.getInventory().setItem(0, tracker);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
            plugin.getGameManager().invis.addEntry(player.getName());
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(plugin, player);
            }
        }
        else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            plugin.getGameManager().invis.removeEntry(player.getName());
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(plugin, player);
            }
        }
    }

    public void updateMatchBoard(int alivePlayers, String eventText) {
        m_event.setSuffix(eventText);
        m_players.setSuffix(ChatColor.YELLOW + "Players: " + ChatColor.WHITE + String.valueOf(alivePlayers));
        m_kills.setSuffix(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + kills);
    }

    public void updateLobbyBoard() {
        l_kills.setSuffix(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + totalKills);
        l_wins.setSuffix(ChatColor.YELLOW + "Wins: " + ChatColor.WHITE + wins);
    }
}
