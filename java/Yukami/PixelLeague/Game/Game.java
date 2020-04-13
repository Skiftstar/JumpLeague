package Yukami.PixelLeague.Game;

import Yukami.PixelLeague.Main;
import Yukami.PixelLeague.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Game implements Listener {

    private Main plugin;
    private List<Location> startingPos;
    private List<Block> blocks;
    private List<Yukami.PixelLeague.Game.Player> players = new ArrayList<>();
    private List<Location> pvpStartingPos = new ArrayList<>();
    private Scoreboard sb;
    private Objective objective;
    private boolean jumpActive = true, countdownActive = false, pvpActive = false;
    private int countdown;
    private ItemStack resetItem;


    public Game(Main plugin, int playerCount, List<Location> startingPos, List<Block> blocks) {
        this.plugin = plugin;
        this.startingPos = startingPos;
        resetItem = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta im = resetItem.getItemMeta();
        im.setDisplayName(Util.getMess("ResetItemName"));
        resetItem.setItemMeta(im);
        loadPvPStarts();
        this.blocks = blocks;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setScoreboard();
        int maxLives = plugin.getConfig().getInt("pvpLives");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(sb);
            players.add(new Yukami.PixelLeague.Game.Player(player, maxLives));
            player.getInventory().clear();
            player.getInventory().setItem(0, resetItem);
        }
        plugin.gameActive = true;
        startGame();
    }

    private void loadPvPStarts() {
        List<String> temp = plugin.getConfig().getStringList("pvpSpawns");
        for (String loc : temp) {
            pvpStartingPos.add(locFromString(loc));
        }
    }

    private static Location locFromString(String s) {
        String[] divided = s.split(";");
        String[] divided2 = divided[0].split(",");
        String world = divided2[0];
        double x = Double.parseDouble(divided2[1]);
        double y = Double.parseDouble(divided2[2]);
        double z = Double.parseDouble(divided2[3]);
        return new Location(Bukkit.getServer().getWorld(world), x, y, z, -90 ,0);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(Util.getMess("gameAlreadyRunning"));
        if (players.size() > 0) {
            p.teleport(players.get(0).getPlayer().getLocation());
        }
    }

    private void startGame() {
        for (int i = 0; i < startingPos.size(); i++) {
            players.get(i).getPlayer().teleport(startingPos.get(i));
            players.get(i).getPlayer().setHealth(20);
            players.get(i).getPlayer().setFoodLevel(20);
            players.get(i).getPlayer().setGameMode(GameMode.ADVENTURE);
        }
    }

    private void cleanup(int runs) {
        if (blocks.size() >= 150) {
            List<Block> Bs = new ArrayList<>();
            for (int i = 0; i < 150; i++) {
                blocks.get(i).setType(Material.AIR);
                Bs.add(blocks.get(i));
            }
            for (Block b: Bs) {
                blocks.remove(b);
            }
            if (runs - 1 > 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        cleanup(runs - 1);
                    }
                }, 10);
            }

        } else {
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }
            blocks.clear();
        }
    }

    private void startCountdown() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(Util.getMess("jumpEnding").replace("%sec", Integer.toString(countdown))).create());
                }
                countdown--;
                if (countdown != 0 && jumpActive) {
                    startCountdown();
                } else {
                    startPvPRound();
                    setPvpScoreboard();
                }
            }
        }, 20);
    }

    private void setScoreboard() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = sb.getObjective(Util.getMess("scoreboardNameJump")) == null ? sb.registerNewObjective(Util.getMess("scoreboardNameJump"), "dummy") : sb.getObjective(Util.getMess("scoreboardNameJump"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Player p : Bukkit.getOnlinePlayers()) {
            objective.getScore(p.getDisplayName()).setScore(0);
        }
    }

    private void updateScoreboard() {
        for (Yukami.PixelLeague.Game.Player p : players) {
            objective.getScore(p.getName()).setScore(p.getCheckpoints());
        }

    }

    private void updatePvPScoreboard() {
        for (Yukami.PixelLeague.Game.Player p : players) {
            objective.getScore(p.getName()).setScore(p.getLives());
        }
    }

    private void setPvpScoreboard() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = sb.getObjective(Util.getMess("scoreboardNamePvP")) == null ? sb.registerNewObjective(Util.getMess("scoreboardNamePvP"), "dummy") : sb.getObjective(Util.getMess("scoreboardNamePvP"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Player p : Bukkit.getOnlinePlayers()) {
            objective.getScore(p.getDisplayName()).setScore(3);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(sb);
        }
    }

    private void removeScoreboard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    private void startPvPCountdown() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(Util.getMess("pvpTimer").replace("%sec", Integer.toString(countdown))).create());
                }
                countdown--;
                if (countdown != 0 && !pvpActive) {
                    startPvPCountdown();
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(Util.getMess("pvpActive"), "", 5, plugin.titleLength, 5);
                    }
                    pvpActive = true;
                }
            }
        }, 20);
    }

    @EventHandler
    private void onHungerloss(FoodLevelChangeEvent e) {
        if (!pvpActive) {
            e.setCancelled(true);
        }
    }

    private Yukami.PixelLeague.Game.Player getPlayerFromBukPlayer(Player p) {
        for (Yukami.PixelLeague.Game.Player pl : players) {
            if (pl.getPlayer().equals(p)) {
                return pl;
            }
        }
        return null;
    }

    @EventHandler
    private void onDamageNonEnt(EntityDamageEvent e) {
        if (!pvpActive) {
            e.setCancelled(true);
        }
        if (!(e.getEntity() instanceof  Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        if (!(e.getDamage() > p.getHealth())) {
            return;
        }
        e.setCancelled(true);
        Yukami.PixelLeague.Game.Player player = getPlayerFromBukPlayer(p);
        if (player == null) {
            return;
        }
        Player damager;
        if (player.getTaggedBy() == null) {
            int remLives = player.getLives();

            if (remLives > 1) {
                Bukkit.broadcastMessage(Util.getMess("playerSD").replace("%dead", player.getName()));
                player.getPlayer().sendMessage(Util.getMess("youDied"));
                player.getPlayer().sendTitle(Util.getMess("livesRemaining").replace("%liv", Integer.toString(remLives - 1)), "", 5, plugin.titleLength , 5);
                Random rand = new Random();
                player.death(pvpStartingPos.get(rand.nextInt(pvpStartingPos.size())));
                player.resetKillstreak();
                updatePvPScoreboard();
                if (players.size() == 1) {
                    finish(players.get(0).getPlayer());
                }
                return;
            }
            Bukkit.broadcastMessage(Util.getMess("playerSDKickedOut").replace("%dead", player.getName()));
            players.remove(player);
            player.getPlayer().sendTitle(Util.getMess("youGotKickedOutTitleBig"), Util.getMess("youGotKickedOutTitleSmall"), 5, plugin.titleLength, 5);
            for (ItemStack is : p.getInventory().getContents()) {
                if (is == null) {
                    continue;
                }
                p.getWorld().dropItem(p.getLocation(), is);
            }
            p.getPlayer().setGameMode(GameMode.SPECTATOR);
            if (players.size() == 1) {
                finish(players.get(0).getPlayer());
            }
            updatePvPScoreboard();
            return;
        } else {
            damager = player.getTaggedBy();

        }
        Yukami.PixelLeague.Game.Player killer = getPlayerFromBukPlayer(damager);
        if (killer == null) {
            return;
        }

        int remLives = player.getLives();
        killer.increaseKillStreak();
        if (remLives > 1) {
            Bukkit.broadcastMessage(Util.getMess("killedBy").replace("%dead", player.getName()).replace("%killer", killer.getName()));
            damager.sendMessage(Util.getMess("youKilled").replace("%plr", player.getName()));
            player.getPlayer().sendMessage(Util.getMess("youDied"));
            player.getPlayer().sendTitle(Util.getMess("livesRemaining").replace("%liv", Integer.toString(remLives - 1)), "", 5, plugin.titleLength , 5);
            Random rand = new Random();
            player.death(pvpStartingPos.get(rand.nextInt(pvpStartingPos.size())));
            player.resetKillstreak();
            updatePvPScoreboard();
            if (players.size() == 1) {
                finish(players.get(0).getPlayer());
            }
            return;
        }
        players.remove(player);
        Bukkit.broadcastMessage(Util.getMess("kickedOutBy").replace("%dead", player.getName()).replace("%killer", killer.getName()));
        damager.sendMessage(Util.getMess("youKickedOut").replace("%plr", player.getName()));
        player.getPlayer().sendTitle(Util.getMess("youGotKickedOutTitleBig"), Util.getMess("youGotKickedOutTitleSmall"), 5, plugin.titleLength, 5);
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) {
                continue;
            }
            p.getWorld().dropItem(p.getLocation(), is);
        }
        updatePvPScoreboard();
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
        if (players.size() == 1) {
            finish(players.get(0).getPlayer());
        }

    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e) {
        if (!pvpActive) {
            e.setCancelled(true);
        }
        if (!(e.getEntity() instanceof  Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        Player damager;

        if (!(e.getDamager() instanceof Player)) {
            if (!(e.getDamage() > p.getHealth())) {
                return;
            }
            e.setCancelled(true);
            Yukami.PixelLeague.Game.Player player = getPlayerFromBukPlayer(p);
            if (player == null) {
                return;
            }
            if (player.getTaggedBy() == null) {
                int remLives = player.getLives();

                if (remLives > 1) {
                    Bukkit.broadcastMessage(Util.getMess("playerSD").replace("%dead", player.getName()));
                    player.getPlayer().sendMessage(Util.getMess("youDied"));
                    player.getPlayer().sendTitle(Util.getMess("livesRemaining").replace("%liv", Integer.toString(remLives - 1)), "", 5, plugin.titleLength , 5);
                    Random rand = new Random();
                    player.death(pvpStartingPos.get(rand.nextInt(pvpStartingPos.size())));
                    player.resetKillstreak();
                    updatePvPScoreboard();
                    return;
                }
                Bukkit.broadcastMessage(Util.getMess("playerSDKickedOut").replace("%dead", player.getName()));
                players.remove(player);
                player.getPlayer().sendTitle(Util.getMess("youGotKickedOutTitleBig"), Util.getMess("youGotKickedOutTitleSmall"), 5, plugin.titleLength, 5);
                for (ItemStack is : p.getInventory().getContents()) {
                    if (is == null) {
                        continue;
                    }
                    p.getWorld().dropItem(p.getLocation(), is);
                }
                updatePvPScoreboard();
                p.getPlayer().setGameMode(GameMode.SPECTATOR);
                if (players.size() == 1) {
                    finish(players.get(0).getPlayer());
                }
                return;
            } else {
                damager = player.getTaggedBy();
            }
        } else {
            damager = (Player) e.getDamager();
        }


        if (!(e.getDamage() > p.getHealth())) {
            return;
        }
        e.setCancelled(true);
        Yukami.PixelLeague.Game.Player player = getPlayerFromBukPlayer(p);
        if (player == null) {
            return;
        }
        Yukami.PixelLeague.Game.Player killer = getPlayerFromBukPlayer(damager);
        if (killer == null) {
            return;
        }

        int remLives = player.getLives();
        killer.increaseKillStreak();
        if (remLives > 1) {
            Bukkit.broadcastMessage(Util.getMess("killedBy").replace("%dead", player.getName()).replace("%killer", killer.getName()));
            damager.sendMessage(Util.getMess("youKilled").replace("%plr", player.getName()));
            player.getPlayer().sendMessage(Util.getMess("youDied"));
            player.getPlayer().sendTitle(Util.getMess("livesRemaining").replace("%liv", Integer.toString(remLives - 1)), "", 5, plugin.titleLength , 5);
            Random rand = new Random();
            player.death(pvpStartingPos.get(rand.nextInt(pvpStartingPos.size())));
            player.resetKillstreak();
            updatePvPScoreboard();
            return;
        }
        players.remove(player);
        Bukkit.broadcastMessage(Util.getMess("kickedOutBy").replace("%dead", player.getName()).replace("%killer", killer.getName()));
        damager.sendMessage(Util.getMess("youKickedOut").replace("%plr", player.getName()));
        player.getPlayer().sendTitle(Util.getMess("youGotKickedOutTitleBig"), Util.getMess("youGotKickedOutTitleSmall"), 5, plugin.titleLength, 5);
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) {
                continue;
            }
            p.getWorld().dropItem(p.getLocation(), is);
        }
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
        updatePvPScoreboard();
        if (players.size() == 1) {
            finish(players.get(0).getPlayer());
        }
    }

    private void finish(Player winner) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(Util.getMess("winnerTitleBig"), Util.getMess("winnterTitleSmall").replace("%plr", winner.getDisplayName()), 5, plugin.titleLength, 5);
        }
        if (plugin.getConfig().getInt("rewardPvPWin") > 0) {
            Yukami.PixelLeague.Game.Player p = players.get(0);
            int reward = plugin.getConfig().getInt("rewardPvPWin");
            p.giveMoney("pvpWinReward", reward);
        }
        HandlerList.unregisterAll(this);
        plugin.gameActive = false;
        removeScoreboard();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer(Util.getMess("kickFromServerMess"));
                }
                cleanup((blocks.size() / 150) + 1);
            }
        }, 100);
    }


    private void startPvPRound() {
        jumpActive = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Random rand = new Random();
            p.teleport(pvpStartingPos.get(rand.nextInt(pvpStartingPos.size())));
        }
        for (Yukami.PixelLeague.Game.Player p : players) {
            p.getPlayer().setGameMode(GameMode.SURVIVAL);
            p.getPlayer().getInventory().remove(resetItem);
        }
        countdown = 20;
        startPvPCountdown();
    }

    @EventHandler
    private void onBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent e) {
        if (!pvpActive && jumpActive) {
            e.setCancelled(true);
        }
        blocks.add(e.getBlock());
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Yukami.PixelLeague.Game.Player plr = getPlayerFromBukPlayer(p);
        if (plr == null) {
            return;
        }
        if (e.getItemDrop().getItemStack().equals(resetItem)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void pressurePlateInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().equals(resetItem)) {
                Yukami.PixelLeague.Game.Player plr = getPlayerFromBukPlayer(e.getPlayer());
                if (plr == null) {
                    return;
                }
                plr.toLastCP();
            }
        }
        if (!e.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Yukami.PixelLeague.Game.Player p = getPlayerFromBukPlayer(e.getPlayer());
        if (p == null) {
            return;
        }
        if (e.getClickedBlock().getType().equals(Material.IRON_PLATE)) {
            if (!countdownActive) {
                countdown = 30;
                startCountdown();
                countdownActive = true;
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    pl.sendTitle(Util.getMess("endReachedTitleBig"), Util.getMess("endReachedTitleSmall").replace("%plr", p.getName()), 5, plugin.titleLength, 5);
                }
                p.giveJumpReward();
            }
            Location loc = e.getClickedBlock().getLocation();
            loc.setY(loc.getY() + 1);
            loc.setYaw(-90);
            if (p.hasCheckpoint(loc)) {
                return;
            }
            p.setLastCP(loc);
            p.addCP(loc);
            e.getPlayer().sendMessage(Util.getMess("youReachedTheEnd"));
        }
        if (!e.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
            return;
        }
        Location loc = e.getClickedBlock().getLocation();
        loc.setY(loc.getY() + 1);
        loc.setYaw(-90);
        if (p.hasCheckpoint(loc)) {
            return;
        }
        p.setLastCP(loc);
        p.addCP(loc);
        updateScoreboard();
        e.getPlayer().sendMessage(Util.getMess("checkpointReached"));
        p.checkIfCPReward();
    }

    @EventHandler
    private void onFallDamage(EntityDamageEvent e) {
        if (!jumpActive) {
            return;
        }
        if (!(e.getEntity() instanceof  Player)) {
            return;
        }
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onVoid(PlayerMoveEvent e) {
        Yukami.PixelLeague.Game.Player p = getPlayerFromBukPlayer(e.getPlayer());
        if (p == null) {
            return;
        }
        if (!(e.getTo().getY() <= 0)) {
            return;
        }
        p.toLastCP();
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        Yukami.PixelLeague.Game.Player p = getPlayerFromBukPlayer(e.getPlayer());
        if (p == null) {
            return;
        }
        players.remove(p);
        objective.getScoreboard().resetScores(p.getName());
        Bukkit.broadcastMessage(Util.getMess("playerLeft").replace("%plr", p.getName()));
        updateScoreboard();
        if (players.size() == 1) {
            finish(players.get(0).getPlayer());
        }
    }

}
