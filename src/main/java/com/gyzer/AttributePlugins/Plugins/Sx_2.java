package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.Data.AttributeWriterData;
import com.gyzer.LegendaryRunePlus;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

import java.util.List;

public class Sx_2 extends AttributeProvider {
    @Override
    public void update(Player p, List<AttributeWriterData> writerDataList) {

        SXAttribute.getApi().removeEntityAPIData(LegendaryRunePlus.class,p.getUniqueId());
        SXAttributeData data = SXAttribute.getApi().getLoreData(null, null, getAttrs(writerDataList));
        SXAttribute.getApi().setEntityAPIData(LegendaryRunePlus.class, p.getUniqueId(), data);
    }
}
