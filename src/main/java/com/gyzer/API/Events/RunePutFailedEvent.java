package com.gyzer.API.Events;

import com.gyzer.Data.Rune.Rune;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class RunePutFailedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private Player p;
    private String pageId;
    private String slotId;
    private Rune rune;
    private ItemStack i;

    public RunePutFailedEvent(Player p, String pageId, String slotId, Rune rune, ItemStack i) {
        this.p = p;
        this.pageId = pageId;
        this.slotId = slotId;
        this.rune = rune;
        this.i = i;
    }

    public Player getPlayer() {
        return p;
    }

    public String getPageId() {
        return pageId;
    }

    public String getSlotId() {
        return slotId;
    }

    public Rune getRune() {
        return rune;
    }

    public ItemStack getI() {
        return i;
    }
}
