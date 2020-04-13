package Yukami.PixelLeague;

import Yukami.PixelLeague.Saver_Loader.Loader;
import Yukami.PixelLeague.Saver_Loader.SaveCommand;
import Yukami.PixelLeague.Saver_Loader.Saver;
import Yukami.PixelLeague.Saver_Loader.posListener.pos;
import Yukami.PixelLeague.Saver_Loader.setPvPStartCMD;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public FileConfiguration partsConfig, chestConfig;
    public pos pos;
    public boolean gameActive = true;
    private static Main instance;
    public ConsoleCommandSender console;
    public static FileConfiguration messages;
    public int titleLength, tagDuration, partDistance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public void onEnable() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        instance = this;
        Server server = this.getServer();
        console = server.getConsoleSender();
        loadConfig();
        createPartsConfig();
        createChestConfig();
        loadMessageConfig();
        Util.loadMessages();
        titleLength = getConfig().getInt("titleDisplayLength") * 20;
        tagDuration = getConfig().getInt("tagDuration") * 20;
        partDistance = getConfig().getInt("partDistance");
        Saver.setMain(this);
        Loader.setMain(this);
        pos = new pos(this);
        new startCommand(this);
        new setPvPStartCMD(this);
        new JoinListener(this);
        new setLobbySpawnCMD(this);
        new SaveCommand(this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Main getInstance() {
        return instance;
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void savePartsConfig() {
        File file = new File(getDataFolder() + File.separator + "parts.yml");
        try {
            partsConfig.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPartsConfig() {
        File file = new File(getDataFolder() + File.separator + "parts.yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            partsConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createChestConfig() {
        File file = new File(getDataFolder() + File.separator + "chestItems.yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            chestConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMessageConfig() {
        File file = new File(getDataFolder() + File.separator + "messages.yml");
        try {
            if (!file.exists()) {
                InputStream in = getResource("messages.yml");
                Files.copy(in, file.toPath());
            }
            messages = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
