package Yukami.PixelLeague.Saver_Loader.posListener;

import Yukami.PixelLeague.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class pos implements Listener {

    private Map<UUID, Location[]> Positions = new HashMap<>();

    public pos(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(a.equals(Action.LEFT_CLICK_BLOCK) || a.equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (e.getHand() == null || !e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        if (!p.hasPermission("skifty.we.pos")) {
            return;
        }
        ItemStack is = p.getInventory().getItemInMainHand();
        if (!is.getType().equals(Material.WOOD_SPADE)) {
            return;
        }

        e.setCancelled(true);

        UUID uuid = p.getUniqueId();
        Location[] cords = new Location[2];

        if (Positions.containsKey(uuid)) {
            cords = Positions.get(uuid);
            Positions.remove(uuid);
            Block b = e.getClickedBlock();
            int i;
            if (a.equals(Action.LEFT_CLICK_BLOCK)) {
                cords[0] = b.getLocation();
                i = 1;
            } else {
                cords[1] = b.getLocation();
                i = 2;
            }
            p.sendMessage(Color("&bPosition " + i + " successfully set at " + b.getLocation().getX() + " | " + b.getLocation().getY() + " | " + b.getLocation().getZ()));
        } else {
            cords[0] = null;
            cords[1] = null;
            Block b = e.getClickedBlock();
            int i;
            if (a.equals(Action.LEFT_CLICK_BLOCK)) {
                cords[0] = b.getLocation();
                i = 1;
            } else {
                cords[1] = b.getLocation();
                i = 2;
            }
            p.sendMessage(Color("&bPosition " + i + " successfully set at " + b.getLocation().getX() + " | " + b.getLocation().getY() + " | " + b.getLocation().getZ()));
        }
        Positions.put(uuid, cords);
    }

    public Location[] getPos(Player p) {
        return Positions.get(p.getUniqueId());
    }

    public boolean hasBothPos(Player p) {
        if (!Positions.containsKey(p.getUniqueId())) {
            return false;
        }
        Location[] cords = Positions.get(p.getUniqueId());
        return cords[0] != null && cords[1] != null;
    }

    private String Color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
