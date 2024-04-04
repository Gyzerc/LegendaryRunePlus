package com.gyzer.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RuneUnEquipEvent extends Event {
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
    private boolean cancel = false;

    public RuneUnEquipEvent(Player p, String pageId, String slotId) {
        this.p = p;
        this.pageId = pageId;
        this.slotId = slotId;
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
