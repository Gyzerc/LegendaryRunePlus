package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.entity.Player;
import org.serverct.ersha.jd.AttributeAPI;

public class AttributePlus_2 extends AttributeProvider {
    @Override
    public void update(Player p) {
        AttributeAPI.deleteAttribute(p,"LegendaryRunePlus");
        AttributeAPI.addAttribute(p,"LegendaryRunePlus",getAttrs(LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(p.getUniqueId())),false);
    }
}
