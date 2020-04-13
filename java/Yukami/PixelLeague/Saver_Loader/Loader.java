package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Game.Game;
import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Loader {

    private static Main main;
    private static double defaultLevel = 40.0;
    private static List<String> parts = new ArrayList<>();
    private static Location prevEnding;
    private static int i = 0;
    private static List<String> pastedParts = new ArrayList<>();
    private static List<Location> startPos = new ArrayList<>();
    private static List<Block> blocks = new ArrayList<>();

    private static void setParts(String difficulty) {
        parts.clear();
        blocks.clear();
        Set<String> partsSet = main.partsConfig.getConfigurationSection(difficulty).getKeys(false);
        parts.addAll(partsSet);
        //System.out.println(parts);
    }

    public static void setMain(Main main) {
        Loader.main = main;
    }

    public static void generateParcour(List<Player> players) {
        FileConfiguration config = Main.getInstance().getConfig();
        if (config.getInt("amountEasy") < 1 || config.getInt("amountMedium") < 1 || config.getInt("amountHard") < 1 || config.getInt("amountHardcore") < 1) {
            Main.getInstance().console.sendMessage(Util.getMess("invalidNumberInPartsAmountConsole"));
            Bukkit.broadcastMessage("invalidNumberInPartsAmountBroadcast");
        }
        int amountEasy = config.getInt("amountEasy");
        int amountMedium = config.getInt("amountMedium");
        int amountHard = config.getInt("amountHard");
        int amountHardcore = config.getInt("amountHardcore");
        if (main.partsConfig.getConfigurationSection("leicht").getKeys(false).size() < amountEasy ||
        main.partsConfig.getConfigurationSection("mittel").getKeys(false).size() < amountMedium ||
        main.partsConfig.getConfigurationSection("schwer").getKeys(false).size() < amountHard ||
        main.partsConfig.getConfigurationSection("hardcore").getKeys(false).size() < amountHardcore) {
            Main.getInstance().console.sendMessage(Util.getMess("notEnoughPartsConsole"));
            Bukkit.broadcastMessage("invalidNumberInPartsAmountBroadcast");
        }

        prevEnding = new Location(Bukkit.getServer().getWorld("void"), 0, 0, 0, -90 ,0);
        setParts("leicht");
        Random rand = new Random();
        int index = rand.nextInt(parts.size());
        //Location loc = locFromString(main.partsConfig.getString("leicht." + index + ".end"));
        paste("leicht." + parts.get(index), 0, 40, 0, true, false);
        pastedParts.add("leicht." + parts.get(index));
        parts.remove(parts.get(index));
        for (int i = 0; i < amountEasy - 1; i++) {
            index = rand.nextInt(parts.size());
            paste("leicht." + parts.get(index), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, false);
            //loc = locFromString(main.partsConfig.getString("leicht." + index + ".end"));
            pastedParts.add("leicht." + parts.get(index));
            parts.remove(parts.get(index));
        }
        setParts("mittel");
        for (int i = 0; i < amountMedium; i++) {
            System.out.println(parts);
            index = rand.nextInt(parts.size());
            paste("mittel." + parts.get(index), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, false);
            //loc = locFromString(main.partsConfig.getString("mittel." + index + ".end"));
            pastedParts.add("mittel." + parts.get(index));
            parts.remove(parts.get(index));
        }
        setParts("schwer");
        for (int i = 0; i < amountHard; i++) {
            index = rand.nextInt(parts.size());
            paste("schwer." + parts.get(index), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, false);
            //loc = locFromString(main.partsConfig.getString("schwer." + index + ".end"));
            pastedParts.add("schwer." + parts.get(index));
            parts.remove(parts.get(index));
        }
        setParts("hardcore");
        for (int i = 0; i < amountHardcore; i++) {
            index = rand.nextInt(parts.size());
            if (i == 1) {
                paste("hardcore." + parts.get(index), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, true);
            } else
            {
                paste("hardcore." + parts.get(index), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, false);
            }
            //loc = locFromString(main.partsConfig.getString("hardcore." + index + ".end"));
            pastedParts.add("hardcore." + parts.get(index));
            parts.remove(parts.get(index));
        }
        i++;
        pasteAdditional(players.size() - 1, players);
    }

    public static void pasteAdditional(int amount, List<Player> players) {
        for (int k = 0; k < amount; k++) {
            paste(pastedParts.get(0), 0, 40, (i * Main.getInstance().partDistance), true, false);
            for (int j = 1; j < pastedParts.size(); j++) {
                if (j == pastedParts.size() - 1) {
                    paste(pastedParts.get(j), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, true);
                } else
                {
                    paste(pastedParts.get(j), prevEnding.getX() + 2, prevEnding.getY(), prevEnding.getZ(), false, false);
                }
            }
            i++;
        }
        i = 0;
        pastedParts.clear();
        new Game(main, players.size(), startPos, blocks);
        startPos.clear();
    }

    @SuppressWarnings("deprecation")
    public static void paste(String path, double startX, double startY, double startZ, boolean setStartPos, boolean placeIron) {
        String start = main.partsConfig.getString(path + ".start");
        System.out.println(path);
        Location startLoc = locFromString(start);
        String end = main.partsConfig.getString(path + ".end");
        List<String> blocksList = main.partsConfig.getStringList(path + ".blocks"); //get list from config
        for (String block : blocksList) { //loop through every saved block
            String[] divided = block.split(";");
            String[] divided2 = divided[0].split(",");
            String[] divided3 = divided[1].split(","); //divide the strings to get to the information which is seperated by , or ;
            double x = startX + (Double.parseDouble(divided2[1]) - startLoc.getX());
            double y = startY + (Double.parseDouble(divided2[2]) - startLoc.getY());
            double z = startZ + (Double.parseDouble(divided2[3]) - startLoc.getZ());
            String mat = divided3[0];
            byte data = Byte.parseByte(divided3[1]); //save the information in variables
            Location loc = new Location(Bukkit.getServer().getWorld("void"), x, y, z, -90 ,0); //get the location of the block
            Material material = Material.getMaterial(mat);
            if (material.equals(Material.IRON_PLATE) && !placeIron) {
                prevEnding.setX(loc.getX());
                prevEnding.setY(loc.getY());
                prevEnding.setZ(loc.getZ());
                continue;
            }
            Block b = loc.getBlock(); //get the block of these coordinates
            b.setType(material); //set it to needed material
            b.setData(data); //and set the subID (i.e. rotation of blocks, color of them, etc.)
            if (material.equals(Material.CHEST)) {
                Chest chest = (Chest) b.getState();
                setContents(chest, path.split("\\.")[0]);
            }
            blocks.add(b);
            if (setStartPos && material.equals(Material.GOLD_PLATE)) {
                loc.setY(loc.getY() + 1);
                startPos.add(loc);
            }
        }
    }

    private static void setContents(Chest chest, String difficulty) {
        Random rand = new Random();
        for (String item : Main.getInstance().chestConfig.getConfigurationSection(difficulty).getKeys(false)) {
            int chance = Main.getInstance().chestConfig.getInt(difficulty + "." + item + ".chance");
            int randInt = rand.nextInt(100) + 1;
            if (!(randInt <= chance && randInt >= 1)) {
                continue;
            }
            int maxCount = Main.getInstance().chestConfig.getInt(difficulty + "." + item + ".maxCount");
            int minCount = Main.getInstance().chestConfig.getInt(difficulty + "." + item + ".minCount");
            int count = rand.nextInt((maxCount - minCount) + 1) + minCount;
            if (Material.getMaterial(item.toUpperCase()) == null) {
                Main.getInstance().console.sendMessage(Util.getMess("itemNotFound").replace("%itm", item));
                continue;
            }
            ItemStack is = new ItemStack(Material.getMaterial(item.toUpperCase()), count);
            if (is.getType().equals(Material.POTION) || is.getType().equals(Material.LINGERING_POTION) || is.getType().equals(Material.SPLASH_POTION)) {
                PotionMeta pm = (PotionMeta) is.getItemMeta();
                try {
                    String potionType = Main.getInstance().chestConfig.getString(difficulty + "." + item + ".potionType");
                    boolean potionUpgraded = Main.getInstance().chestConfig.getBoolean(difficulty + "." + item + ".potionUpgraded");
                    boolean potionExtended = Main.getInstance().chestConfig.getBoolean(difficulty + "." + item + ".potionExtended");
                    pm.setBasePotionData(new PotionData(PotionType.valueOf(potionType.toUpperCase()), potionExtended, potionUpgraded));
                    is.setItemMeta(pm);
                } catch (Exception e) {
                    Main.getInstance().console.sendMessage(Util.getMess("potionException").replace("%diff", difficulty));
                    continue;
                }
            }
            randInt = rand.nextInt(27);
            if (chest.getInventory().getItem(randInt) == null || chest.getInventory().getItem(randInt).getType() == null) {
                chest.getInventory().setItem(randInt, is);
                continue;
            }
            while (chest.getInventory().getItem(randInt) != null || !chest.getInventory().getItem(randInt).getType().equals(Material.AIR)) {
                randInt = rand.nextInt(27);
            }
            chest.getInventory().setItem(randInt, is);
        }
    }


    public static Location locFromString(String s) {
        String[] divided = s.split(";");
        String[] divided2 = divided[0].split(",");
        double x = Double.parseDouble(divided2[1]);
        double y = Double.parseDouble(divided2[2]);
        double z = Double.parseDouble(divided2[3]);
        return new Location(Bukkit.getServer().getWorld("void"), x, y, z, -90 ,0);
    }
}
