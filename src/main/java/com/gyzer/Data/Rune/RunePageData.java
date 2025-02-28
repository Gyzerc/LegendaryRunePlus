package com.gyzer.Data.Rune;

import com.gyzer.LegendaryRunePlus;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RunePageData {
    private String pageId;
    private HashMap<String, UUID> put;
    private HashMap<String, List<String>> attrs;
    private HashMap<String, String> types;

    public RunePageData(String pageId, HashMap<String, UUID> put, HashMap<String, List<String>> attrs,HashMap<String, String> types) {
        this.pageId = pageId;
        this.put = put;
        this.attrs = attrs;
        this.types = types;
    }

    public HashMap<String, String> getTypes() {
        return types;
    }

    public void setTypes(HashMap<String, String> types) {
        this.types = types;
    }

    public String getPageId() {
        return pageId;
    }

    public HashMap<String, UUID> getPut() {
        return put;
    }

    public HashMap<String, List<String>> getAttrs() {
        return attrs;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setPut(HashMap<String, UUID> put) {
        this.put = put;
    }

    public void setAttrs(HashMap<String, List<String>> attrs) {
        this.attrs = attrs;
    }

    public void remove(String slotId) {
        if (put.containsKey(slotId)) {
            UUID uuid = put.remove(slotId);
            LegendaryRunePlus.getLegendaryRunePlus().getPlayerRuneItemManager().delItem(uuid);
        }
        attrs.remove(slotId);
        types.remove(slotId);
    }

    public void add(String slotId, List<String> attrs, ItemStack i, String type) {
        this.attrs.put(slotId,attrs);
        types.put(slotId,type);

        UUID uuid = UUID.randomUUID();
        put.put(slotId,uuid);
        LegendaryRunePlus.getLegendaryRunePlus().getPlayerRuneItemManager().saveItem(uuid,i);

    }


}
