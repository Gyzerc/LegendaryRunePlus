package com.gyzer.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ItemUtils {

    public static int hasPlayerMcItem(Player p, Material id, int ziid) {
        int sl = 0;
        int maxid = 0;
        ItemStack[] var8;
        int var7 = (var8 = p.getInventory().getContents()).length;
        for (int var6 = 0; var6 < var7; var6++) {
            ItemStack item = var8[var6];
            if (maxid < 36 && item != null && item.getType() == id)
                if (ziid != -1) {
                    if (!item.getItemMeta().hasDisplayName())
                        sl += item.getAmount();
                } else if (!item.getItemMeta().hasDisplayName()) {
                    sl += item.getAmount();
                }
            maxid++;
        }
        return sl;
    }

    public static int hasPlayerItem(Player p, String itemname) {
        int nbsl = 0;
        ItemStack[] var12;
        int var11 = (var12 = p.getInventory().getContents()).length;
        for (int var10 = 0; var10 < var11; var10++) {
            ItemStack s = var12[var10];
            if (s != null && s.getItemMeta().hasDisplayName() && s.getItemMeta().getDisplayName().equals(itemname))
                nbsl += s.getAmount();
        }
        return nbsl;
    }
    public static void TakePlayerMcItem(Player p, Material id, int ziid, int sl) {
        int zongshu = sl;
        ItemStack[] var8;
        int var7 = (var8 = p.getInventory().getContents()).length;
        for (int var6 = 0; var6 < var7; var6++) {
            ItemStack item = var8[var6];
            if (item != null && item.getType() == id)
                if (ziid != -1) {
                    if (item.getDurability() == (short)ziid && !item.getItemMeta().hasDisplayName()) {
                        if (item.getAmount() > zongshu) {
                            removeItem(p, item);
                            item.setAmount(item.getAmount() - zongshu);
                            p.getInventory().addItem(new ItemStack[] { item });
                            break;
                        }
                        if (item.getAmount() == zongshu) {
                            removeItem(p, item);
                            break;
                        }
                        removeItem(p, item);
                        zongshu -= item.getAmount();
                    }
                } else if (!item.getItemMeta().hasDisplayName()) {
                    if (item.getAmount() > zongshu) {
                        removeItem(p, item);
                        item.setAmount(item.getAmount() - zongshu);
                        p.getInventory().addItem(new ItemStack[] { item });
                        break;
                    }
                    if (item.getAmount() == zongshu) {
                        removeItem(p, item);
                        break;
                    }
                    removeItem(p, item);
                    zongshu -= item.getAmount();
                }
        }
        p.updateInventory();
    }

    public static void removeItem(Player p, ItemStack item) {
        int id = 0;
        ItemStack[] var6;
        int var5 = (var6 = p.getInventory().getContents()).length;
        for (int var4 = 0; var4 < var5; var4++) {
            ItemStack i = var6[var4];
            if (i != null && i.equals(item)) {
                p.getInventory().setItem(id, null);
                return;
            }
            id++;
        }
    }

    public static void removeItem(Player p, Material itemid) {
        int id = 0;
        ItemStack[] var6;
        int var5 = (var6 = p.getInventory().getContents()).length;
        for (int var4 = 0; var4 < var5; var4++) {
            ItemStack i = var6[var4];
            if (i != null && i.getType() == itemid) {
                p.getInventory().setItem(id, null);
                return;
            }
            id++;
        }
    }

    public static boolean TakePlayerItem(Player p, String itemname, int takesl) {
        boolean cg = false;
        boolean jg = false;
        int nbsl = 0;
        int ybsl = 0;
        ItemStack nbitem = null;
        ItemStack ybitem = null;
        ItemStack[] var12;
        int var11 = (var12 = p.getInventory().getContents()).length;
        for (int var10 = 0; var10 < var11; var10++) {
            ItemStack s = var12[var10];
            if (s != null && s.getItemMeta().hasDisplayName() && s.getItemMeta().getDisplayName().equals(itemname)) {
                jg = true;
                nbsl += s.getAmount();
                nbitem = s.clone();
                removeItem(p, s);
            }
        }
        if (!jg)
            return false;
        if (ybsl + nbsl > takesl) {
            cg = true;
            if (ybsl >= takesl) {
                ybsl -= takesl;
            } else {
                nbsl -= takesl - ybsl;
                ybsl = 0;
            }
        } else if (ybsl + nbsl < takesl) {
            cg = false;
        } else {
            cg = true;
            ybsl = 0;
            nbsl = 0;
        }
        if (ybsl > 0) {
            ybitem.setAmount(ybsl);
            p.getInventory().addItem(new ItemStack[] { ybitem });
        }
        if (nbsl > 0) {
            nbitem.setAmount(nbsl);
            p.getInventory().addItem(new ItemStack[] { nbitem });
        }
        p.updateInventory();
        return cg;
    }

}
