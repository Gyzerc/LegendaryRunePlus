package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.LegendaryRunePlus;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class Sx_3 extends AttributeProvider {
    private Object SxApi;
    private Method setData;
    private Method loadAttrs;
    private Method removeData;
    public Sx_3(){
        try {
            Class<?> c = Class.forName("github.saukiya.sxattribute.SXAttribute");
            Method get = c.getMethod("getApi");
            SxApi = get.invoke(c);
            setData = SxApi.getClass().getMethod("setEntityAPIData", Class.class, UUID.class, SXAttributeData.class);
            loadAttrs = SxApi.getClass().getMethod("loadListData", List.class);
            removeData = SxApi.getClass().getMethod("removeEntityAPIData", Class.class, UUID.class);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void update(Player p) {
        removeAttribute(p.getUniqueId());
        setAttribute(p.getUniqueId(),getAttrs(LegendaryRunePlus.getLegendaryRunePlus().getUserDataManager().getUserData(p.getUniqueId())));
    }

    private void removeAttribute(UUID uuid){
        try {
            removeData.invoke(SxApi, LegendaryRunePlus.class,uuid);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAttribute(UUID uuid, List<String> attrs){
        try {
            SXAttributeData data = (SXAttributeData) loadAttrs.invoke(SxApi,attrs);
            setData.invoke(SxApi,LegendaryRunePlus.class,uuid,data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
