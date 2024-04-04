package com.gyzer.Commands.SubCommands;

import com.gyzer.Data.Rune.Rune;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand extends com.gyzer.Commands.Builder.CommandProvider {
    public GiveCommand( ) {
        super("", "give", 5, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String playerName = args[1];
        String id = args[2];
        String levelStr = args[3];
        String amountStr = args[4];
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null) {
            if (isInteger(levelStr) && isInteger(amountStr)) {
                Rune rune = legendaryRunePlus.getRunesManager().getRune(id).orElse(null);
                if (rune != null) {
                    int level = Integer.parseInt(levelStr);
                    if (level > rune.getLevel()) {
                        sender.sendMessage(configManager.lang_plugin+configManager.lang_not_has_rune_level.replace("%level",String.valueOf(rune.getLevel())));
                        return;
                    }
                    sender.sendMessage(configManager.lang_plugin + configManager.lang_give_rune.replace("%player%",playerName).replace("%display%",rune.getDisplay().replace("%level%",levelStr)).replace("%amount%",amountStr));
                    p.sendMessage(configManager.lang_plugin + configManager.lang_received_rune.replace("%display%",rune.getDisplay().replace("%level%",levelStr)).replace("%amount%",amountStr));
                    for (int a = 0 ; a < Integer.parseInt(amountStr) ; a++){
                        p.getInventory().addItem(rune.generate(Integer.parseInt(levelStr)));
                    }
                    return;
                }
                sender.sendMessage(configManager.lang_plugin + configManager.lang_not_has_rune);
                return;
            }
            sender.sendMessage(configManager.lang_plugin + configManager.lang_notinteger);
            return;
        }
        sender.sendMessage(configManager.lang_plugin + configManager.lang_notonline);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), 1 , Arrays.asList("give") , 0)
                .addTab(legendaryRunePlus.getRunesManager().getRuneIds(),2,Arrays.asList("give"),0)
                .addTab(Arrays.asList("等级"),3,Arrays.asList("give"),0)
                .addTab(Arrays.asList("数量"),4,Arrays.asList("give"),0)
                .build(args);
    }
}
