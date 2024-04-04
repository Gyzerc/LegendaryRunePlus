package com.gyzer.Data.Database.Provider;

public class Builder {
    private String tableName;
    private String mainKey;
    private StringBuilder stringBuilder;

    public Builder(String tableName) {
        this.tableName = tableName;
        stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (");
    }

    public Builder addTextKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("").append(keyName).append(" TEXT DEFAULT NULL");
        return this;
    }

    public Builder addUUIDKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("").append(keyName).append(" UUID DEFAULT NULL");
        return this;
    }

    public Builder addBlobKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("").append(keyName).append(" BLOB DEFAULT NULL");
        return this;
    }

    public Builder addIntegerKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("`").append(keyName).append("` INTEGER NOT NULL");
        return this;
    }

    public Builder addDoubleKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("`").append(keyName).append("` DOUBLE NOT NULL");
        return this;
    }
    public Builder addLongKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("`").append(keyName).append("` LONG NOT NULL");
        return this;
    }
    public Builder addVarcharKey(String keyName,int length){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("`").append(keyName).append("` varchar("+length+") NOT NULL");
        return this;
    }
    public Builder addBooleanKey(String keyName){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        stringBuilder.append("`").append(keyName).append("` BOOLEAN NOT NULL");
        return this;
    }
    public Builder build(String mainKey){
        if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
            stringBuilder.append(",");
        }
        this.mainKey = mainKey;
        stringBuilder.append("PRIMARY KEY (`"+mainKey+"`));");
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public String getMainKey() {
        return mainKey;
    }

    @Override
    public String toString(){
        return stringBuilder.toString();
    }


}
