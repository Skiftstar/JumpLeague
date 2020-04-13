package Yukami.PixelLeague.Saver_Loader;

import Yukami.PixelLeague.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Saver {

    private static String name;
    private static Main main;
    private static List<String> blocks;
    private static List<Block> chests = new ArrayList<>();
    private static List<Block> start = new ArrayList<>();
    private static List<Block> end = new ArrayList<>();

    public static void setMain(Main main){
        Saver.main = main;
    }

    public static boolean saveArena(Location[] cords, String difficulty) {
        name = difficulty;

        //same algorithm used for searching the arena in Class "TnTCommand"

        double xDiff = cords[1].getX() - cords[0].getX();
        int xABS;
        try {
            xABS = ((int) xDiff / Math.abs((int) xDiff));
        } catch (ArithmeticException e) {
            xABS = 1;
        }
        double yDiff = cords[1].getY() - cords[0].getY();
        int yABS;
        try {
            yABS = ((int) yDiff / Math.abs((int) yDiff));
        } catch (ArithmeticException e) {
            yABS = 1;
        }
        double zDiff = cords[1].getZ() - cords[0].getZ();
        int zABS;
        try {
            zABS = ((int) zDiff / Math.abs((int) zDiff));
        } catch (ArithmeticException e) {
            zABS = 1;
        }
        Location loc = new Location(cords[0].getWorld(), cords[0].getX(), cords[0].getY(), cords[0].getZ(), 0,0);
        Block b;
        blocks = new ArrayList<>();


        int blockCount = 0;
        for (int l = 0; l != yDiff + yABS; l = l + yABS) {
            for (int j = 0; j != zDiff + zABS; j = j + zABS) {
                for (int i = 0; i != xDiff + xABS; i = i + xABS) {

                    if (loc.getBlock().getType().equals(Material.IRON_PLATE)) {
                        if (end.size() > 0) {
                            if (!end.contains(loc.getBlock())) {
                                System.out.println("too many end");
                                return false;
                            }
                        }
                        end.add(loc.getBlock());
                    }
                    if (loc.getBlock().getType().equals(Material.GOLD_PLATE)) {
                        if (start.size() > 0) {
                            if (!start.contains(loc.getBlock())) {
                                System.out.println("too many start");
                                return false;
                            }
                        }
                        start.add(loc.getBlock());
                    }
                    if (loc.getBlock().getType().equals(Material.CHEST)) {
                        if (chests.size() > 0) {
                            if (!chests.contains(loc.getBlock())) {
                                System.out.println("too many chest");
                                return false;
                            }
                        }
                        chests.add(loc.getBlock());
                    }
                    if (!loc.getBlock().getType().equals(Material.AIR)) {
                        save(loc.getBlock()); //save the block in the List
                    }
                    loc.setX(loc.getX() + xABS);
                }
                loc.setZ(loc.getZ() + zABS);
                loc.setX(cords[0].getX());
            }
            loc.setY(loc.getY() + yABS);
            loc.setZ(cords[0].getZ());
        }
        if (chests.size() == 0) {
            System.out.println("no chest");
            return false;
        }
        if (start.size() == 0) {
            System.out.println("no start");
            return false;
        }
        if (end.size() == 0) {
            System.out.println("no end");
            return false;
        }
        int indexes;
        if (main.partsConfig.get(difficulty) == null) {
            indexes = 0;
        } else
        {
            indexes = main.partsConfig.getConfigurationSection(difficulty).getKeys(false).size();
        }
        main.partsConfig.set(name + "." + indexes + ".blocks", blocks); //after the algorithm is finished, put everything in the config
        main.partsConfig.set(name + "." + indexes + ".chest", convert(chests.get(0)));
        main.partsConfig.set(name + "." + indexes + ".start", convert(start.get(0)));
        main.partsConfig.set(name + "." + indexes + ".end", convert(end.get(0)));
        main.savePartsConfig(); //and save it
        end.clear();
        start.clear();
        chests.clear();
        return true;
    }

    @SuppressWarnings("deprecation")
    public static String convert(Block b) {
        Material mat = b.getType();
        Location loc = b.getLocation();
        byte data = b.getData();
        return(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ";" + mat + "," + data); //seperating important things
    }

    @SuppressWarnings("deprecation")
    private static void save(Block b) {
        Material mat = b.getType();
        Location loc = b.getLocation();
        byte data = b.getData();
        blocks.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ";" + mat + "," + data); //seperating important things
    }



}
