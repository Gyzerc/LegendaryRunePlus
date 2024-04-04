package com.gyzer.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class RunePutEvent extends Event {
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
    private ItemStack i;
    private ItemStack change;
    private boolean cancel = false;

    public RunePutEvent(Player p, String pageId, String slotId,ItemStack i) {
        this.p = p;
        this.pageId = pageId;
        this.slotId = slotId;
        this.i = i;
    }

    public ItemStack getItem() {
        return i;
    }

    public void setItem(ItemStack i) {
        this.i = i;
        this.change = i;
    }

    public ItemStack getChange() {
        return change;
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

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
