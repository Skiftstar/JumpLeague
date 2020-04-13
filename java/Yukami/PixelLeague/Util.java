package Yukami.PixelLeague;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static Map<String, String> lang = new HashMap<>();

    public static String getMess(String messName) {
        if (!lang.containsKey(messName)) {
            Main.getInstance().console.sendMessage(ChatColor.RED + "[" + Main.getInstance().getDescription().getName() + "] Couldn't find the following message: " + ChatColor.BOLD + messName);
            return "Message not found, Check console!";
        } else {
            return lang.get(messName);
        }
    }

    public static void loadMessages() {
        FileConfiguration config = Main.messages;
        for (String key : config.getKeys(false)) {
            for (String messName : config.getConfigurationSection(key).getKeys(false)) {
                String message = ChatColor.translateAlternateColorCodes('&', config.getString(key + "." + messName));
                lang.put(messName, message);
            }
        }
    }
}
