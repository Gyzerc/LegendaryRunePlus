package com.gyzer.Panel;

import com.gyzer.Data.Rune.Rune;
import com.gyzer.Data.Rune.Type;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import com.gyzer.Manager.ConfigManager;
import com.gyzer.Manager.LoreFormat;
import com.gyzer.Panel.Loader.Button.ButtonCost;
import com.gyzer.Panel.Loader.Button.ButtonFuction;
import com.gyzer.Panel.Loader.Button.ItemCheck;
import com.gyzer.Panel.Loader.Button.RuneSettings;
import com.gyzer.Panel.Loader.MenuItem;
import com.gyzer.Panel.Loader.PanelReader;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class Panel implements InventoryHolder {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private final Inventory inv;
    private final Player p;
    private final UUID target;
    private final PanelReader panelReader;
    private final HashMap<Integer, MenuItem> menuItem;
    public Panel(Player opener,UUID target, PanelReader panelReader) {
        this.p = opener;
        this.target = target;
        this.panelReader = panelReader;
        this.menuItem = new HashMap<>();
        if (target.equals(opener.getUniqueId())) {
            this.inv = Bukkit.createInventory(this, panelReader.getSize(), panelReader.getTitle());
        } else {
            OfflinePlayer offlinePlayer =Bukkit.getOfflinePlayer(target);
            this.inv = Bukkit.createInventory(this, panelReader.getSize(), panelReader.getTitle_other().replace("%player%",offlinePlayer.getName()));
        }
        Bukkit.getScheduler().runTaskAsynchronously(legendaryRunePlus, this::loadPanel);
    }

    private void loadPanel() {
        String pageName = panelReader.getId();
        UserData data = legendaryRunePlus.getUserDataManager().getUserData(target);
        panelReader.getItems().forEach(((integer, menuItem) -> {
            this.menuItem.put(integer,menuItem);
            if (menuItem.getFuction().getType().equals(ButtonFuction.FuctionType.RUNE)) {
                RuneSettings settings = menuItem.getFuction().getRuneSettings();
                String slotId = settings.getSlotId();

                ItemStack i = null;
                ItemMeta id = null;
                //当该槽位有铭文镶嵌的lore
                if (data.getPut(pageName,slotId).isPresent()) {
                    i = data.getPut(pageName,slotId).get().clone();
                    id = i.getItemMeta();
                    List<String> lore = id.hasLore() ? id.getLore() : new ArrayList<>();

                    if (target.equals(p.getUniqueId())) {
                        if (settings.isUnEquip()) {
                            new ArrayList<>(LoreFormat.lore_unEquip_need).forEach(l -> {
                                String input = l.replace("%unequip_chance%", String.valueOf(settings.getUnEquipRequire().getChance()));
                                String replacer = replacer(input, settings.getUnEquipRequire());
                                if (replacer != null) {
                                    lore.add(replacer);
                                }
                            });
                        } else {
                            lore.addAll(new ArrayList<>(LoreFormat.lore_unEquip));
                        }
                    }

                    id.setLore(lore);
                    i.setItemMeta(id);

                }
                //当该槽位没有铭文镶嵌
                else {
                    String level = "";
                    if (settings.getLevel().size() == 1) {
                        level = String.valueOf(settings.getLevel().get(0));
                    } else if (settings.getLevel().size() > 1) {
                        level = settings.getLevel().get(0) + "-" + settings.getLevel().get(settings.getLevel().size()-1);
                    }

                    i = menuItem.getItem().clone();
                    id = i.getItemMeta();
                    String display = id.hasDisplayName() ? id.getDisplayName() : "未设置名称";

                    //当该槽位已解锁或者不需要解锁的lore
                    if (data.hasUnlockSlot(pageName,slotId) || !settings.isLocked()) {
                        Type type = legendaryRunePlus.getTypesManager().getType(settings.getType()).orElse(null);
                        List<String> lore = new ArrayList<>();
                        if (target.equals(p.getUniqueId())) {
                            for (String l : new ArrayList<>(LoreFormat.lore_unlock)) {
                                lore.add(l.replace("%type%", (type != null ? type.getDisplay() : ""))
                                        .replace("%level%", level));
                            }
                        }
                        id.setLore(lore);
                        i.setItemMeta(id);
                    }
                    //当该槽位未解锁且解锁需要花费时的lore
                    else {
                        i = new ItemStack(settings.getLocked_materil(),i.getAmount(),(short) settings.getLocked_data());
                        id.setDisplayName(LoreFormat.locked_display.replace("%display%",display));
                        if (legendaryRunePlus.isVersion_high()) {
                            id.setCustomModelData(settings.getLocked_model());
                        }
                        List<String> lore = new ArrayList<>();
                        if (target.equals(p.getUniqueId())) {
                            //检测解锁该槽位是否需要花费
                            if (settings.isLocked()) {
                                Type type = legendaryRunePlus.getTypesManager().getType(settings.getType()).orElse(null);
                                for (String l : new ArrayList<>(LoreFormat.lore_locked)) {
                                    String back = l.replace("%level%", level).replace("%type%", (type != null ? type.getDisplay() : "")).replace("%put_chance%", String.valueOf(settings.getUnlockRequire() != null ? settings.getUnlockRequire().getChance() : 100));
                                    String replacer = replacer(back, settings.getUnlockRequire());
                                    if (replacer != null) {
                                        lore.add(replacer);
                                    }
                                }
                            }
                        }
                        id.setLore(lore);
                        i.setItemMeta(id);
                    }
                }
                inv.setItem(integer,i);
            }
            else {
                inv.setItem(integer , menuItem.getItem());
            }
        }));
    }

    public String getPageName() {
        return panelReader.getId();
    }
    private String replacer(String input,ButtonCost cost) {
        if (input != null && !input.isEmpty()) {
            switch (input) {
                case "%placeholder_vault%":
                    if (cost.getVault() > 0) {
                        return LoreFormat.papi_vault.replace("%amount%", String.valueOf(cost.getVault()));
                    } else {
                        return null;
                    }
                case "%placeholder_points%":
                    if (cost.getPoints() > 0) {
                        return LoreFormat.papi_points.replace("%amount%", String.valueOf(cost.getPoints()));
                    } else {
                        return null;
                    }
                case "%placeholder_exp%":
                    if (cost.getExp() > 0) {
                        return LoreFormat.papi_exp.replace("%amount%",String.valueOf(cost.getExp()));
                    } else {
                        return null;
                    }
                case "%placeholder_level%":
                    if (cost.getLevel() > 0) {
                        return LoreFormat.papi_level.replace("%amount%",String.valueOf(cost.getLevel()));
                    } else {
                        return null;
                    }
                case "%placeholder_item_display%":
                    ItemCheck custom = cost.getCustomItem();
                    if (custom.getDisplay() != null) {
                        return LoreFormat.papi_item_display.replace("%display%",custom.getDisplay()).replace("%amount%",String.valueOf(custom.getAmount()));
                    } else {
                        return null;
                    }
                case "%placeholder_item_default%":
                    ItemCheck item = cost.getDefaultItem();
                    if (item.getMaterial() != null) {
                        return LoreFormat.papi_item.replace("%type%",item.getMaterial().name()).replace("%amount%",String.valueOf(item.getAmount()));
                    } else {
                        return null;
                    }
                case "%placeholder_depend%":
                    if (cost.getDepend() != null) {
                        return LoreFormat.papi_depend.replace("%name%",panelReader.getRuneSlotDisplayById(cost.getDepend()));
                    } else {
                        return null;
                    }
            }
            return input;
        }
        return "";
    }



    public UUID getTarget() {
        return target;
    }

    public Player getPlayer() {
        return p;
    }

    public void open() {
        Bukkit.getScheduler().runTask(legendaryRunePlus,()->p.openInventory(inv));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public PanelReader getPanelReader() {
        return panelReader;
    }

    private final ConfigManager configManager = legendaryRunePlus.getConfigManager();
    public void onClick(InventoryClickEvent e) {
        if (e.getRawSlot() >= 0 && e.getRawSlot() <= panelReader.getSize()) {
            e.setCancelled(true);
            ItemStack clickItem = e.getCurrentItem();
            if (clickItem != null && !clickItem.getType().equals(Material.AIR)) {
                MenuItem menuItem = this.menuItem.get(e.getRawSlot());
                if (menuItem != null) {
                    ButtonFuction fuction = menuItem.getFuction();
                    ButtonFuction.FuctionType fuctionType = fuction.getType();
                    switch (fuctionType) {
                        case CLOSE:
                            p.closeInventory();
                            return;
                        case PLAYER:
                            if (!target.equals(p.getUniqueId())) {
                                return;
                            }
                            p.performCommand(fuction.getValue() != null ? fuction.getValue().replace("%player%", p.getName()) : "");
                            return;
                        case CONSOLE:
                            if (!target.equals(p.getUniqueId())) {
                                return;
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fuction.getValue() != null ? fuction.getValue().replace("%player%", p.getName()) : "");
                            return;
                        case OP:
                            if (!target.equals(p.getUniqueId())) {
                                return;
                            }
                            String cmd = fuction.getValue() != null ? fuction.getValue().replace("%player%", p.getName()) : "";
                            boolean isOp = p.isOp();
                            p.setOp(true);
                            p.performCommand(cmd);
                            p.setOp(isOp);
                            return;
                        case RUNE:
                            if (!target.equals(p.getUniqueId())) {
                                return;
                            }
                            String pageId = panelReader.getId();
                            UserData data = legendaryRunePlus.getUserDataManager().getUserData(target);
                            RuneSettings settings = fuction.getRuneSettings();
                            String slotId = settings.getSlotId();
                            if (e.isLeftClick()) {
                                ItemStack putItem = e.getCursor();
                                if (putItem != null && !putItem.getType().equals(Material.AIR)) {
                                    //已镶嵌
                                    if (data.getPut(pageId,slotId).isPresent()) {
                                        configManager.sound_put_already_has.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                        p.sendMessage(configManager.lang_plugin + configManager.lang_cant_put_has);
                                        return;
                                    }
                                    //可放置
                                    if (!settings.isLocked() || data.hasUnlockSlot(pageId, slotId)) {
                                        String slotType = settings.getType();
                                        Type type = legendaryRunePlus.getTypesManager().getType(slotType).orElse(null);
                                        if (type != null) {
                                            Rune rune = isRune(putItem);
                                            if (rune != null) {
                                                if (slotType.equals(rune.getType())) {
                                                    int max = rune.getMax();
                                                    if (max > data.getPutAmount(pageId,rune.getId())) {
                                                        if (putItem.getAmount() > 1) {
                                                            configManager.sound_put_already_has.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                                            p.sendMessage(configManager.lang_plugin + configManager.lang_cant_put_amount);
                                                            return;
                                                        }
                                                        if (!settings.getLevel().contains(getLevel(putItem))) {
                                                            configManager.sound_put_already_has.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                                            p.sendMessage(configManager.lang_plugin + configManager.lang_cant_put_level);
                                                            return;
                                                        }
                                                        int chance = getChance(putItem);
                                                        if ((new Random()).nextInt(101) <= chance) {
                                                            configManager.sound_put_successfully.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                                            p.sendMessage(configManager.lang_plugin + configManager.lang_rune_put_successfully);
                                                            ItemStack put = putItem.clone();
                                                            putItem.setAmount(0);

                                                            data.putRune(p,pageId,slotId,put);
                                                            e.setCurrentItem(put);

                                                            new Panel(p,target,panelReader).open();
                                                            return;
                                                        }
                                                        putItem.setAmount(0);
                                                        configManager.sound_put_fail.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                                        p.sendMessage(configManager.lang_plugin + configManager.lang_rune_put_fail);
                                                        return;
                                                    }
                                                    p.sendMessage(configManager.lang_plugin + configManager.lang_cant_put_max);
                                                    return;
                                                }
                                                p.sendMessage(configManager.lang_plugin + configManager.lang_cant_put_type.replace("%type%",type.getDisplay()));
                                                return;
                                            }
                                            p.sendMessage(configManager.lang_plugin + configManager.lang_not_rune);
                                            return;
                                        }
                                        p.sendMessage(configManager.lang_plugin + configManager.lang_null_rune_type.replace("%type%",slotType));
                                        return;
                                    }
                                }
                                //进行解锁检测
                                else {
                                    if (!data.getPut(pageId,slotId).isPresent() && settings.isLocked() && !data.hasUnlockSlot(pageId,slotId)) {
                                        if (settings.getUnlockRequire().canPass(p, this)) {
                                            settings.getUnlockRequire().takeAway(p);
                                            data.unlockSlot(p, pageId, slotId);

                                            configManager.sound_unlock.ifPresent(s -> p.playSound(p.getLocation(), s, 1, 1));
                                            p.sendMessage(configManager.lang_plugin + configManager.lang_unlock_slot.replace("%display%" ,panelReader.getRuneSlotDisplayById(slotId)));
                                            new Panel(p, target, getPanelReader()).open();
                                        }
                                    }
                                }
                            } else if (e.isRightClick()) {
                                ItemStack putItem = e.getCursor();
                                if (putItem == null || putItem.getType().equals(Material.AIR)) {
                                    Optional<ItemStack> re = data.getPut(pageId,slotId);
                                    if (!re.isPresent()) {
                                        return;
                                    }
                                    ItemStack runeItem = re.get();

                                    if (settings.isUnEquip()) {
                                        if (settings.getUnEquipRequire().getChance() > 0) {
                                            if ((new Random()).nextInt(101) > settings.getUnEquipRequire().getChance()) {
                                                configManager.sound_put_already_has.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                                p.sendMessage(configManager.lang_plugin + configManager.lang_rune_unequip_fail);
                                                return;
                                            }
                                        }
                                        if (!settings.getUnEquipRequire().canPass(p,this)) {
                                            configManager.sound_put_already_has.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));
                                            p.sendMessage(configManager.lang_plugin + configManager.lang_rune_unequip_fail);
                                            return;
                                        }
                                        settings.getUnEquipRequire().takeAway(p);
                                    }
                                    configManager.sound_unequip.ifPresent(s -> p.playSound(p.getLocation(),s,1,1));

                                    String display = runeItem.getItemMeta().hasDisplayName() ? runeItem.getItemMeta().getDisplayName() : "";
                                    p.sendMessage(configManager.lang_plugin + configManager.lang_rune_unequip_successfully.replace("%display%",display));
                                    p.getInventory().addItem(runeItem);
                                    data.unEquipRune(p,pageId,slotId);
                                    new Panel(p,target,panelReader).open();
                                }
                            }
                    }
                }
            }
        }
    }

    public void onDrag(InventoryDragEvent e) {
        if (!target.equals(p.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        for (int slot : e.getRawSlots()) {
            if (slot >= 0 && slot < panelReader.getSize()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    private Rune isRune(ItemStack i) {
        String id = NBT.get(i, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("LegendaryRunePlus_Id"));
        if (id != null) {
            return legendaryRunePlus.getRunesManager().getRune(id).orElse(null);
        }
        return null;
    }

    private int getChance(ItemStack i) {
        double chance = NBT.get(i , (Function<ReadableItemNBT, Double>) nbt -> nbt.getDouble("LegendaryRunePlus_Chance"));
        return (int) chance;
    }
    private int getLevel(ItemStack i) {
        int chance = NBT.get(i , (Function<ReadableItemNBT, Integer>) nbt -> nbt.getInteger("LegendaryRunePlus_Level"));
        return  chance;
    }
}
