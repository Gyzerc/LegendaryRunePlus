package com.gyzer.Manager;

import com.gyzer.LegendaryRunePlus;
import com.gyzer.Panel.Loader.Button.ButtonCost;
import com.gyzer.Panel.Loader.Button.ButtonFuction;
import com.gyzer.Panel.Loader.Button.ItemCheck;
import com.gyzer.Panel.Loader.Button.RuneSettings;
import com.gyzer.Panel.Loader.MenuItem;
import com.gyzer.Panel.Loader.PanelReader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PagesManager {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private HashMap<String, PanelReader> caches;

    public PagesManager() {
        caches = new HashMap<>();
        loadAllPages();
    }

    private void loadAllPages() {
        File file = new File(legendaryRunePlus.getDataFolder() + "/Pages");
        if (!file.exists()) {
            legendaryRunePlus.saveResource("Pages/普通铭文页.yml",false);
        }
        File[] files = file.listFiles();

        if (files != null) {
            for (File pageFile : files) {
                String fileName = pageFile.getName();
                if (fileName.contains(".yml")) {
                    String id = fileName.replace(".yml","");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(pageFile);
                    try {
                        String title = legendaryRunePlus.color(yml.getString("title"));
                        String title_other = legendaryRunePlus.color(yml.getString("title_other",""));
                        List<String> layout = yml.getStringList("layout");
                        HashMap<Integer,MenuItem> items = new HashMap<>();
                        HashMap<String,MenuItem> buttons = new HashMap<>();
                        List<String> ids = new ArrayList<>();

                        ConfigurationSection section = yml.getConfigurationSection("ingredients");
                        if (section != null) {
                            for (String key : section.getKeys(false)) {

                                String path = key+".settings";
                                ItemStack i = legendaryRunePlus.getItem(section,key);
                                ButtonFuction.FuctionType fuctionType = ButtonFuction.FuctionType.valueOf(section.getString(path+".name","NULL").toUpperCase());
                                RuneSettings settings = null;
                                ButtonCost locked = null;
                                ButtonCost unEquip = null;
                                String value = null;

                                switch (fuctionType) {
                                    case CLOSE:
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,value)));
                                        break;
                                    case NULL:
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,value)));
                                        break;
                                    case PLAYER:
                                        value = legendaryRunePlus.color(section.getString(path+".value"));
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,value)));
                                        break;
                                    case CONSOLE:
                                        value = legendaryRunePlus.color(section.getString(path+".value"));
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,value)));
                                        break;
                                    case OP:
                                        value = legendaryRunePlus.color(section.getString(path+".value"));
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,value)));
                                        break;
                                    case RUNE:
                                        String slotId = section.getString(path+".rune.id");
                                        if (slotId == null || ids.contains(slotId)) {
                                            legendaryRunePlus.info("检测到铭文页 "+fileName+" 中的 "+slotId+" 重复了！",Level.SEVERE);
                                            break;
                                        }
                                        List<Integer> levels = getLevel(section,key);

                                        String type = section.getString(path+".rune.type");
                                        boolean isLocked = section.getBoolean(path+".rune.lock.enable",false);
                                        Material material = null;
                                        int data = 0;
                                        int model = 0;

                                        if (isLocked) {

                                            material = legendaryRunePlus.getMaterial(section.getString(path+".rune.lock.display.material","BARRIER"));
                                            data = section.getInt(path+".rune.lock.display.data",0);
                                            model = section.getInt(path+".rune.lock.display.custom-model-data",0);

                                            int chance = section.getInt(path+".rune.lock.require.chance",0);
                                            double vault = section.getDouble(path+".rune.lock.require.vault",0);
                                            double points = section.getDouble(path+".rune.lock.require.points",0);
                                            int level = section.getInt(path+".rune.lock.require.level",0);
                                            double exp = section.getDouble(path+".rune.lock.require.exp",0);
                                            String custom_item = section.getString(path+".rune.lock.require.custom-item");
                                            ItemCheck check_custom = ItemCheck.getFromString(custom_item);
                                            String item = section.getString(path+".rune.lock.require.item");
                                            ItemCheck check_item = ItemCheck.getFromString(item);
                                            String depend = section.getString(path+".rune.lock.require.depend");
                                            locked = new ButtonCost(depend,vault,points,level,exp,chance,check_custom,check_item);

                                        }

                                        boolean isUnEquip = section.getBoolean(path+".rune.unEquip.enable",false);
                                        if (isUnEquip) {

                                            int chance = section.getInt(path+".rune.unEquip.require.chance",-1);
                                            double vault = section.getDouble(path+".rune.unEquip.require.vault",0);
                                            double points = section.getDouble(path+".rune.unEquip.require.points",0);
                                            int level = section.getInt(path+".rune.unEquip.require.level",0);
                                            double exp = section.getDouble(path+".rune.unEquip.require.exp",0);
                                            String custom_item = section.getString(path+".rune.unEquip.require.custom-item");
                                            ItemCheck check_custom = ItemCheck.getFromString(custom_item);
                                            String item = section.getString(path+".rune.unEquip.require.item");
                                            ItemCheck check_item = ItemCheck.getFromString(item);
                                            unEquip = new ButtonCost(null,vault,points,level,exp,chance,check_custom,check_item);

                                        }

                                        settings = new RuneSettings(slotId,type,levels,isLocked,locked,material,data,model,isUnEquip,unEquip);
                                        ids.add(slotId);
                                        buttons.put(key,new MenuItem(key,i,new ButtonFuction(fuctionType,slotId,value,settings)));
                                }
                            }

                            int lineAmount = 0;
                            for (String line : layout) {
                                char[] chars = line.toCharArray();
                                for (int a = 0; a < 9; a++) {
                                    if (chars.length > a) {
                                        char c = chars[a];
                                        MenuItem get = buttons.get(c + "");
                                        if (get != null) {
                                            items.put((lineAmount * 9 + a), get);
                                        }
                                    }
                                }
                                lineAmount++;
                            }
                            caches.put(id,new PanelReader(id,title,title_other,(layout.size() * 9),items,ids));
                            legendaryRunePlus.msg("&a成功加载铭文页 - &f"+id);
                        }
                    } catch (NullPointerException e) {
                        legendaryRunePlus.info("检测到铭文页 "+fileName+" 配置出错！", Level.SEVERE);
                    }
                }
            }
            legendaryRunePlus.msg("&a成功加载 &b"+caches.size()+" &a个铭文页.");
        }
    }

    public PanelReader getPage(String id) {
        return caches.get(id);
    }

    public List<String> getPages() {
        return caches.values().stream().map(PanelReader::getId).collect(Collectors.toList());
    }

    private List<Integer> getLevel(ConfigurationSection section,String key) {
        String str = section.getString(key+".settings.rune.level");
        if (str != null) {
            if (str.contains("-")) {
                String[] args = str.split("-");
                int min = Integer.parseInt(args[0]);
                int max = Integer.parseInt(args[1]);
                if (max > min) {
                    List<Integer> list = new ArrayList<>();
                    for (int a = min; a <= max; a++) {
                        list.add(a);
                    }
                    return list;
                }
            }
            return Collections.singletonList(section.getInt(key + ".settings.rune.level"));
        }
        return Arrays.asList(1,2,3);
    }
}
