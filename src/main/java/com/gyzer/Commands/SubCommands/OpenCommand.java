package com.gyzer.Commands.SubCommands;

import com.gyzer.Panel.Loader.PanelReader;
import com.gyzer.Panel.Panel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OpenCommand extends com.gyzer.Commands.Builder.CommandProvider {
    public OpenCommand() {
        super("", "open", Arrays.asList(2,3), false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            Player target = null;
            UUID uuid = senderPlayer.getUniqueId();
            String id = args[1];
            PanelReader page = legendaryRunePlus.getPagesManager().getPage(id);
            if (page != null) {
                if (args.length == 3) {
                    if (!senderPlayer.hasPermission("LegendaryRunePlus.view.other")) {
                        sender.sendMessage(configManager.lang_plugin+configManager.lang_permission);
                        return;
                    }
                    target = Bukkit.getPlayerExact(args[2]);
                    if (target != null) {
                        uuid = target.getUniqueId();
                        if (!sender.isOp()) {
                            sender.sendMessage(configManager.lang_plugin + configManager.lang_permission);
                            return;
                        }
                    } else {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
                        if (!offlinePlayer.hasPlayedBefore() || !legendaryRunePlus.getDataProvider().getUserDatas().contains(offlinePlayer.getUniqueId())) {
                            sender.sendMessage(configManager.lang_plugin + configManager.lang_null_player);
                            return;
                        }
                        uuid = offlinePlayer.getUniqueId();
                    }
                }
                if (senderPlayer.hasPermission("LegendaryRunePlus.open.*") || senderPlayer.hasPermission("LegendaryRunePlus.open."+id)) {
                    Panel panel = new Panel((Player) sender, uuid, page);
                    panel.open();
                    return;
                }
                sender.sendMessage(configManager.lang_plugin+configManager.lang_permission);
                return;
            }
            sender.sendMessage(configManager.lang_plugin+configManager.lang_not_has_page);
            return;
        }
        sender.sendMessage(configManager.lang_plugin + "该指令只能由玩家发起.");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(legendaryRunePlus.getPagesManager().getPages(),1, Collections.singletonList("open"),0)
                .addTab(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), 2 , Collections.singletonList("open") , 0)
                .build(args);
    }
}
