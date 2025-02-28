package com.gyzer.Manager;

import com.gyzer.LegendaryRunePlus;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRuneItemManager {
    private ConcurrentHashMap<UUID, ItemStack> caches;

    public PlayerRuneItemManager() {
        loadItems();
    }

    public Optional<ItemStack> getItem(UUID uuid) {
        return caches.containsKey(uuid) ? Optional.of(caches.get(uuid)) : Optional.empty();
    }
    public void delItem(UUID uuid) {
        LegendaryRunePlus.getLegendaryRunePlus().getDataProvider().delItem(uuid);
    }
    public void saveItem(UUID uuid,ItemStack i) {
        caches.put(uuid,i);
        LegendaryRunePlus.getLegendaryRunePlus().getDataProvider().setItem(uuid,i);
    }

    private void loadItems() {
        this.caches = LegendaryRunePlus.getLegendaryRunePlus().getDataProvider().getItems().orElse(new ConcurrentHashMap<>());
    }

}
