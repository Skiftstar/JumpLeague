package Yukami.PixelLeague;

import Yukami.PixelLeague.Saver_Loader.Loader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class startCommand implements CommandExecutor {

    public startCommand(Main plugin) {
        plugin.getCommand("leagueStart").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelLeague.leagueStart")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Loader.generateParcour(players);
        return false;
    }
}
