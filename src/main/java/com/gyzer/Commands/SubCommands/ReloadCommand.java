package com.gyzer.Commands.SubCommands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends com.gyzer.Commands.Builder.CommandProvider {
    public ReloadCommand( ) {
        super("", "reload", 1, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        legendaryRunePlus.reload();
        sender.sendMessage(configManager.lang_plugin + "成功重载插件配置文件.");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
