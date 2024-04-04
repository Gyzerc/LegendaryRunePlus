package com.gyzer.Panel.Loader;

import com.gyzer.Panel.Loader.Button.ButtonFuction;
import com.gyzer.Panel.Loader.Button.RuneSettings;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class PanelReader {

    private String id;
    private String title;
    private String title_other;
    private int size;
    private HashMap<Integer, MenuItem> items;
    private List<String> runeSlotIds;
    public PanelReader(String id, String title, String title_other, int size, HashMap<Integer, MenuItem> items,List<String> runeSlotIds) {
        this.id = id;
        this.title = title;
        this.title_other = title_other;
        this.size = size;
        this.items = items;
        this.runeSlotIds = runeSlotIds;
    }

    public String getTitle_other() {
        return title_other;
    }

    public List<String> getRuneSlotIds() {
        return runeSlotIds;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public HashMap<Integer, MenuItem> getItems() {
        return items;
    }

    public String getRuneSlotDisplayById(String slotId) {
        String id = "";
        MenuItem mi = items.values().stream().filter(m -> {
            if (m.getFuction().getType().equals(ButtonFuction.FuctionType.RUNE)) {
                RuneSettings settings = m.getFuction().getRuneSettings();
                return settings.getSlotId().equals(slotId);
            }
            return false;
        }).findFirst().orElse(null);
        if (mi != null) {
            ItemStack i = mi.getItem();
            if (i != null) {
                id = i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : "";
            }
        }
        return id;
    }
}
