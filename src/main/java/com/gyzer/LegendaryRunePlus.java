package com.gyzer;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.google.gson.Gson;
import com.gyzer.AttributePlugins.AttributePlugin;
import com.gyzer.AttributePlugins.AttributeProvider;
import com.gyzer.AttributePlugins.Plugins.*;
import com.gyzer.Commands.LegendaryCommands;
import com.gyzer.Data.Database.MySQL;
import com.gyzer.Data.Database.Provider.DataProvider;
import com.gyzer.Data.Database.SQLite;
import com.gyzer.Listeners.EventsListener;
import com.gyzer.Manager.*;
import com.gyzer.Utils.MsgUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class LegendaryRunePlus extends JavaPlugin {

    private static LegendaryRunePlus legendaryRunePlus;
    private MsgUtils msgUtils;
    private ConfigManager configManager;
    private RunesManager runesManager;
    private TypesManager typesManager;
    private PagesManager pagesManager;
    private DataProvider dataProvider;
    private UserDataManager userDataManager;
    private DoubleEvaluator mathCalculate;
    private Economy economy;
    private PlayerPointsAPI playerPoints;
    private AttributeProvider attributeProvider;
    private PlayerRuneItemManager playerRuneItemManager;
    public boolean version_high;

    @Override
    public void onEnable() {

        long time = System.currentTimeMillis();
        new NBTItem(new ItemStack(Material.PAPER)).setString("11","11");
        legendaryRunePlus = this;
        //获取是否高版本
        version_high = BukkitVersionHigh();
        msgUtils = new MsgUtils();
        reload();
        new LegendaryCommands();
        setUpDatabase();
        setUpAttributePlugin();
        userDataManager = new UserDataManager();
        playerRuneItemManager = new PlayerRuneItemManager() ;
        mathCalculate = new DoubleEvaluator();
        Bukkit.getPluginManager().registerEvents(new EventsListener(),this);
        setUpIngredients();
        msg("&e插件启动成功！ 耗时&f"+(System.currentTimeMillis()-time)+"ms");
        msg("&c插件定制联系: &2QQ3081472612 &f| &c插件官方交流群: &2762518453");
        new Metrics(this,21510);


    }

    @Override
    public void onDisable() {
        userDataManager.disable();
        dataProvider.closeDataBase();
    }


    private void setUpAttributePlugin() {
        switch (configManager.attributePlugin) {
            case AP3:
                if (AttributeProvider.isEnable(AttributePlugin.AP3)) {
                    attributeProvider = new AttributePlus_3();
                    msg("&b检测到属性插件 &eAttributePlus - 3.X &b已挂钩");
                    return;
                }
            case AP2:
                if (AttributeProvider.isEnable(AttributePlugin.AP2)) {
                    attributeProvider = new AttributePlus_2();
                    msg("&b检测到属性插件 &eAttributePlus - 2.X &b已挂钩");
                    return;
                }
            case SX3:
                if (AttributeProvider.isEnable(AttributePlugin.SX3)) {
                    attributeProvider = new Sx_3();
                    msg("&b检测到属性插件 &eSX-Attribute - 3.X &b已挂钩");
                    return;
                }
            case SX2:
                if (AttributeProvider.isEnable(AttributePlugin.SX2)) {
                    attributeProvider = new Sx_2();
                    msg("&b检测到属性插件 &eSX-Attribute - 2.X &b已挂钩");
                    return;
                }
            case MYTHICLIB:
                if (AttributeProvider.isEnable(AttributePlugin.MYTHICLIB)) {
                    attributeProvider = new MythicLib();
                    msg("&b检测到属性插件 &eMythicLib &b已挂钩");
                    return;
                }
            default:
                msg("&4未检测到服务器内相关的属性插件！");
        }
    }

    private void setUpIngredients() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                msg("&b已挂钩 &eVault &b插件.");
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            playerPoints = PlayerPoints.getInstance().getAPI();
            msg("&b已挂钩 &ePlayerPoints &b插件.");
        }
    }


    private void setUpDatabase() {
        if (getConfig().getString("Store","SQLite").equals("MySQL")) {
            msg("&d检测到你目前使用 &EMySQL &d存储数据.");
            dataProvider = new MySQL();
            return;
        }
        msg("&d检测到你目前使用 &ESQLite &d存储数据.");
        dataProvider = new SQLite();
    }


    public void reload() {
        configManager = new ConfigManager();
        typesManager = new TypesManager();
        runesManager = new RunesManager();
        pagesManager = new PagesManager();
        new LoreFormat();
    }

    private boolean BukkitVersionHigh() {
        String name = Bukkit.getServer().getBukkitVersion();
        String versionStr =  name.substring(0,name.indexOf("-"));
        List<String> groups = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (char c : versionStr.toCharArray()) {
            if (c == '.') {
                groups.add(builder.toString());
                builder = new StringBuilder();
                continue;
            }
            builder.append(c);
        }
        groups.add(builder.toString());
        int version = Integer.parseInt(groups.get(1));
        return (version >= 13);
    }

    public static LegendaryRunePlus getLegendaryRunePlus() {
        return legendaryRunePlus;
    }


    public String color(String msg){return msgUtils.msg(msg);}
    public List<String> color(List<String> msg){return msgUtils.msg(msg);}

    public MsgUtils getMsgUtils() {
        return msgUtils;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RunesManager getRunesManager() {
        return runesManager;
    }

    public TypesManager getTypesManager() {
        return typesManager;
    }

    public PagesManager getPagesManager() {
        return pagesManager;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public UserDataManager getUserDataManager() {
        return userDataManager;
    }

    public DoubleEvaluator getMathCalculate() {
        return mathCalculate;
    }

    public AttributeProvider getAttributeProvider() {
        return attributeProvider;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerPointsAPI getPlayerPoints() {
        return playerPoints;
    }

    public PlayerRuneItemManager getPlayerRuneItemManager() {
        return playerRuneItemManager;
    }

    public boolean isVersion_high() {
        return version_high;
    }

    public void info(String msg, Level level, Throwable throwable){
        getLogger().log(level,msg,throwable);
    }
    public void info(String msg, Level level){
        getLogger().log(level,msg);
    }
    public Material getMaterial(String str) {
            return Material.getMaterial(str) != null ? Material.getMaterial(str) : Material.STONE;
    }



    public void msg(String str) {
        Bukkit.getConsoleSender().sendMessage(legendaryRunePlus.getConfigManager().lang_plugin + color(str));
    }

    public ItemStack getItem(ConfigurationSection section,String key) {
        ItemStack i = new ItemStack(getMaterial(section.getString(key+".material","STONE")),section.getInt(key+".amount",1),(short) section.getInt(key+".data",0));
        ItemMeta id = i.getItemMeta();
        id.setDisplayName(color(section.getString(key+".display","")));
        if (version_high) {
            id.setCustomModelData(section.getInt(key+".custom-model-data",0));
        }
        id.setLore(color(section.getStringList(key+".lore")));
        i.setItemMeta(id);
        return i;
    }
    public String getDigital(double a) {
        char[] chars = String.valueOf(a).toCharArray();
        boolean begin = false;
        int y = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            if (begin) {
                builder.append(c);
                if (y == 1) {
                    break;
                }
                y++;
            } else {
                if (c == '.') {
                    begin = true;
                }
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
