package com.gyzer.Data.Rune;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class RunePageData {
    private String pageId;
    private HashMap<String, ItemStack> put;
    private HashMap<String, List<String>> attrs;
    private HashMap<String, String> types;

    public RunePageData(String pageId, HashMap<String, ItemStack> put, HashMap<String, List<String>> attrs,HashMap<String, String> types) {
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

    public HashMap<String, ItemStack> getPut() {
        return put;
    }

    public HashMap<String, List<String>> getAttrs() {
        return attrs;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setPut(HashMap<String, ItemStack> put) {
        this.put = put;
    }

    public void setAttrs(HashMap<String, List<String>> attrs) {
        this.attrs = attrs;
    }

    public void remove(String slotId) {
        put.remove(slotId);
        attrs.remove(slotId);
        types.remove(slotId);
    }

    public void add(String slotId, List<String> attrs, ItemStack i, String type) {
        this.attrs.put(slotId,attrs);
        put.put(slotId,i);
        types.put(slotId,type);
    }


}
