package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.Data.AttributeWriterData;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.entity.Player;
import org.serverct.ersha.jd.AttributeAPI;

import java.util.ArrayList;
import java.util.List;

public class AttributePlus_2 extends AttributeProvider {
    @Override
    public void update(Player p, List<AttributeWriterData> writerDataList) {
        AttributeAPI.deleteAttribute(p,"LegendaryRunePlus");
        AttributeAPI.addAttribute(p,"LegendaryRunePlus",getAttrs(writerDataList),false);
    }


}
