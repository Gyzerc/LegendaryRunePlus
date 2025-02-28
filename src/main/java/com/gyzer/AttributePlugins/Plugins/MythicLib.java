package com.gyzer.AttributePlugins.Plugins;

import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.Data.AttributeWriterData;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class MythicLib extends AttributeProvider {



    @Override
    public void update(Player p, List<AttributeWriterData> writerDataList) {
        MMOPlayerData mmoPlayerData = MMOPlayerData.get(p.getUniqueId());
        StatMap statMap = mmoPlayerData.getStatMap();
        for (StatInstance instance : statMap.getInstances()){
            instance.removeIf(i -> i.equals("LegendaryRunePlus_Attr"));
        }
        getAttrs_MythicLib(writerDataList).forEach((s, aDouble) -> {
            StatModifier statModifier = new StatModifier("LegendaryRunePlus_Attr",s,aDouble);
            statModifier.register(mmoPlayerData);
        });
    }

/**
 *
 *      反射调用MythicLib （一开始以为这个插件是java8以上才能调用的所以利用反射调用，后来发现这个插件好像可以直接引用...
 *      于是下面代码直接废弃。。。）
 *
 */
//    public void addStat(Player p, String source, String attr, double value) {
//        Object data = getMMOPlayerData(p.getUniqueId());
//        Object stat = newStat(source,attr,value);
//        register(data,stat);
//    }
//    public void removeStat(Object data,String source) {
//        try {
//            Method getMap = data.getClass().getMethod("getStatMap");
//            Object statMap = getMap.invoke(data);
//
//            Method getAll = statMap.getClass().getMethod("getInstances");
//            Collection<Object> instances = (Collection<Object>) getAll.invoke(statMap);
//            for (Object stat : instances) {
//                Method remove = stat.getClass().getMethod("removeIf", Predicate.class);
//                remove.invoke(stat,(Predicate<String>)i -> i.equals(source));
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private Object getMMOPlayerData(UUID uuid) {
//        try {
//            Class<?> clazz = Class.forName("io.lumine.mythic.lib.api.player.MMOPlayerData");
//            Constructor<?> constructor = clazz.getDeclaredConstructor(UUID.class);
//            Object o =  constructor.newInstance(uuid);
//
//            Method get = o.getClass().getMethod("get",UUID.class);
//            return get.invoke(o,uuid);
//        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
//                 InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private Object newStat(String source,String attr,double value) {
//        try {
//            Class<?> clazz = Class.forName("io.lumine.mythic.lib.api.stat.modifier.StatModifier");
//            Constructor<?> constructor = clazz.getDeclaredConstructor(String.class,String.class,double.class);
//            return constructor.newInstance(source,attr,value);
//        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
//                 InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void register(Object data,Object stat) {
//        try {
//            Method register = stat.getClass().getMethod("register", getObcClass("io.lumine.mythic.lib.api.player.MMOPlayerData"));
//            register.invoke(stat,data);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public static Class<?> getObcClass(String classPath) {
//        try {
//            return Class.forName( classPath);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
