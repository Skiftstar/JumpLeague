package Yukami.PixelLeague.Game;

import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {

    Location lastCP;
    List<Location> checkpoints = new ArrayList<>();
    int lives;
    int killStreak = 0;
    org.bukkit.entity.Player p;
    String name;
    UUID uuid;
    org.bukkit.entity.Player taggedBy = null;
    int tagTaskID = -1;

    public Player(org.bukkit.entity.Player p, int lives) {
        this.p = p;
        this.name = p.getDisplayName();
        this.uuid = p.getUniqueId();
        this.lives = lives;
    }

    public org.bukkit.entity.Player getTaggedBy() {
        return taggedBy;
    }

    public void setTaggedBy(org.bukkit.entity.Player taggedBy) {
        if (taggedBy != null) {
            Bukkit.getScheduler().cancelTask(tagTaskID);
        }
        this.taggedBy = taggedBy;
        startCooldown();
    }

    private void startCooldown() {
        tagTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                taggedBy = null;
            }
        }, Main.getInstance().tagDuration);
    }

    public void setLastCP(Location lastCP) {
        this.lastCP = lastCP;
    }

    public void increaseKillStreak() {
        killStreak += 1;
        if (killStreak == 3) {
            Bukkit.broadcastMessage(Util.getMess("playerOnThreeKS").replace("%plr", getName()));
            if (Main.getInstance().getConfig().getInt("rewardKillstreakThree") > 0) {
                int reward = Main.getInstance().getConfig().getInt("rewardKillstreakThree");
                giveMoney("killStreakThreeReward", reward);
            }
        }
        if (killStreak == 5) {
            Bukkit.broadcastMessage(Util.getMess("playerOnFiveKS").replace("%plr", getName()));
            if (Main.getInstance().getConfig().getInt("rewardKillstreakFive") > 0) {
                int reward = Main.getInstance().getConfig().getInt("rewardKillstreakFive");
                giveMoney("killStreakFiveReward", reward);
            }
        }
        if (killStreak == 10) {
            Bukkit.broadcastMessage(Util.getMess("playerOnTenKS").replace("%plr", getName()));
            if (Main.getInstance().getConfig().getInt("rewardKillstreakTen") > 0) {
                int reward = Main.getInstance().getConfig().getInt("rewardKillstreakTen");
                giveMoney("killStreakTenReward", reward);
            }
        }
    }

    public void giveMoney(String rewardMess, int money) {
        Economy econ = Main.getEconomy();
        EconomyResponse r = econ.depositPlayer(p, money);
        if(r.transactionSuccess()) {
            p.sendMessage(Util.getMess(rewardMess).replace("%mon", Integer.toString(money)).replace("%tot", Double.toString(r.balance)));
        } else {
            Main.getInstance().console.sendMessage(Util.getMess("vaultTransactionError").replace("%err", r.errorMessage));
            p.sendMessage(Util.getMess("vaultTransactionError").replace("%err", r.errorMessage));
        }
    }

    public void giveMoney(int money) {
        Economy econ = Main.getEconomy();
        EconomyResponse r = econ.depositPlayer(p, money);
        if(!r.transactionSuccess()) {
            Main.getInstance().console.sendMessage(Util.getMess("vaultTransactionError").replace("%err", r.errorMessage));
            p.sendMessage(Util.getMess("vaultTransactionError").replace("%err", r.errorMessage));
        }
    }

    public void checkIfCPReward() {
        FileConfiguration config = Main.getInstance().getConfig();
        int amountEasy = config.getInt("amountEasy") + 1;
        int amountMedium = config.getInt("amountMedium");
        int amountHard = config.getInt("amountHard");
        int amountHardcore = config.getInt("amountHardcore");
        if (checkpoints.size() == amountEasy && config.getInt("rewardFinishEasy") > 0) {
            int reward = config.getInt("rewardFinishEasy");
            giveMoney("finishEasy", reward);
        } else if (checkpoints.size() == amountEasy + amountMedium && config.getInt("rewardFinishMedium") > 0)  {
            int reward = config.getInt("rewardFinishMedium");
            giveMoney("finishMedium", reward);
        } else if (checkpoints.size() == amountEasy + amountMedium + amountHard && config.getInt("rewardFinishHard") > 0) {
            int reward = config.getInt("rewardFinishHard");
            giveMoney("finishHard", reward);
        } else if (checkpoints.size() == amountEasy + amountMedium + amountHard + amountHardcore && config.getInt("rewardFinishHardcore") > 0) {
            int reward = config.getInt("rewardFinishHardcore");
            giveMoney("finishHardcore", reward);
        }
    }

    public void giveJumpReward() {
        FileConfiguration config = Main.getInstance().getConfig();
        if (config.get("rewardFinishFirst.item") != null) {
            String itemName = config.getString("rewardFinishFirst.item");
            Material mat = Material.getMaterial(itemName);
            if (mat == null) {
                Main.getInstance().console.sendMessage(Util.getMess("itemNotFoundJumpReward").replace("%itm", itemName));
            } else {
                ItemStack is = new ItemStack(mat);
                p.getInventory().addItem(is);
            }
        }
        if (config.get("rewardFinishFirst.money") != null) {
            int money = config.getInt("rewardFinishFirst.money");
            giveMoney(money);
        }
        p.sendMessage(Util.getMess("finishFirst"));
    }

    public boolean hasCheckpoint(Location loc) {
        return checkpoints.contains(loc);
    }

    public void addCP(Location loc) {
        checkpoints.add(loc);
    }

    public void toLastCP() {
        p.teleport(lastCP);
    }

    public int getCheckpoints() {
        return checkpoints.size();
    }

    public String getName() {
        return name;
    }

    public org.bukkit.entity.Player getPlayer() {
        return p;
    }

    public void resetKillstreak() {
        killStreak = 0;
    }

    public int getLives() {
        return lives;
    }

    public void death(Location respawnLoc) {
        lives -= 1;
        p.teleport(respawnLoc);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setFireTicks(0);
    }
}
