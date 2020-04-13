package Yukami.PixelLeague;

import Yukami.PixelLeague.Saver_Loader.Saver;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class setLobbySpawnCMD implements CommandExecutor {

    private Main plugin;

    public setLobbySpawnCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("setLobbySpawn").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelLeague.setLobbySpawn")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        Location loc = p.getLocation();
        plugin.getConfig().set("lobbySpawn", Saver.convert(loc.getBlock()));
        plugin.saveConfig();
        p.sendMessage(Util.getMess("lobbySpawnSet"));
        return true;
    }
}
