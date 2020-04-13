package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class delpartCMD implements CommandExecutor {

    private Main plugin;

    public delpartCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("delpart").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelleague.delpart")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        if (strings.length < 2) {
            p.sendMessage(Util.getMess("delPartUsage"));
            return false;
        }
        if (!(strings[0].equalsIgnoreCase("easy") || strings[0].equalsIgnoreCase("medium") || strings[0].equalsIgnoreCase("hard") || strings[0].equalsIgnoreCase("hardcore"))) {
            p.sendMessage(Util.getMess("delPartUsage"));
            return false;
        }
        if (plugin.partsConfig.get(strings[0].toLowerCase() + "." + strings[1].toLowerCase()) == null) {
            p.sendMessage(Util.getMess("partNonExistent"));
            return false;
        }
        plugin.partsConfig.set(strings[0].toLowerCase() + "." + strings[1].toLowerCase(), null);
        plugin.savePartsConfig();
        p.sendMessage(Util.getMess("partDeleted"));
        return true;
    }
}
