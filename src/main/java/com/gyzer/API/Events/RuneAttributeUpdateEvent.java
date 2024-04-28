package com.gyzer.API.Events;

import com.gyzer.Data.AttributeWriterData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class RuneAttributeUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private Player p;
    private List<AttributeWriterData> writerDataList;

    public RuneAttributeUpdateEvent(Player p, List<AttributeWriterData> writerDataList) {
        this.p = p;
        this.writerDataList = writerDataList;
    }

    public Player getPlayer() {
        return p;
    }

    public List<AttributeWriterData> getWriterDataList() {
        return writerDataList;
    }

    public void setWriterDataList(List<AttributeWriterData> writerDataList) {
        this.writerDataList = writerDataList;
    }

}
