package com.gyzer.Data.Rune;

import com.google.gson.Gson;
import com.gyzer.LegendaryRunePlus;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Rune {
    private String id;
    private String display;
    private Material material;
    private int data;
    private int model;
    private List<String> lore;
    private String type;
    private String chance;
    private int level;
    private int max;
    private List<String> attrs;

    public Rune(String id, String display, Material material, int data, int model, List<String> lore, String type, String chance, int level, int max, List<String> attrs) {
        this.id = id;
        this.display = display;
        this.material = material;
        this.data = data;
        this.model = model;
        this.lore = lore;
        this.type = type;
        this.chance = chance;
        this.level = level;
        this.max = max;
        this.attrs = attrs;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public Material getMaterial() {
        return material;
    }

    public int getData() {
        return data;
    }

    public int getModel() {
        return model;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getType() {
        return type;
    }

    public String getChance() {
        return chance;
    }

    public int getLevel() {
        return level;
    }

    public int getMax() {
        return max;
    }

    public List<String> getAttrs() {
        return attrs;
    }
    public ItemStack generate(int level) {
        List<String> attr = generateAttrs(level,attrs);
        double chance = Double.parseDouble(generateDouble(this.chance,level));

        ItemStack i = new ItemStack(material,1,(short) data);
        ItemMeta id = i.getItemMeta();
        id.setDisplayName(display.replace("%level%",String.valueOf(level)));
        List<String> lore = new ArrayList<>();
        for (String l : new ArrayList<>(this.lore)) {
            if (l.equals("%attr%")) {
                lore.addAll(attr);
            } else {
                lore.add(l.replace("%chance%",String.valueOf(chance)));
            }
        }
        if (LegendaryRunePlus.getLegendaryRunePlus().version_high) {
            id.setCustomModelData(model);
        }
        id.setLore(lore);
        i.setItemMeta(id);

        NBT.modify(i , nbtItem -> {
            nbtItem.setString("LegendaryRunePlus_Id",this.id);
            nbtItem.setString("LegendaryRunePlus_Type",this.type);
            nbtItem.setInteger("LegendaryRunePlus_Level",level);
            nbtItem.setDouble("LegendaryRunePlus_Chance",chance);
            nbtItem.setString("LegendaryRunePlus_Attrs",new Gson().toJson(attr));
        });

        return i;
    }

    private List<String> generateAttrs(int level, List<String> attrs) {
        List<String> list = new ArrayList<>();
        for (String l : new ArrayList<>(attrs)) {
            String mathStr = getMathStr(l);
            list.add(l.replace("{"+mathStr+"}",generateDouble(mathStr,level)));
        }
        return list;
    }

    private String generateDouble(String mathStr, int level) {
        String dealMathStr = dealRandom(mathStr.replace("%level%",String.valueOf(level)));
        return LegendaryRunePlus.getLegendaryRunePlus().getDigital(dealMath(dealMathStr));
    }

    private String getMathStr(String l) {
        StringBuilder builder = new StringBuilder();
        boolean begin = false;
        char[] chars = l.toCharArray();
        for (char c : chars) {
            if (begin) {
                if (c == '}') {
                    break;
                }
                builder.append(c);
            } else {
                if (c == '{') {
                    begin = true;
                    continue;
                }
            }
        }
        return builder.toString();
    }
    private String dealRandom(String str) {
        StringBuilder builder = new StringBuilder();
        StringBuilder cache = new StringBuilder();
        boolean begin = false;
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (begin) {
                if ( c == ']') {
                    begin = false;
                    String arg = cache.toString();
                    if (!arg.isEmpty()) {
                        String[] args = arg.split(";");
                        double min = dealMath(args[0]) * 100;
                        double max = dealMath(args.length > 1 ? args[1] : null) * 100 - min;
                        if (max > min) {
                            double roll =( (new Random()).nextInt((int) max) + min ) / 100;
                            builder.append(roll);
                        }
                    }
                    cache = new StringBuilder();
                    continue;
                }
                cache.append(c);
            } else {
                if (c == '[') {
                    begin = true;
                    continue;
                }
                builder.append(c);
            }
        }
        return builder.toString();
    }


    public Double dealMath(String arg)
    {
        if (arg != null || !arg.isEmpty()) {
            return LegendaryRunePlus.getLegendaryRunePlus().getMathCalculate().evaluate(arg);
        }
        return 0.0;
    }

}
