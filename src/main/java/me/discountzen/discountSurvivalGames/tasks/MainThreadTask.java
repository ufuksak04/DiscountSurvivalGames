package me.discountzen.discountSurvivalGames.tasks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import me.discountzen.discountSurvivalGames.DiscountSurvivalGames;
import me.discountzen.discountSurvivalGames.classes.SGPlayer;
import me.discountzen.discountSurvivalGames.storage.PlayerData;
import me.discountzen.discountSurvivalGames.util.GameManager;
import me.discountzen.discountSurvivalGames.util.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;


public class MainThreadTask extends BukkitRunnable {
    private final DiscountSurvivalGames plugin;
    private final PlayerData data;
    private final GameManager gameManager;


    public MainThreadTask(DiscountSurvivalGames plugin) {
        this.plugin = plugin;
        this.data = plugin.getPlayerData();
        gameManager = plugin.getGameManager();
        /*
        Location location = new Location(Bukkit.getWorld(plugin.config.hubMap), 2044.500, 96, 2209.500, 0, 0);
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class, a -> {
            a.setInvisible(false);
            a.setMarker(false);
            a.setInvulnerable(false);
            a.setMarker(false);
            a.setCustomNameVisible(false);
        });
        NamespacedKey key = new NamespacedKey(plugin, "join_button_npc");
        stand.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
         */


        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        // cancel player interact packet if user is a spectator
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (data.getPlayer(player).state.equals(PlayerState.SPECTATOR)) {
                    event.setCancelled(true);
                }
            }
        });
//        PacketContainer pac = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
//        Bukkit.getLogger().info(pac.getStructures().getFields().toString());

        // cancel all sound packet created by player if user is a spectator and if the recipient of the sound is a player who is still participating in the game.
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ENTITY_SOUND) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                int entityId = packet.getIntegers().read(0);

                Entity target = null;
                for (World world : Bukkit.getWorlds()) {
                    for (Entity e : world.getEntities()) {
                        if (e.getEntityId() == entityId) {
                            target = e;
                            break;
                        }
                    }
                }
                if (target instanceof Player player) {
                    SGPlayer mem = data.getPlayer(player);
                    if (!mem.state.equals(PlayerState.ALIVE)) {
                        event.setCancelled(true);
                    }
                    else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (data.getPlayer(p).state.equals(PlayerState.SPECTATOR) && p.getWorld() == player.getWorld()) {
                                event.setCancelled(true); // prevent alive players from seeing/hearing spectator hits
                            }
                        }
                    }
                }
            }
        });
    }

    private int timer = 0;
    private int gamesInQueue = 0;

    @Override
    public void run() {
        if (plugin.config.gameQueuerEnabled) GameQueuer();
        if (timer == 0) {
            timer = 60;
            gameManager.CleanupQueue();
            if (gamesInQueue > 0) StartGameFromQueue();
        }
        timer--;
    }

    private void GameQueuer() {
        if (gameManager.games.size() + gamesInQueue < 3) {
            gamesInQueue++;
        }
    }

    private void StartGameFromQueue() {
        gamesInQueue--;
        GameManager gameManager = plugin.getGameManager();
        gameManager.CreateGame();
    }





}
