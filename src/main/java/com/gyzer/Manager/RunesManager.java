package com.gyzer.Manager;

import com.gyzer.Data.Rune.Rune;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class RunesManager {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private HashMap<String, Rune> runes;
    public RunesManager() {
        runes = new HashMap<>();
        loadAllRunes();
    }
    private void loadAllRunes() {
        File file = new File(legendaryRunePlus.getDataFolder()+"/Runes");
        if (!file.exists()) {
            legendaryRunePlus.saveResource("Runes/红莲魂玉.yml",false);
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File runeFile : files) {
                String fileName = runeFile.getName();
                if (!runeFile.isDirectory() && fileName.contains(".yml")) {
                    String id = fileName.replace(".yml","");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(runeFile);
                    try {
                        String display = legendaryRunePlus.color(yml.getString("display"));
                        Material material = legendaryRunePlus.getMaterial(yml.getString("material"));
                        int data = yml.getInt("data",0);
                        int model = yml.getInt("custom-model-data",0);
                        List<String> lore = legendaryRunePlus.color(yml.getStringList("lore"));
                        String type = yml.getString("settings.type");
                        int level = yml.getInt("settings.level",5);
                        int max = yml.getInt("settings.max",-1);
                        List<String> attrs = legendaryRunePlus.color(yml.getStringList("settings.attrs"));
                        String chance = yml.getString("settings.chance");
                        runes.put(id,new Rune(id,display,material,data,model,lore,type,chance,level,max,attrs));
                    } catch (NullPointerException e) {
                        legendaryRunePlus.info("检测到铭文 "+fileName+" 配置出错！", Level.SEVERE);
                    }
                }
            }
            legendaryRunePlus.msg("&e加载 &a"+runes.size()+" &e个铭文.");
        }
    }

    public Optional<Rune> getRune(String id) {
        return runes.containsKey(id) ? Optional.of(runes.get(id)) : Optional.empty();
    }

    public List<String> getRuneIds() {
        return runes.values().stream().map(Rune::getId).collect(Collectors.toList());
    }
}
