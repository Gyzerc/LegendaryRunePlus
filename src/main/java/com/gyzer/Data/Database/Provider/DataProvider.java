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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public abstract class DataProvider {
    protected abstract void initDataBase() throws RuntimeException;
    public abstract void setConnection() throws SQLException;
    public abstract void closeDataBase();
    public abstract Optional<UserData> getUserData(UUID uuid);
    public abstract void saveUserData(UserData data);
    public abstract List<UUID> getUserDatas();
    public abstract void createTable(DataTable table);
    public abstract boolean isExist(DataTable table);
    public void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                    HashMap<String,ItemStack> itemMap = toItemMap(args[1]);
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
                HashMap<String, ItemStack> put = entry.getValue().getPut();
                put.forEach((s, itemStack) -> {
                    builder.append(s).append(":").append(toItemString(itemStack)).append(",");
                });
                builder.append(";");
            }
        }
        return builder.toString();
    }


    private String toItemString(ItemStack i) {
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
    private HashMap<String,ItemStack> toItemMap(String str) {
        HashMap<String,ItemStack> map = new HashMap<>();
        if (str != null && !str.isEmpty()) {
            String[] args = str.split(",");
            if (args.length > 0) {
                for (String child : args) {
                    String[] values = child.split(":");
                    if (values.length == 2) {
                        map.put(values[0],toItem(values[1]));
                    }
                }
            }
        }
        return map;
    }
    private ItemStack toItem(String arg)
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
}
