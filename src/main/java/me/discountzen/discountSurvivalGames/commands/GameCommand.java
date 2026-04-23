package me.discountzen.discountSurvivalGames.commands;

import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.MapEntry;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.tasks.GameControllerTask;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.GamePhase;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import me.discountzen.discountSurvivalGames.util.TextUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameCommand implements CommandExecutor, TabCompleter {
    private final DiscountSurvivalGames plugin;

    public GameCommand(DiscountSurvivalGames plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        SGPlayer mem = plugin.getPlayerData().getPlayer(player);
        int gameID = mem.getGameID();
        if (gameID != 999999) {
            GameManager gm = plugin.getGameManager();
            GameControllerTask game = gm.games.get(gameID);
            if (args.length == 0) {
                SendGameInfo(game, player);
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("refill")) {
                    game.players.forEach(p -> p.sendMessage(ChatColor.YELLOW + "Chests have been refilled!"));
                    game.ChestRefill();
                }
                else if (args[0].equalsIgnoreCase("skip")) {
                    if (game.timer_preGame > 1) {
                        game.timer_preGame = 1;
                        game.players.forEach(p -> p.sendMessage(ChatColor.YELLOW + "Event skipped by " + ChatColor.WHITE + player.getName() + ChatColor.YELLOW + "!"));
                    }
                    else {
                        if (game.timer_Refill > 1) {
                            int diff = game.timer_Refill - 1;
                            game.timer_Refill = 1;
                            game.timer_Game -= diff;
                            game.players.forEach(p -> p.sendMessage(ChatColor.YELLOW + "Event skipped by " + ChatColor.WHITE + player.getName() + ChatColor.YELLOW + "!"));
                        }
                        else {
                            if (game.timer_Game > 1) {
                                game.timer_Game = 1;
                                game.players.forEach(p -> p.sendMessage(ChatColor.YELLOW + "Event skipped by " + ChatColor.WHITE + player.getName() + ChatColor.YELLOW + "!"));
                            }
                            else {
                                player.sendMessage(ChatColor.RED + "No event to skip!");
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("info")) {
                    SendGameInfo(game, player);
                }
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "You are not in a game!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("refill", "skip", "info");
        }
        else if (args.length == 1) {
            return Arrays.asList("refill", "skip", "info")
                    .stream()
                    .filter(value -> value.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        /*
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("config")) {
                return Arrays.asList("reload")
                        .stream()
                        .filter(value -> value.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());

            }
        }
         */
        return Collections.emptyList();
    }

    private void SendGameInfo(GameControllerTask game, Player player) {
        ArrayList<String> header = new ArrayList<>();
        header.add(TextUtils.CenterString(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "GAME ID: " + ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + game.gameID));
        ArrayList<String> body = new ArrayList<>();
        body.add(TextUtils.CenterString(ChatColor.GRAY + "Map: " + ChatColor.DARK_AQUA + game.map.name));
        body.add(TextUtils.CenterString(ChatColor.GRAY + "Game Phase: " + ChatColor.DARK_AQUA + TextUtils.TitleCase(game.phase.name())));
        if (game.phase.equals(GamePhase.LOBBY) || game.phase.equals(GamePhase.POSTGAME)) {
            body.add(TextUtils.CenterString(ChatColor.GRAY + "Players: " + ChatColor.DARK_AQUA + game.players.size()));
        }
        else {
            body.add(TextUtils.CenterString(ChatColor.GRAY + "Survivors: " + ChatColor.DARK_AQUA + game.getPlayersWithState(PlayerState.ALIVE).size()));
            body.add(TextUtils.CenterString(ChatColor.GRAY + "Spectators: " + ChatColor.DARK_AQUA + game.getPlayersWithState(PlayerState.SPECTATOR).size()));
        }
        TextUtils.SendMessageArray(player, TextUtils.BoxedMessage(header, body, ChatColor.translateAlternateColorCodes('&', "&f")));
    }
}
