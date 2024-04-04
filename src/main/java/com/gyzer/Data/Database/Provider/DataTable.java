package com.gyzer.Data.Database.Provider;

public enum DataTable {

    Player_Data("LegendaryRunePlus_PlayerData",new Builder("LegendaryRunePlus_PlayerData")
            .addVarcharKey("uuid",60)
            .addTextKey("unlocks")
            .addTextKey("attrs")
            .addTextKey("items")
            .addTextKey("types")
            .build("uuid"));

    private String name;
    private Builder builder;
    DataTable(String name,Builder builder){
        this.name = name;
        this.builder = builder;
    }
    public String getName() {
        return name;
    }

    public Builder getBuilder() {
        return builder;
    }
}
