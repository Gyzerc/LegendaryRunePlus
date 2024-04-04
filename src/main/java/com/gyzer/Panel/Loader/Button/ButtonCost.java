package com.gyzer.Panel.Loader.Button;

import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import com.gyzer.Manager.ConfigManager;
import com.gyzer.Panel.Panel;
import org.bukkit.entity.Player;

public class ButtonCost {
    private String depend;
    private double vault;
    private double points;
    private int level;
    private double exp;
    private int chance;
    private ItemCheck customItem;
    private ItemCheck defaultItem;

    public ButtonCost(String depend, double vault, double points, int level, double exp, int chance, ItemCheck customItem, ItemCheck defaultItem) {
        this.depend = depend;
        this.vault = vault;
        this.points = points;
        this.level = level;
        this.exp = exp;
        this.chance = chance;
        this.customItem = customItem;
        this.defaultItem = defaultItem;
    }

    public int getChance() {
        return chance;
    }

    public ItemCheck getDefaultItem() {
        return defaultItem;
    }

    public String getDepend() {
        return depend;
    }

    public double getVault() {
        return vault;
    }

    public double getPoints() {
        return points;
    }

    public int getLevel() {
        return level;
    }

    public double getExp() {
        return exp;
    }

    public ItemCheck getCustomItem() {
        return customItem;
    }
    private final ConfigManager configManager = LegendaryRunePlus.getLegendaryRunePlus().getConfigManager();




    public boolean canPass(Player p, Panel panel) {
        if (vault > 0) {
            if (LegendaryRunePlus.getLegendaryRunePlus().getEconomy() != null) {
                if (!LegendaryRunePlus.getLegendaryRunePlus().getEconomy().has(p,vault)) {
                    p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_valut.replace("%amount%",String.valueOf(vault)));
                    return false;
                }
            }
        }
        if (points > 0) {
            if (LegendaryRunePlus.getLegendaryRunePlus().getPlayerPoints() != null) {
                if (LegendaryRunePlus.getLegendaryRunePlus().getPlayerPoints().look(p.getUniqueId()) < points) {
                    p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_points.replace("%amount%",String.valueOf(points)));
                    return false;
                }
            }
        }
        if (level > 0) {
            if (p.getLevel() < level) {
                p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_level.replace("%amount%",String.valueOf(level)));
                return false;
            }
        }
        if (exp > 0) {
            if (p.getTotalExperience() < exp) {
                p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_exp.replace("%amount%",String.valueOf(exp)));
                return false;
            }
        }
        if (depend != null) {
            String pageName = panel.getPageName();
            UserData data = LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(p.getUniqueId());
            String display = panel.getPanelReader().getRuneSlotDisplayById(depend);
            if (!display.isEmpty() && !data.hasUnlockSlot(pageName,depend)) {
                p.sendMessage(configManager.lang_plugin + configManager.lang_not_enough_depend.replace("%display%",display));
                return false;
            }
        }
        if (!defaultItem.canPass(p)) {
            return false;
        }
        if (!customItem.canPass(p)) {
            return false;
        }
        return true;
    }

    public void takeAway(Player p){
        if (vault > 0) {
            if (LegendaryRunePlus.getLegendaryRunePlus().getEconomy() != null) {
                LegendaryRunePlus.getLegendaryRunePlus().getEconomy().withdrawPlayer(p,vault);
            }
        }
        if (points > 0) {
            if (LegendaryRunePlus.getLegendaryRunePlus().getPlayerPoints() != null) {
                LegendaryRunePlus.getLegendaryRunePlus().getPlayerPoints().take(p.getUniqueId(), (int) points);
            }
        }
        if (level > 0) {
            p.setLevel( p.getLevel() - level);
        }
        if (exp > 0) {
            p.setTotalExperience((int) (p.getTotalExperience() - exp));
        }
        defaultItem.takeAway(p);
        customItem.takeAway(p);
    }
}
