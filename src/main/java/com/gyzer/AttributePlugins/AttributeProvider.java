package com.gyzer.AttributePlugins;

import com.gyzer.Data.AttributeWriterData;
import com.gyzer.Data.Rune.RunePageData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AttributeProvider {
    public abstract void update(Player p, List<AttributeWriterData> writerDataList);
    public static boolean isEnable(AttributePlugin attributePlugin) {
        return Bukkit.getPluginManager().isPluginEnabled(attributePlugin.getPlugin());
    }
    protected List<String> getAttrs(List<AttributeWriterData> data) {
        List<String> attrs = new ArrayList<>();
        data.forEach(w -> {
            if (w.getAttrs() != null && !w.getAttrs().isEmpty()) {
                attrs.addAll(w.getAttrs());
            }
        });
        return attrs;
    }
    protected HashMap<String,Double> getAttrs_MythicLib(List<AttributeWriterData> data) {
        HashMap<String,Double> maps = new HashMap<>();
        data.forEach(attributeWriterData -> {
            if (attributeWriterData.getAttrs() != null && !attributeWriterData.getAttrs().isEmpty()) {
                attributeWriterData.getAttrs().forEach(s -> {
                    if (s.contains(":")) {
                        maps.put(s.substring(0,s.indexOf(":")),Double.parseDouble(s.substring(s.indexOf(":")+1)));
                    }
                });
            }
        });
        return maps;
    }
}
