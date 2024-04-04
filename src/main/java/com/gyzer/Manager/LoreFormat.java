package com.gyzer.Manager;

import com.gyzer.LegendaryRunePlus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoreFormat {
    private static final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    public static String papi_exp;

    public static String papi_level;

    public static String papi_vault;

    public static String papi_points;

    public static String papi_item;

    public static String papi_item_display;

    public static String papi_depend;

    public static String locked_display;

    public static String luckstone;

    public static List<String> lore_locked;

    public static List<String> lore_unEquip;

    public static List<String> lore_unlock;

    public static List<String> lore_unEquip_need;
    private static File file;
    private static YamlConfiguration yml;
    public LoreFormat() {
        file = new File(legendaryRunePlus.getDataFolder(),"lore-format.yml");
        if (!file.exists()) {
            legendaryRunePlus.saveResource("lore-format.yml",false);
        }
        yml = YamlConfiguration.loadConfiguration(file);
        papi_exp = getString("PlaceHolder.exp","&a%amount% &f原版经验值");
        papi_level = getString("PlaceHolder.level","&a%amount% &f原版等级");
        papi_vault = getString("PlaceHolder.vault","&a%amount% &f游戏币");
        papi_points = getString("PlaceHolder.points","&a%amount% &f点券");
        papi_item = getString("PlaceHolder.item-default","&a%type% &f×%amount%");
        papi_item_display = getString("PlaceHolder.item-display","&a%display% &f×%amount%");
        papi_depend = getString("PlaceHolder.depend","&e需要解锁前置铭文 %name%");
        locked_display = getString("PlaceHolder.lock-display","%display% &c[未解锁]");
        lore_locked = getStringList("Lore.Locked");
        luckstone = getString("PlaceHolder.luckstone","&6&l> &e已安装 %luckstone%");
        lore_unEquip = getStringList("Lore.unEquip");
        lore_unlock = getStringList("Lore.UnLock");
        lore_unEquip_need = getStringList("Lore.unEquip_need");
    }

    public static String getString(String path,String def)
    {
        if (yml.contains(path))
        {
            return legendaryRunePlus.color(yml.getString(path));
        }
        else {
            yml.set(path,def);
            try {
                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return def;
    }

    public static List<String> getStringList(String path)
    {
        if (yml.contains(path))
        {
            return legendaryRunePlus.color(yml.getStringList(path));
        }
        else {
            List<String> l=new ArrayList<>();
            yml.set(path,l);
            try {
                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            return l;
        }
    }
}
