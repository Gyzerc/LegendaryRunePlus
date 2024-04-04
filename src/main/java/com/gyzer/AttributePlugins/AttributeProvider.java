package com.gyzer.AttributePlugins;

import com.gyzer.Data.Rune.RunePageData;
import com.gyzer.Data.UserData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AttributeProvider {
    public abstract void update(Player p);
    public static boolean isEnable(AttributePlugin attributePlugin) {
        return Bukkit.getPluginManager().isPluginEnabled(attributePlugin.getPlugin());
    }
    public List<String> getAttrs(UserData data) {
        List<String> attrs = new ArrayList<>();
        for (Map.Entry<String, RunePageData> entry : data.getDatas().entrySet()) {
            RunePageData runePageData =  entry.getValue();
            if (runePageData.getAttrs() != null && !runePageData.getAttrs().isEmpty()) {
                for (Map.Entry<String, List<String>> attr : runePageData.getAttrs().entrySet()){
                    List<String> l = attr.getValue();
                    if (l != null && !l.isEmpty()) {
                        attrs.addAll(l);
                    }
                }
            }
        }
        return attrs;
    }
}
