package com.gyzer.Data.Database;

import com.gyzer.Data.Database.Provider.DataProvider;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import org.bukkit.inventory.ItemStack;
import org.serverct.ersha.jd.R;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SQLite extends DataProvider {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private File folder;
    private Connection connection;

    public SQLite() {
        setConnection();
        initDataBase();
    }

    @Override
    protected void initDataBase() throws RuntimeException {
        createDataTable(getConnection(),PLAYER_DATA);
        createDataTable(getConnection(),ITEM_DATA);
    }
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Optional<UserData> getUserData(UUID uuid) {
        Connection connection = getConnection();
        try {
            Optional<ResultSet> optionalResultSet = getDataStringResult(connection,PLAYER_DATA.getBuilder(), uuid.toString());
            if (optionalResultSet.isPresent()) {
                ResultSet rs = optionalResultSet.get();
                String unlockStr = rs.getString("unlocks");
                HashMap<String,List<String>> unlocks = toHashMap(unlockStr);
                String attr = rs.getString("attrs");
                String put = rs.getString("items");
                String types = rs.getString("types");
                return Optional.of(new UserData(uuid,unlocks,toRuneData(attr,put,types)));
            }
        } catch (SQLException ex) {
            legendaryRunePlus.info("获取玩家数据时出错！ ",Level.SEVERE,ex);
        }
        return Optional.empty();
    }

    @Override
    public void saveUserData(UserData data) {
        setData(getConnection(),PLAYER_DATA.getBuilder(), data.getUuid().toString(),
                data.getUuid().toString(),
                ListMapToString(data.getUnlocks()),
                toStringAttrs(data.getDatas()),
                toPutsString(data.getDatas()),
                toStringTypes(data.getDatas()));
    }

    @Override
    public List<UUID> getUserDatas() {
        Connection connection = getConnection();
        List<UUID> users = new ArrayList<>();
        try {
            Optional<ResultSet> optionalResultSet = getDataStrings(connection,PLAYER_DATA.getBuilder());
            if (optionalResultSet.isPresent()) {
                ResultSet resultSet = optionalResultSet.get();
                while (resultSet.next()) {
                    String uid = resultSet.getString("uuid");
                    users.add(UUID.fromString(uid));
                }
            }
        }
        catch (SQLException e) {
            legendaryRunePlus.info("获取所有用户失败！",Level.SEVERE,e);
        }
        return users;
    }

    @Override
    public Optional<ItemStack> getItem(UUID uuid) {
        Connection connection = getConnection();
        try {
            Optional<ResultSet> optionalResultSet = getDataStringResult(connection,ITEM_DATA.getBuilder(),uuid.toString());
            if (optionalResultSet.isPresent()) {
                ResultSet resultSet = optionalResultSet.get();
                String itemStr = resultSet.getString("item");
                if (itemStr != null && !itemStr.isEmpty()) {
                    ItemStack i = toItem(itemStr);
                    if (i != null) {
                        return Optional.of(i);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void setItem(UUID uuid, ItemStack i) {
        setData(getConnection(), ITEM_DATA.getBuilder(), uuid.toString(), uuid.toString(), toItemString(i));
    }

    @Override
    public void delItem(UUID uuid) {
        delData(getConnection(),ITEM_DATA.getBuilder(),uuid.toString());
    }

    @Override
    public Optional<ConcurrentHashMap<UUID, ItemStack>> getItems() {
        Connection connection = getConnection();
        ConcurrentHashMap<UUID,ItemStack> items = new ConcurrentHashMap<>();
        try {
            Optional<ResultSet> optionalResultSet = getDataStrings(connection,ITEM_DATA.getBuilder());
            if (optionalResultSet.isPresent()) {
                ResultSet rs = optionalResultSet.get();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String str = rs.getString("item");
                    if (str != null && !str.isEmpty()) {
                        ItemStack i = toItem(str);
                        if (i != null) {
                            items.put(uuid,i);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(items);
    }


    @Override
    public void setConnection() {
        folder = new File(legendaryRunePlus.getDataFolder(),  "data.db");
        if (!folder.exists()){
            try {
                legendaryRunePlus.msg("&d成功创建本地 &ESQLite &d数据库.");
                folder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + folder);
            legendaryRunePlus.msg("&d成功连接 &eSQLite &d数据库.");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void closeDataBase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                legendaryRunePlus.msg("&d成功断开 &eSQLite &d数据库连接.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
