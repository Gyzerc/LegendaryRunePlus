package com.gyzer.Manager;

import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserDataManager {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private ConcurrentHashMap<UUID, UserData> caches;

    public UserDataManager() {
        caches = new ConcurrentHashMap<>();
    }

    public UserData getUserData(UUID uuid) {
        UserData data = caches.get(uuid);
        if (data == null) {
            data = legendaryRunePlus.getDataProvider().getUserData(uuid).orElse(new UserData(uuid,new HashMap<>(),new HashMap<>()));
            caches.put(uuid,data);
        }
        return data;
    }

    public void update(UserData data,boolean removeFromCaches) {
        legendaryRunePlus.getDataProvider().saveUserData(data);
        if (removeFromCaches) {
            caches.remove(data.getUuid());
            return;
        }
        caches.put(data.getUuid(),data);
    }

    public void update(Player p) {
        UUID uuid = p.getUniqueId();
        UserData data = caches.remove(uuid);
        if (data != null) {
            legendaryRunePlus.getDataProvider().saveUserData(data);
            legendaryRunePlus.msg("&e已保存玩家 &3"+p.getName()+" &e的缓存数据.");
        }
    }

    public void disable() {
        caches.forEach((uuid, data) -> {
            legendaryRunePlus.getDataProvider().saveUserData(data);
        });
        legendaryRunePlus.msg("&e成功保存 &a"+caches.size()+" &e个数据缓存.");
    }
}
