package com.gyzer.Manager;

import com.gyzer.Data.Rune.Type;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypesManager {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private HashMap<String, Type> caches;

    public TypesManager() {
        caches = new HashMap<>();
        loadAllTypes();
    }

    private void loadAllTypes() {
        File file = new File(legendaryRunePlus.getDataFolder(),"Types.yml");
        if (!file.exists()) {
            legendaryRunePlus.saveResource("Types.yml",false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.getConfigurationSection("Types");
        if (section != null) {
            for (String id : section.getKeys(false)) {
                caches.put(id,new Type(id, legendaryRunePlus.color(section.getString(id+".display"))));
            }
        }
        legendaryRunePlus.msg("&e加载 &a"+caches.size()+" &e个铭文类型.");
    }

    public Optional<Type> getType(String id) {
        return caches.containsKey(id) ? Optional.of(caches.get(id)) : Optional.empty();
    }

    public List<String> getTypeIds() {
        return caches.values().stream().map(Type::getId).collect(Collectors.toList());
    }
}
