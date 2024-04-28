package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.Data.AttributeWriterData;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.entity.Player;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;

import java.util.List;

public class AttributePlus_3 extends AttributeProvider {
    @Override
    public void update(Player p, List<AttributeWriterData> attributeWriterData) {
        UserData data = LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(p.getUniqueId());
        AttributeData attributeData = AttributePlus.attributeManager.getAttributeData(p);
        attributeData.takeApiAttribute("LegendaryRunePlus");
        AttributeAPI.addSourceAttribute(attributeData,"LegendaryRunePlus",getAttrs(attributeWriterData),true);
    }
}
