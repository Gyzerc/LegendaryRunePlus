package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.LegendaryRunePlus;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

public class Sx_2 extends AttributeProvider {
    @Override
    public void update(Player p) {
        SXAttribute.getApi().removeEntityAPIData(LegendaryRunePlus.class,p.getUniqueId());
        SXAttributeData data = SXAttribute.getApi().getLoreData(null, null, getAttrs(LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(p.getUniqueId())));
        SXAttribute.getApi().setEntityAPIData(LegendaryRunePlus.class, p.getUniqueId(), data);
    }
}
