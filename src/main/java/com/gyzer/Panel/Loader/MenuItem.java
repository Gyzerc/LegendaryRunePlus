package com.gyzer.Panel.Loader;

import com.gyzer.Panel.Loader.Button.ButtonFuction;
import org.bukkit.inventory.ItemStack;

public class MenuItem {
    private String id;
    private ItemStack i;
    private ButtonFuction fuction;

    public MenuItem(String id, ItemStack i, ButtonFuction fuction) {
        this.id = id;
        this.i = i;
        this.fuction = fuction;
    }

    public void setItem(ItemStack i) {
        this.i = i;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItem() {
        return i;
    }

    public ButtonFuction getFuction() {
        return fuction;
    }
}
