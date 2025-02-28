package com.gyzer.Data.Database.Provider;

import com.google.gson.Gson;
import com.gyzer.Data.Rune.RunePageData;
import com.gyzer.Data.UserData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DataProvider {
    protected DataTable PLAYER_DATA = new DataTable("LegendaryRunePlus_PlayerData",new DataProvider.Builder("LegendaryRunePlus_PlayerData")
            .addVarcharKey("uuid",60)
            .addTextKey("unlocks")
            .addTextKey("attrs")
            .addTextKey("items")
            .addTextKey("types")
            .build("uuid"));
    protected DataTable ITEM_DATA = new DataTable("LegendaryRunePlus_ItemsData",new DataProvider.Builder("LegendaryRunePlus_ItemsData")
            .addVarcharKey("uuid",60)
            .addTextKey("item")
            .build("uuid"));

    protected abstract void initDataBase() throws RuntimeException;
    public abstract void setConnection() throws SQLException;
    public abstract void closeDataBase();
    public abstract Optional<UserData> getUserData(UUID uuid);
    public abstract void saveUserData(UserData data);
    public abstract List<UUID> getUserDatas();
    public abstract Optional<ItemStack> getItem(UUID uuid);
    public abstract void setItem(UUID uuid,ItemStack i);
    public abstract void delItem(UUID uuid);
    public abstract Optional<ConcurrentHashMap<UUID,ItemStack>> getItems();
    public void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void createDataTable(Connection connection, DataTable table) {
        if (connection != null) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate(table.getBuilder().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected Optional<ResultSet> getDataStringResult(Connection connection, DataProvider.Builder builder, String target) {
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement("SELECT * FROM " + builder.getTableName() + " WHERE `" + builder.getMainKey() + "` = '" + target + "' LIMIT 1;");
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    return Optional.of(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    protected Optional<ResultSet> getDataStrings(Connection connection, DataProvider.Builder builder) {
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.prepareStatement("SELECT * FROM "+builder.getTableName()+";");
                rs = statement.executeQuery();
                return Optional.of(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
    protected void delData(Connection connection, DataProvider.Builder builder, String target) {
        if (connection != null) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("DELETE FROM `"+builder.getTableName()+"` WHERE `"+builder.getMainKey()+"` = ?");
                statement.setString(1, target);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected <T> void setData(Connection connection, DataProvider.Builder builder, String target, T... ts) {
        if (connection != null) {
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(builder.getInsertString(target));
                int a = 1;
                for (T t : ts) {
                    ps.setObject(a, t);
                    a++;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public String toStringTypes(HashMap<String, RunePageData> datas) {
        StringBuilder builder = new StringBuilder();
        if (datas  != null && !datas.isEmpty()) {
            for (Map.Entry<String,RunePageData> entry:datas.entrySet()) {
                HashMap<String,String> types = entry.getValue().getTypes();
                if (types == null || types.isEmpty()) {
                    continue;
                }
                builder.append(entry.getKey()).append("⁝").append(StringMapToString(entry.getValue().getTypes())).append(";");
            }
        }
        return builder.toString();
    }
    public String toStringAttrs(HashMap<String, RunePageData> datas) {
        StringBuilder builder = new StringBuilder();
        if (datas  != null && !datas.isEmpty()) {
            for (Map.Entry<String, RunePageData> entry : datas.entrySet()) {
                HashMap<String, List<String>> attr = entry.getValue().getAttrs();
                if (attr == null || attr.isEmpty()) {
                    continue;
                }
                builder.append(entry.getKey()).append("⁝").append(ListMapToString(entry.getValue().getAttrs())).append(";");
            }
        }
        return builder.toString();
    }


    public HashMap<String,RunePageData> toRuneData(String attrs,String put,String types) {
        HashMap<String,RunePageData> datas = new HashMap<>();
        if (attrs != null && !attrs.isEmpty()) {
            for (String arg : attrs.split(";")) {
                String[] args = arg.split("⁝");
                if (args.length == 2) {
                    String pageName = args[0];
                    HashMap<String,List<String>> attrMap = toHashMap(args[1]);
                    RunePageData runePageData = datas.getOrDefault(pageName,new RunePageData(pageName,new HashMap<>(),new HashMap<>(), new HashMap<>()));
                    runePageData.setAttrs(attrMap);
                    datas.put(pageName,runePageData);
                }
            }
        }
        if (put != null && !put.isEmpty()) {
            for (String arg : put.split(";")) {
                String[] args = arg.split("⁝");
                if (args.length == 2) {
                    String pageName = args[0];
                    HashMap<String,UUID> itemMap = toItemMap(args[1]);
                    RunePageData runePageData = datas.getOrDefault(pageName,new RunePageData(pageName,new HashMap<>(),new HashMap<>() , new HashMap<>()));
                    runePageData.setPut(itemMap);
                    datas.put(pageName,runePageData);
                }
            }
        }
        if (types != null && !types.isEmpty()) {
            for (String arg : types.split(";")) {
                String[] args = arg.split("⁝");
                if (args.length == 2) {
                    String pageName = args[0];
                    HashMap<String,String> itemMap = toStringHashMap(args[1]);
                    RunePageData runePageData = datas.getOrDefault(pageName,new RunePageData(pageName,new HashMap<>(),new HashMap<>() , new HashMap<>()));
                    runePageData.setTypes(itemMap);
                    datas.put(pageName,runePageData);
                }
            }
        }
        return datas;
    }






    public String toPutsString(HashMap<String,RunePageData> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String,RunePageData> entry : data.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().isEmpty()
            && entry.getValue() != null) {
                builder.append(entry.getKey()).append("⁝");
                HashMap<String, UUID> put = entry.getValue().getPut();
                put.forEach((s, itemStack) -> {
                    builder.append(s).append(":").append(itemStack.toString()).append(",");
                });
                builder.append(";");
            }
        }
        return builder.toString();
    }


    protected String toItemString(ItemStack i) {
        try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream=new BukkitObjectOutputStream(byteArrayOutputStream)){
            bukkitObjectOutputStream.writeObject(i);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String ListMapToString(HashMap<String,List<String>> attrs) {
        try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream=new BukkitObjectOutputStream(byteArrayOutputStream)){
            bukkitObjectOutputStream.writeObject(attrs);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String StringMapToString(HashMap<String,String> attrs) {
        try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream=new BukkitObjectOutputStream(byteArrayOutputStream)){
            bukkitObjectOutputStream.writeObject(attrs);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public HashMap<String,List<String>> toHashMap(String arg)
    {
        byte[] bytes=Base64.getDecoder().decode(arg);
        try (ByteArrayInputStream byteArrayOutputStream=new ByteArrayInputStream(bytes);
             BukkitObjectInputStream bukkitObjectOutputStream=new BukkitObjectInputStream(byteArrayOutputStream)){
            return (HashMap<String,List<String>>) bukkitObjectOutputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }
    public HashMap<String,String> toStringHashMap(String arg)
    {
        byte[] bytes=Base64.getDecoder().decode(arg);
        try (ByteArrayInputStream byteArrayOutputStream=new ByteArrayInputStream(bytes);
             BukkitObjectInputStream bukkitObjectOutputStream=new BukkitObjectInputStream(byteArrayOutputStream)){
            return (HashMap<String,String>) bukkitObjectOutputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }
    private HashMap<String,UUID> toItemMap(String str) {
        HashMap<String,UUID> map = new HashMap<>();
        if (str != null && !str.isEmpty()) {
            String[] args = str.split(",");
            if (args.length > 0) {
                for (String child : args) {
                    String[] values = child.split(":");
                    if (values.length == 2) {
                        map.put(values[0],UUID.fromString(values[1]));
                    }
                }
            }
        }
        return map;
    }
    protected ItemStack toItem(String arg)
    {
        byte[] bytes=Base64.getDecoder().decode(arg);
        try (ByteArrayInputStream byteArrayOutputStream=new ByteArrayInputStream(bytes);
             BukkitObjectInputStream bukkitObjectOutputStream=new BukkitObjectInputStream(byteArrayOutputStream)){
            return (ItemStack) bukkitObjectOutputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




    public static class Builder {
        private String tableName;
        private String mainKey;
        private StringBuilder stringBuilder;
        private List<String> keys;

        public Builder(String tableName) {
            this.keys = new ArrayList<>();
            this.tableName = tableName;
            stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (");
        }

        public Builder addTextKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" TEXT DEFAULT NULL");
            keys.add(keyName);
            return this;
        }

        public Builder addUUIDKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" UUID DEFAULT NULL");
            keys.add(keyName);
            return this;
        }

        public Builder addBlobKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" BLOB DEFAULT NULL");
            keys.add(keyName);
            return this;
        }

        public Builder addIntegerKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` INTEGER NOT NULL");
            keys.add(keyName);
            return this;
        }

        public Builder addDoubleKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` DOUBLE NOT NULL");
            keys.add(keyName);
            return this;
        }
        public Builder addLongKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` LONG NOT NULL");
            keys.add(keyName);
            return this;
        }
        public Builder addVarcharKey(String keyName,int length){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` varchar("+length+") NOT NULL");
            keys.add(keyName);
            return this;
        }
        public Builder addBooleanKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` BOOLEAN NOT NULL");
            keys.add(keyName);
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

        public String getInsertString(String target) { //`
            StringBuilder main = new StringBuilder("REPLACE INTO "+tableName+" ");
            StringBuilder keys = new StringBuilder("(");
            StringBuilder keys_unknow = new StringBuilder("(");
            for (int i =0 ; i < this.keys.size() ; i ++) {
                keys.append("`").append(this.keys.get(i)).append("`");
                keys_unknow.append("?");
                if (i == this.keys.size() - 1 ) {
                    keys.append(")");
                    keys_unknow.append(")");
                    break;
                } else {
                    keys.append(",");
                    keys_unknow.append(",");
                }
            }
            main.append(keys).append(" VALUES ").append(keys_unknow);
            return main.toString();
        }

    }

    public static class DataTable {

        private String tableName;
        private DataProvider.Builder builder;

        public DataTable(String tableName, DataProvider.Builder builder) {
            this.tableName = tableName;
            this.builder = builder;
        }

        public String getTableName() {
            return tableName;
        }

        public DataProvider.Builder getBuilder() {
            return builder;
        }
    }
}
