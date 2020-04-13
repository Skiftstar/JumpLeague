package Yukami.PixelLeague;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private Main plugin;

    public JoinListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (plugin.gameActive) {
            return;
        }
        Player p = e.getPlayer();
        String s = plugin.getConfig().getString("lobbySpawn");
        String[] divided = s.split(";");
        String[] divided2 = divided[0].split(",");
        String[] divided3 = divided[1].split(","); //divide the strings to get to the information which is seperated by , or ;
        String world = divided2[0];
        double x = Double.parseDouble(divided2[1]);
        double y = Double.parseDouble(divided2[2]);
        double z = Double.parseDouble(divided2[3]);
        Location lobbySpawn = new Location(Bukkit.getWorld(world), x, y, z, -90 ,0);
        p.teleport(lobbySpawn);
    }
}
