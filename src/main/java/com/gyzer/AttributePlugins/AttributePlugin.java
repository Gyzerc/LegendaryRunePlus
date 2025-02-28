package com.gyzer.AttributePlugins;

public enum AttributePlugin {
    AP2("AttributePlus"),AP3("AttributePlus"),SX2("SX-Attribute"),SX3("SX-Attribute"),MYTHICLIB("MythicLib");

    private String plugin;

    AttributePlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getPlugin() {
        return plugin;
    }

    public static AttributePlugin getPlugin(String str) {
        try {
            return AttributePlugin.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AP3;
        }
    }
}
