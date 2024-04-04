package com.gyzer.Panel.Loader.Button;

import com.gyzer.LegendaryRunePlus;
import com.gyzer.Manager.ConfigManager;
import com.gyzer.Utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemCheck {
    private Material material;
    private int data;
    private int amount = 1;
    private String display;

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Material getMaterial() {
        return material;
    }

    public int getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public String getDisplay() {
        return display;
    }

    private final ConfigManager configManager = LegendaryRunePlus.getLegendaryRunePlus().getConfigManager();
    public boolean canPass(Player p) {
        if (material != null) {
            if (ItemUtils.hasPlayerMcItem(p,material,data) < amount) {
                p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_item.replace("%material%",material.name()).replace("%amount%",String.valueOf(amount)));
                return false;
            }
        }
        if (display != null) {
            if (ItemUtils.hasPlayerItem(p, display) < amount) {
                p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_customitem.replace("%display%",display).replace("%amount%",String.valueOf(amount)));
                return false;
            }
        }
        return true;
    }

    public void takeAway(Player p){
        if (material != null) {
            ItemUtils.TakePlayerMcItem(p,material,data,amount);
            return;
        }
        if (display != null) {
            ItemUtils.TakePlayerItem(p, display, amount);
        }
    }
    public static ItemCheck getFromString(String str) {
        ItemCheck check = new ItemCheck();
        if (str == null || str.isEmpty()) {
            return check;
        }
        String[] args = str.split(";");
        if (args.length == 2) {
            check.setDisplay(LegendaryRunePlus.getLegendaryRunePlus().color(args[0]));
            check.setAmount(Integer.parseInt(args[1]));
        }
        else if (args.length == 3) {
            check.setMaterial(LegendaryRunePlus.getLegendaryRunePlus().getMaterial(args[0]));
            check.setData(Integer.parseInt(args[1]));
            check.setAmount(Integer.parseInt(args[2]));
        }
        return check;
    }
}
