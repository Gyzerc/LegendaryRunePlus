package com.gyzer.Panel.Loader.Button;

public class ButtonFuction {
    private FuctionType type;
    private String id;
    private String value;
    private RuneSettings settings;

    public ButtonFuction(FuctionType type, String id, String value, RuneSettings settings) {
        this.type = type;
        this.id = id;
        this.value = value;
        this.settings = settings;
    }

    public ButtonFuction(FuctionType type,String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public FuctionType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public RuneSettings getRuneSettings() {
        return settings;
    }

    public enum FuctionType {
        NULL,RUNE,CLOSE,CONSOLE,PLAYER,OP;
    }
}
