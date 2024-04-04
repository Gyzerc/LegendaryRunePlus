package com.gyzer.Commands.Builder;

import com.gyzer.LegendaryRunePlus;
import com.gyzer.Manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class CommandProvider {
    public final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    public final ConfigManager configManager = legendaryRunePlus.getConfigManager();
    private String permission;
    private String command;
    private List<Integer> length;
    private boolean admin;

    public CommandProvider(String permission, String command, int length, boolean admin) {
        this.permission = permission;
        this.command = command;
        this.length = Collections.singletonList(length);
        this.admin = admin;
    }

    public CommandProvider(String permission, String command, List<Integer> length, boolean admin) {
        this.permission = permission;
        this.command = command;
        this.length = length;
        this.admin = admin;
    }

    public abstract void handle(CommandSender sender, String[] args);
    public abstract List<String> complete(CommandSender sender, String[] args);

    public String getPermission() {
        return permission;
    }

    public String getCommand() {
        return command;
    }

    public List<Integer> getLength() {
        return length;
    }

    public boolean isAdmin() {
        return admin;
    }

    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer p = Bukkit.getPlayerExact(name);
        if (p == null) {
            // Not the best option, but Spigot doesn't offer a good replacement (as usual)
            p = Bukkit.getOfflinePlayer(name);
            return p.hasPlayedBefore() ? p : null;
        }
        return p;
    }

    public boolean isOnline(String str) {
        return Bukkit.getPlayerExact(str) != null;
    }

    public boolean isInteger(String str) {
        try {
            int a = Integer.parseInt(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public class CommandTabBuilder {
        private Set<TabList> list;
        public CommandTabBuilder(){
            list = new HashSet<>();
        }

        public CommandTabBuilder addTab(List<String> returnList,int position,List<String> previousArg,int previousPosition){
            list.add(new TabList(returnList,position,previousArg,previousPosition));
            return this;
        }

        public List<String> build(String[] args){
            List<String> returnList=new ArrayList<>();
            int length = args.length;
            if (length > 0) {
                for (TabList tabList : list) {
                    if (tabList.getPosition() == length-1) {
                        if (tabList.getPreviousPosition() >= length) {
                            continue;
                        }
                        if ( tabList.getPreviousArg() == null){
                            continue;
                        }
                        String previousArg = args[tabList.getPreviousPosition()];

                        if (tabList.getPreviousArg().contains(previousArg)){
                            returnList = tabList.getReturnList();
                            break;
                        }
                    }
                }
            }
            return returnList;
        }

        public class TabList {

            private List<String> returnList;
            //此参数出现的位置
            private int position;

            //识别上一个参数
            private List<String> previousArg;
            //上一个参数出现的位置
            private int previousPosition;

            public TabList(List<String> returnList, int position, List<String> previousArg, int previousPosition) {
                this.returnList = returnList;
                this.position = position;
                this.previousArg = previousArg;
                this.previousPosition = previousPosition;
            }

            public List<String> getReturnList() {
                return returnList;
            }

            public int getPosition() {
                return position;
            }

            public List<String> getPreviousArg() {
                return previousArg;
            }

            public int getPreviousPosition() {
                return previousPosition;
            }
        }

    }
}
