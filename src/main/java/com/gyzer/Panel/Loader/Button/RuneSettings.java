package com.gyzer.Panel.Loader.Button;

import org.bukkit.Material;

import java.util.List;

public class RuneSettings {
    private String slotId;
    private String type;
    private List<Integer> level;
    private boolean locked;
    private ButtonCost unlockRequire;

    private Material locked_materil;
    private int locked_data;
    private int locked_model;

    private boolean unEquip;
    private ButtonCost unEquipRequire;

    public RuneSettings(String slotId,String type, List<Integer> level, boolean locked, ButtonCost unlockRequire, Material locked_materil, int locked_data, int locked_model, boolean unEquip, ButtonCost unEquipRequire) {
        this.slotId = slotId;
        this.type = type;
        this.level = level;
        this.locked = locked;
        this.unlockRequire = unlockRequire;
        this.locked_materil = locked_materil;
        this.locked_data = locked_data;
        this.locked_model = locked_model;
        this.unEquip = unEquip;
        this.unEquipRequire = unEquipRequire;
    }

    public String getType() {
        return type;
    }

    public boolean isLocked() {
        return locked;
    }

    public List<Integer> getLevel() {
        return level;
    }

    public boolean isUnEquip() {
        return unEquip;
    }

    public String getSlotId() {
        return slotId;
    }

    public ButtonCost getUnlockRequire() {
        return unlockRequire;
    }

    public ButtonCost getUnEquipRequire() {
        return unEquipRequire;
    }

    public Material getLocked_materil() {
        return locked_materil;
    }

    public int getLocked_data() {
        return locked_data;
    }

    public int getLocked_model() {
        return locked_model;
    }
}
