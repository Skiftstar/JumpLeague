package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class partsCMD implements CommandExecutor {

    private Main plugin;

    public partsCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("parts").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Util.getMess("playerOnlyCommand"));
            return false;
        }
        Player p = (Player) commandSender;
        if (!p.hasPermission("pixelleague.partsList")) {
            p.sendMessage(Util.getMess("NEPermissions"));
            return false;
        }
        String finalMess = Util.getMess("partsList");
        if (plugin.partsConfig.get("easy") == null || plugin.partsConfig.getConfigurationSection("easy").getKeys(false).size() == 0) {
            finalMess = finalMess.replace("%easy", Util.getMess("partsListNone"));
        } else {
            String temp = "";
            for (String name : plugin.partsConfig.getConfigurationSection("easy").getKeys(false)) {
                temp += name + ", ";
            }
            temp = temp.substring(0, temp.length() - 2);
            finalMess = finalMess.replace("%easy", temp);
        }
        if (plugin.partsConfig.get("medium") == null || plugin.partsConfig.getConfigurationSection("medium").getKeys(false).size() == 0) {
            finalMess = finalMess.replace("%med", Util.getMess("partsListNone"));
        } else {
            String temp = "";
            for (String name : plugin.partsConfig.getConfigurationSection("medium").getKeys(false)) {
                temp += name + ", ";
            }
            temp = temp.substring(0, temp.length() - 2);
            finalMess = finalMess.replace("%med", temp);
        }
        if (plugin.partsConfig.get("hard") == null || plugin.partsConfig.getConfigurationSection("hard").getKeys(false).size() == 0) {
            finalMess = finalMess.replace("%hard", Util.getMess("partsListNone"));
        } else {
            String temp = "";
            for (String name : plugin.partsConfig.getConfigurationSection("hard").getKeys(false)) {
                temp += name + ", ";
            }
            temp = temp.substring(0, temp.length() - 2);
            finalMess = finalMess.replace("%hard", temp);
        }
        if (plugin.partsConfig.get("hardcore") == null || plugin.partsConfig.getConfigurationSection("hardcore").getKeys(false).size() == 0) {
            finalMess = finalMess.replace("%hc", Util.getMess("partsListNone"));
        } else {
            String temp = "";
            for (String name : plugin.partsConfig.getConfigurationSection("hardcore").getKeys(false)) {
                temp += name + ", ";
            }
            temp = temp.substring(0, temp.length() - 2);
            finalMess = finalMess.replace("%hc", temp);
        }
        p.sendMessage(finalMess);
        return true;
    }
}
