package com.gyzer.API;

import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LegendaryRunePlusAPI {
    private static final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    public static UserData getUserData(Player p) {
        return legendaryRunePlus.getUserDataManager().getUserData(p.getUniqueId());
    }
}
