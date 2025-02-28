package com.gyzer.Data.Database;

import com.google.gson.Gson;
import com.gyzer.Data.Database.Provider.DataProvider;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import com.gyzer.Utils.ItemUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MySQL extends DataProvider {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private HikariDataSource connectPool;

    public MySQL() {
        initDataBase();
    }

    @Override
    protected void initDataBase() throws RuntimeException {
        setConnection();
        //创建数据库表
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
            createDataTable(connection,PLAYER_DATA);
            createDataTable(connection,ITEM_DATA);
            legendaryRunePlus.msg("&d成功连接 &EMySQL &D数据库.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

    @Override
    public void setConnection() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        ConfigurationSection section = legendaryRunePlus.getConfig().getConfigurationSection("HikariCP");
        if (section != null) {
            hikariConfig.setConnectionTimeout(section.getLong("connectionTimeout"));
            hikariConfig.setMinimumIdle(section.getInt("minimumIdle"));
            hikariConfig.setMaximumPoolSize(section.getInt("maximumPoolSize"));
            section = legendaryRunePlus.getConfig().getConfigurationSection("Mysql");
            if (section != null) {
                String url = "jdbc:mysql://" + section.getString("address") + ":" + section.getString("port") + "/" + section.getString("database") + "?useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai";
                hikariConfig.setJdbcUrl(url);
                hikariConfig.setUsername(section.getString("user"));
                hikariConfig.setPassword(section.getString("password"));
                hikariConfig.setAutoCommit(true);
                connectPool = new HikariDataSource(hikariConfig);
                return;
            }
            legendaryRunePlus.info("config.yml中缺少了 MySQL 配置,请重新生成配置文件进行修改..", Level.SEVERE);
            return;
        }
        legendaryRunePlus.info("config.yml中缺少了 HikariCP 配置,请重新生成配置文件进行修改..", Level.SEVERE);
    }





    @Override
    public void closeDataBase() {
        if (connectPool != null && !connectPool.isClosed()) {
            connectPool.close();
            legendaryRunePlus.msg("&d成功断开 &eMySQL &d数据库连接.");
        }
    }

    @Override
    public Optional<UserData> getUserData(UUID uuid) {
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
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
        } finally {
            close(connection);
        }
        return Optional.empty();
    }

    @Override
    public void saveUserData(UserData data) {
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
            setData(connection,PLAYER_DATA.getBuilder(), data.getUuid().toString(),
                    data.getUuid().toString(),
                    ListMapToString(data.getUnlocks()),
                    toStringAttrs(data.getDatas()),
                    toPutsString(data.getDatas()),
                    toStringTypes(data.getDatas()));
        } catch (SQLException ex) {
            legendaryRunePlus.info("保存玩家数据时出错！ ",Level.SEVERE,ex);
        } finally {
            close(connection);
        }
    }

    @Override
    public List<UUID> getUserDatas() {
        Connection connection = null;
        List<UUID> users = new ArrayList<>();
        try {
            connection = connectPool.getConnection();
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
        } finally {
            close(connection);
        }
        return users;
    }

    @Override
    public Optional<ItemStack> getItem(UUID uuid) {
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
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
        } finally {
            close(connection);
        }
        return Optional.empty();
    }

    @Override
    public void setItem(UUID uuid, ItemStack i) {
        if (i != null) {
            Connection connection = null;
            try {
                connection = connectPool.getConnection();
                setData(connection, ITEM_DATA.getBuilder(), uuid.toString(), uuid.toString(), toItemString(i));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                close(connection);
            }
        }
    }

    @Override
    public void delItem(UUID uuid) {
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
            delData(connection,ITEM_DATA.getBuilder(),uuid.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

    @Override
    public Optional<ConcurrentHashMap<UUID, ItemStack>> getItems() {
        Connection connection = null;
        ConcurrentHashMap<UUID,ItemStack> items = new ConcurrentHashMap<>();
        try {
            connection = connectPool.getConnection();
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
        } finally {
            close(connection);
        }
        return Optional.of(items);
    }
}
