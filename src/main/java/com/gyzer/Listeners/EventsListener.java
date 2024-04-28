package com.gyzer.Listeners;

import com.gyzer.API.Events.RuneAttributeUpdateEvent;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import com.gyzer.Panel.Panel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class EventsListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(e.getPlayer().getUniqueId()).updateAttributes();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Panel) {
            Panel panel = (Panel) e.getInventory().getHolder();
            panel.onDrag(e);
        }
    }

    @EventHandler
    public void onInv(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof Panel) {
            Panel panel = (Panel) e.getInventory().getHolder();
            panel.onClick(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().update(p);
    }
}
