package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand implements CommandExecutor {

    private Main plugin;

    public SaveCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("savepart").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Nur für Spieler!");
            return false;
        }
        Player p = (Player) commandSender;

        if (!p.hasPermission("PixelLeague.savePart")) {
            p.sendMessage(Color("&cDu hast nich genügend Rechte!"));
            return false;
        }

        if (strings.length == 0) {
            p.sendMessage(Color("&cCommand Usage:\n/savepart DIFFICULTY\nDifficulty kann Leicht, Mittel, Schwer, Hardcore sein!"));
            return false;
        }
        if (!(strings[0].equalsIgnoreCase("leicht") || strings[0].equalsIgnoreCase("mittel") || strings[0].equalsIgnoreCase("schwer") || strings[0].equalsIgnoreCase("hardcore"))) {
            p.sendMessage(Color("&cCommand Usage:\n/savepart DIFFICULTY\nDifficulty kann Leicht, Mittel, Schwer, Hardcore sein!"));
            return false;
        }
        if (!plugin.pos.hasBothPos(p)) {
            p.sendMessage(Color("&cBitte setze zuerst deine Eckpunkte mit einer Holzschaufel !"));
            return false;
        }
        final Location[] cords = plugin.pos.getPos(p);
        if (Saver.saveArena(cords, strings[0].toLowerCase())) {
            p.sendMessage(Color("&aPart gespeichert!"));
            return true;
        }
        p.sendMessage(Color("&cEs sind entweder mehr als ein/e Ende/Start/Truhe, kein/e Ende/Start/Truhe in dem Part enthalten!\nEs muss genau ein/e Ende/Start/Truhe in dem Part sein!"));


        return false;
    }

    private String Color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
