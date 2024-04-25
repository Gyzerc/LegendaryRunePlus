package com.gyzer.Commands;

import com.gyzer.Commands.Builder.CommandProvider;
import com.gyzer.Commands.SubCommands.*;
import com.gyzer.LegendaryRunePlus;
import com.gyzer.Manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class LegendaryCommands implements CommandExecutor, TabExecutor {

    public LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    public ConfigManager configManager = legendaryRunePlus.getConfigManager();
    private static HashMap<String, CommandProvider> commands;


    public LegendaryCommands() {
        Bukkit.getPluginCommand("LegendaryRunePlus").setExecutor(this);
        Bukkit.getPluginCommand("LegendaryRunePlus").setTabCompleter(this);
        commands = new HashMap<>();
        register();
    }

    private void register() {
        commands.put("open",new OpenCommand());
        commands.put("give",new GiveCommand());
        commands.put("unlock",new UnlockCommand());
        commands.put("lock",new LockCommand());
        commands.put("reload",new ReloadCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings)
    {
        int length = strings.length;
        if (length == 0){
            //发送指令提示
            List<String> help = Arrays.asList(
                    "&3&lLegendary&b&lRunePlus &f- &a指令帮助",
                    "&f指令名称: &f[ &2lrune,rune,runes,lr,lrp &f]",
                    "&f子命令:",
                    "&f &7- &8'' open 铭文页 ''  &f&l→ &7打开指定铭文页",
                    "&f &7- &8'' open 玩家 铭文页 ''  &f&l→ &7打开指定玩家的铭文页",
                    "&f &7- &8'' give 玩家 铭文ID 铭文等级 数量''  &f&l→ &7给与铭文",
                    "&f &7- &8'' unlock 玩家 铭文页 铭文槽位ID''  &f&l→ &7解锁玩家铭文槽位",
                    "&f &7- &8'' lock 玩家 铭文页 铭文槽位ID''  &f&l→ &7上锁玩家铭文槽位",
                    "&f &7- &8'' reload ''  &f&l→ &7重载配置文件"
            );
            if (sender.hasPermission("LegendaryRunePlus.help")) {
                legendaryRunePlus.color(help).forEach(sender::sendMessage);
            }
        }
        else {
            String subCommandName = strings[0];
            HashMap<String,CommandProvider> map=commands;
            CommandProvider cmd = map.get(subCommandName);
            if (cmd == null){
                sender.sendMessage(configManager.lang_plugin+configManager.lang_unknown_command);
                return false;
            }
            if ( cmd.getPermission().isEmpty() || sender.hasPermission(cmd.getPermission()) ) {
                if (cmd.getLength().contains(length)) {
                    cmd.handle(sender, strings);
                    return true;
                }
            }
            else {
                sender.sendMessage(configManager.lang_plugin + configManager.lang_permission);
                return true;
            }
            sender.sendMessage(configManager.lang_plugin+configManager.lang_unknown_command);
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        int length = strings.length;
        List<String> tab=new ArrayList<>();
        if (length == 1 ){
            for (Map.Entry<String,CommandProvider> entry:commands.entrySet()){
                CommandProvider legendaryCommand=entry.getValue();
                if ((legendaryCommand.isAdmin() && commandSender.isOp()) || (commandSender.hasPermission(legendaryCommand.getPermission()))){
                    tab.add(entry.getKey());
                }
            }
            return tab;
        }
        else {
            String subCommand = strings[0];
            HashMap<String,CommandProvider> map=commands;
            CommandProvider legendaryCommand = map.get(subCommand);
            if (legendaryCommand != null){
                if ((legendaryCommand.isAdmin() && commandSender.isOp()) || (commandSender.hasPermission(legendaryCommand.getPermission()))){
                    return legendaryCommand.complete(commandSender,strings);
                }
            }
        }
        return null;
    }
}
