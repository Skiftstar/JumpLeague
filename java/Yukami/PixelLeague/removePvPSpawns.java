package Yukami.PixelLeague;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class removePvPSpawns implements CommandExecutor {

    private Main plugin;

    public removePvPSpawns(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("removepvpspawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelleague.removepvpspawn")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        if (strings.length < 1) {
            p.sendMessage(Util.getMess("removePvPSpawnsUsage"));
            return false;
        }
        if (!strings[0].equalsIgnoreCase("all")) {
            int toRemove;
            try {
                toRemove = Integer.parseInt(strings[0]);
            } catch (NumberFormatException e) {
                p.sendMessage(Util.getMess("NaN"));
                return false;
            }
            List<String> spawns = plugin.getConfig().getStringList("pvpSpawns");
            if (spawns.size() < toRemove) {
                p.sendMessage(Util.getMess("spawnsAmountLessThanNumberOfDeletes"));
                return false;
            }
            for (int i = 0; i < toRemove; i++) {
                spawns.remove(spawns.size() - 1);
            }
            plugin.getConfig().set("pvpSpawns", spawns);
        } else {
            plugin.getConfig().set("pvpSpawns", null);
        }
        plugin.saveConfig();

        p.sendMessage(Util.getMess("pvpSpawnsRemoved"));
        return true;
    }
}
