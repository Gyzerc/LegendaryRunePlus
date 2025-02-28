package com.gyzer.Commands.SubCommands;

import com.gyzer.Data.UserData;
import com.gyzer.Panel.Loader.PanelReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LockCommand extends com.gyzer.Commands.Builder.CommandProvider {
    public LockCommand( ) {
        super("", "lock", 4, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        String pageId = args[2];
        String slotId = args[3];
        if (target != null) {
            PanelReader page = legendaryRunePlus.getPagesManager().getPage(pageId);
            if (page != null) {
                if (page.getRuneSlotIds().contains(slotId)) {
                    sender.sendMessage(configManager.lang_plugin + configManager.lang_lock_slot_admin.replace("%player%",args[1]).replace("%page%",pageId).replace("%slot%",page.getRuneSlotDisplayById(slotId)));
                    UserData data = legendaryRunePlus.getUserDataManager().getUserData(target.getUniqueId());
                    if (data.hasUnlockSlot(pageId,slotId)) {
                        data.Lock(pageId,slotId);
                        target.sendMessage(configManager.lang_plugin + configManager.lang_lock_slot.replace("%page%",pageId).replace("%slot%",page.getRuneSlotDisplayById(slotId)));
                    }
                    return;
                }
                sender.sendMessage(configManager.lang_plugin + configManager.lang_not_has_slot);
                return;
            }



            sender.sendMessage(configManager.lang_plugin + configManager.lang_not_has_page);
            return;
        }
        sender.sendMessage(configManager.lang_plugin+configManager.lang_notonline);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
