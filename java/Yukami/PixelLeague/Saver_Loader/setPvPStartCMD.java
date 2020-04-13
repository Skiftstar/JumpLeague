package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class setPvPStartCMD implements CommandExecutor {

    private Main plugin;

    public setPvPStartCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("setPvPSpawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelLeague.setPvPSpawn")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        Location loc = p.getLocation();
        List<String> spawns;
        if (plugin.getConfig().get("pvpSpawns") == null) {
            spawns = new ArrayList<>();
        } else {
            spawns = plugin.getConfig().getStringList("pvpSpawns");
        }
        spawns.add(Saver.convert(loc.getBlock()));
        plugin.getConfig().set("pvpSpawns", spawns);
        plugin.saveConfig();
        p.sendMessage(Util.getMess("pvpSpawnSet"));
        return true;
    }
}
