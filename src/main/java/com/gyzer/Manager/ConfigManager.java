package com.gyzer.Manager;

import com.gyzer.AttributePlugins.AttributePlugin;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ConfigManager {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    public String lang_notinteger;
    public String lang_notonline;
    public String lang_null_player;
    public String lang_plugin;
    public String lang_unknown_command;
    public List<String> lang_help;
    public String lang_permission;
    public String lang_not_has_page;
    public String lang_not_has_rune;
    public String lang_not_has_slot;
    public String lang_not_has_rune_level;
    public String lang_give_rune;
    public String lang_received_rune;
    public String lang_not_rune;
    public String lang_cant_put_type;
    public String lang_cant_put_amount;
    public String lang_cant_put_has;
    public String lang_cant_put_max;
    public String lang_cant_put_level;
    public String lang_not_enough_valut;
    public String lang_not_enough_points;
    public String lang_not_enough_level;
    public String lang_not_enough_exp;
    public String lang_not_enough_depend;
    public String lang_not_enough_item;
    public String lang_not_enough_customitem;
    public String lang_null_rune_type;
    public String lang_rune_put_fail;
    public String lang_rune_put_successfully;
    public String lang_unlock_slot;
    public String lang_unlock_slot_admin;
    public String lang_lock_slot;
    public String lang_lock_slot_admin;
    public String lang_rune_unequip_successfully;
    public String lang_rune_unequip_fail;
    public Optional<Sound> sound_put_successfully;
    public Optional<Sound> sound_put_fail;
    public Optional<Sound> sound_put_already_has;
    public Optional<Sound> sound_unlock;
    public Optional<Sound> sound_unequip;
    public AttributePlugin attributePlugin;

    public ConfigManager() {
        legendaryRunePlus.saveDefaultConfig();
        legendaryRunePlus.reloadConfig();
        FileConfiguration yml = legendaryRunePlus.getConfig();

        attributePlugin = AttributePlugin.getPlugin(yml.getString("AttributePlugin","AP3"));

        lang_plugin = legendaryRunePlus.color(yml.getString("lang.plugin"));
        lang_help = legendaryRunePlus.color(yml.getStringList("lang.help"));
        lang_unknown_command= legendaryRunePlus.color(yml.getString("lang.unknown_command","&c未知指令."));
        lang_permission = legendaryRunePlus.color(yml.getString("lang.permission","&c你没有使用该命令的权限"));
        lang_notonline = legendaryRunePlus.color(yml.getString("lang.notonline","&c该玩家不在线"));
        lang_null_player = legendaryRunePlus.color(yml.getString("lang.null-player"));
        lang_notinteger = legendaryRunePlus.color(yml.getString("lang.notinteger","&c请输入正确的整数"));
        lang_not_has_page = legendaryRunePlus.color(yml.getString("lang.not-has-page"));
        lang_not_has_rune = legendaryRunePlus.color(yml.getString("lang.not-has-rune"));
        lang_not_has_rune_level = legendaryRunePlus.color(yml.getString("lang.not-has-rune-level"));
        lang_not_has_slot = legendaryRunePlus.color(yml.getString("lang.not-has-slot"));
        lang_give_rune = legendaryRunePlus.color(yml.getString("lang.give-rune"));
        lang_received_rune = legendaryRunePlus.color(yml.getString("lang.received-rune"));
        lang_not_rune = legendaryRunePlus.color(yml.getString("lang.not-rune"));
        lang_cant_put_type = legendaryRunePlus.color(yml.getString("lang.cant-put-type"));
        lang_cant_put_amount = legendaryRunePlus.color(yml.getString("lang.cant-put-amount"));
        lang_cant_put_has = legendaryRunePlus.color(yml.getString("lang.cant-put-has"));
        lang_cant_put_max = legendaryRunePlus.color(yml.getString("lang.cant-put-max"));
        lang_cant_put_level = legendaryRunePlus.color(yml.getString("lang.cant-put-level"));
        lang_not_enough_valut = legendaryRunePlus.color(yml.getString("lang.not-enough-vault"));
        lang_not_enough_points = legendaryRunePlus.color(yml.getString("lang.not-enough-points"));
        lang_not_enough_level = legendaryRunePlus.color(yml.getString("lang.not-enough-level"));
        lang_not_enough_exp = legendaryRunePlus.color(yml.getString("lang.not-enough-exp"));
        lang_not_enough_depend = legendaryRunePlus.color(yml.getString("lang.not-enough-depend"));
        lang_not_enough_item = legendaryRunePlus.color(yml.getString("lang.not-enough-item"));
        lang_not_enough_customitem = legendaryRunePlus.color(yml.getString("lang.not-enough-customitem"));
        lang_null_rune_type = legendaryRunePlus.color(yml.getString("lang.null-rune-type"));
        lang_rune_put_fail = legendaryRunePlus.color(yml.getString("lang.rune-put-fail"));
        lang_rune_put_successfully = legendaryRunePlus.color(yml.getString("lang.rune-put-successfully"));
        lang_unlock_slot = legendaryRunePlus.color(yml.getString("lang.unlock-slot"));
        lang_unlock_slot_admin = legendaryRunePlus.color(yml.getString("lang.unlock-slot-admin"));
        lang_lock_slot = legendaryRunePlus.color(yml.getString("lang.lock-slot"));
        lang_lock_slot_admin = legendaryRunePlus.color(yml.getString("lang.lock-slot-admin"));
        lang_rune_unequip_successfully = legendaryRunePlus.color(yml.getString("lang.rune-unequip-successfully"));
        lang_rune_unequip_fail = legendaryRunePlus.color(yml.getString("lang.rune-unequip-fail"));

        sound_put_successfully = getSound(yml.getString("sounds.put-successfully"));
        sound_put_fail = getSound(yml.getString("sounds.put-fail"));
        sound_put_already_has = getSound(yml.getString("sounds.put-already-has"));
        sound_unlock = getSound(yml.getString("sounds.unlock"));
        sound_unequip = getSound(yml.getString("sounds.unequip"));

    }
    public Optional<Sound> getSound(String str) {
        try {
            Sound sound = Sound.valueOf(str.toUpperCase());
            return Optional.of(sound);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
