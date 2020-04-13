package Yukami.PixelLeague;

import Yukami.PixelLeague.Saver_Loader.Loader;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class GeneralListener implements Listener {

    private Main plugin;
    public static boolean timerActive = false;
    private static int timerID = -1;
    private static int reqPlayers;
    private static int timerLength;
    private static int currTimer;

    public GeneralListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reqPlayers = plugin.getConfig().getInt("requiredPlayers");
        timerLength = plugin.getConfig().getInt("timerLength") * 20;
        currTimer = timerLength / 20;
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntityType().equals(EntityType.DROPPED_ITEM) || e.getEntityType().equals(EntityType.SPLASH_POTION) || e.getEntityType().equals(EntityType.LINGERING_POTION)) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (plugin.gameActive) {
            return;
        }
        Player p = e.getPlayer();
        if (plugin.getConfig().get("lobbySpawn") == null) {
            p.sendMessage(Util.getMess("noLobbyFound"));
            return;
        }
        String s = plugin.getConfig().getString("lobbySpawn");
        String[] divided = s.split(";");
        String[] divided2 = divided[0].split(",");
        String world = divided2[0];
        double x = Double.parseDouble(divided2[1]);
        double y = Double.parseDouble(divided2[2]);
        double z = Double.parseDouble(divided2[3]);
        Location lobbySpawn = new Location(Bukkit.getWorld(world), x, y, z, -90 ,0);
        p.teleport(lobbySpawn);
        if (Bukkit.getOnlinePlayers().size() >= reqPlayers && !timerActive) {
            currTimer = timerLength / 20;
            timerActive = true;
            timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (currTimer == 0) {
                        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                        Loader.generateParcour(players);
                        Bukkit.getScheduler().cancelTask(timerID);
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(Util.getMess("gameStartTimer").replace("%sec", Integer.toString(currTimer))).create());
                    }
                    currTimer -= 1;
                }
            }, 20, timerLength);
        }
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        if (Bukkit.getOnlinePlayers().size() < reqPlayers && timerActive) {
            stopTask();
        }
    }

    public static void stopTask() {
        timerActive = false;
        Bukkit.getScheduler().cancelTask(timerID);
    }
}
