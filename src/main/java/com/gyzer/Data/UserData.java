package com.gyzer.Data;

import com.google.gson.Gson;
import com.gyzer.API.Events.RuneAttributeUpdateEvent;
import com.gyzer.API.Events.RunePutEvent;
import com.gyzer.API.Events.RuneUnEquipEvent;
import com.gyzer.API.Events.SlotUnlockEvent;
import com.gyzer.Data.Rune.RunePageData;
import com.gyzer.LegendaryRunePlus;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserData {
    private UUID uuid;
    private HashMap<String,List<String>> unlocks;
    private HashMap<String, RunePageData> datas;

    public UserData(UUID uuid, HashMap<String, List<String>> unlocks, HashMap<String, RunePageData> datas) {
        this.uuid = uuid;
        this.unlocks = unlocks;
        this.datas = datas;
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<String, List<String>> getUnlocks() {
        return unlocks;
    }

    public HashMap<String, RunePageData> getDatas() {
        return datas;
    }

    public void setUnlocks(HashMap<String, List<String>> unlocks) {
        this.unlocks = unlocks;
    }

    public void setDatas(HashMap<String, RunePageData> datas) {
        this.datas = datas;
    }

    public boolean hasUnlockSlot(String pageName,String slotId) {
        List<String> unlock = unlocks.getOrDefault(pageName,new ArrayList<>());
        return unlock.contains(slotId);
    }

    public void Lock(String pageId, String slotId) {
        if (!hasUnlockSlot(pageId,slotId)) {
            return;
        }
        List<String> unlock = unlocks.getOrDefault(pageId,new ArrayList<>());
        unlock.remove(slotId);

        RunePageData data = datas.getOrDefault(pageId,new RunePageData(pageId,new HashMap<>(),new HashMap<>(),new HashMap<>()));
        data.remove(slotId);

        update(false);
        updateAttributes();
    }
    public void unlockSlot(Player p ,String pageName,String slotId) {
        if (hasUnlockSlot(pageName,slotId)) {
            return;
        }
        SlotUnlockEvent e = new SlotUnlockEvent(p,pageName,slotId);
        Bukkit.getPluginManager().callEvent(e);


        List<String> unlock = unlocks.getOrDefault(pageName,new ArrayList<>());
        unlock.add(slotId);
        unlocks.put(pageName,unlock);

        update(false);
    }

    public Optional<ItemStack> getPut(String pageName,String slotId) {
        RunePageData runePageData = datas.get(pageName);
        if (runePageData != null) {
            HashMap<String,UUID> map = runePageData.getPut();
            return map.containsKey(slotId) ? LegendaryRunePlus.getLegendaryRunePlus().getPlayerRuneItemManager().getItem(map.get(slotId)) : Optional.empty();
        }
        return Optional.empty();
    }

    public List<String> getAttr(String pageName,String slotId) {
        RunePageData runePageData = datas.get(pageName);
        List<String> attrs = new ArrayList<>();
        if (runePageData != null) {
            HashMap<String, List<String>> map = runePageData.getAttrs();
            if (map.containsKey(slotId)) {
                attrs = map.get(slotId);
            }
        }
        return attrs;
    }

    public void unEquipRune(Player p,String pageName, String slotId) {
        RunePageData runePageData = datas.get(pageName);
        if (runePageData != null) {
            RuneUnEquipEvent e = new RuneUnEquipEvent(p,pageName,slotId);
            Bukkit.getPluginManager().callEvent(e);


            runePageData.remove(slotId);
            datas.put(pageName, runePageData);
            update(false);
            updateAttributes();

        }
    }

    public void putRune(Player p,String pageName,String slotId,ItemStack i) {


        RunePageData runePageData = datas.getOrDefault(pageName, new RunePageData(pageName, new HashMap<>(), new HashMap<>(), new HashMap<>()));

        NBTItem nbtItem = new NBTItem(i.clone());
        List<String> attrs = new ArrayList<>();
        String str = nbtItem.getString("LegendaryRunePlus_Attrs");
        if (str != null) {
            attrs = new Gson().fromJson(str, List.class);
        }
        String type = nbtItem.getString("LegendaryRunePlus_Id");
        runePageData.add(slotId, attrs, i, type);
        datas.put(pageName, runePageData);

        update(false);
        updateAttributes();

    }

    public int getPutAmount(String pageName,String runeId) {
        int a = 0;
        for (Map.Entry<String,RunePageData> entry : datas.entrySet()) {
            a += (int) entry.getValue().getTypes().entrySet().stream().filter(e -> e.getValue().equals(runeId)).count();
        }
        return a;
    }

    //更新符文属性
    public void updateAttributes() {
        if (LegendaryRunePlus.getLegendaryRunePlus().getAttributeProvider() != null) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                List<AttributeWriterData> attrs = new ArrayList<>();
                for (Map.Entry<String,RunePageData> entry:datas.entrySet()) {
                    List<String> pageAttrs = new ArrayList<>();
                    entry.getValue().getAttrs().forEach((s, list) -> {
                        pageAttrs.addAll(list);
                    });
                    attrs.add(new AttributeWriterData(entry.getKey(),pageAttrs));
                }
                RuneAttributeUpdateEvent e = new RuneAttributeUpdateEvent(p,attrs);
                Bukkit.getPluginManager().callEvent(e);
                LegendaryRunePlus.getLegendaryRunePlus().getAttributeProvider().update(p,e.getWriterDataList());
            }
        }
    }

    //更新缓存以及数据库
    public void update(boolean removeFromCaches) {
        Bukkit.getScheduler().runTaskAsynchronously(LegendaryRunePlus.getLegendaryRunePlus(),()->LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().update(this,removeFromCaches));
    }


}
