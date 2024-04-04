package com.gyzer.Data.Database;

import com.google.gson.Gson;
import com.gyzer.Data.Database.Provider.DataProvider;
import com.gyzer.Data.Database.Provider.DataTable;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class MySQL extends DataProvider {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private HikariDataSource connectPool;

    public MySQL() {
        initDataBase();
    }

    @Override
    protected void initDataBase() throws RuntimeException {
        try {
            setConnection();
            createTable(DataTable.Player_Data);
            legendaryRunePlus.msg("&d成功连接 &EMySQL &D数据库.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setConnection() throws SQLException {
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
    public void createTable(DataTable table) {
        Connection connection = null;
        Statement stat = null;
        if (!isExist(table)){
            try {
                connection = connectPool.getConnection();
                stat = connection.createStatement();
                stat.executeUpdate(table.getBuilder().toString());
                close(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }
    @Override
    public boolean isExist(DataTable table) {
        if (connectPool == null){
            return false;
        }
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT `"+table.getBuilder().getMainKey()+"` FROM `"+table.getName()+"` LIMIT 1;");
            statement.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            close(connection);
        }
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
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = connectPool.getConnection();
            ps = connection.prepareStatement("SELECT * FROM "+DataTable.Player_Data.getName()+" WHERE uuid = '" + uuid.toString() + "' LIMIT 1;");
            rs = ps.executeQuery();
            while (rs.next()) {
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
        PreparedStatement ps = null;
        Connection connection = null;
        try {
            connection = connectPool.getConnection();
            ps = connection.prepareStatement("REPLACE INTO "+ DataTable.Player_Data.getName()+" (uuid,unlocks,attrs,items,types) VALUES(?,?,?,?,?)");
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, ListMapToString(data.getUnlocks()));
            ps.setString(3, toStringAttrs(data.getDatas()));
            ps.setString(4, toPutsString(data.getDatas()));
            ps.setString(5, toStringTypes(data.getDatas()));
            ps.executeUpdate();
            close(connection);
        } catch (SQLException ex) {
            legendaryRunePlus.info("保存玩家数据时出错！ ",Level.SEVERE,ex);
        }
    }

    @Override
    public List<UUID> getUserDatas() {
        Connection connection = null;
        PreparedStatement statement = null;
        List<UUID> users = new ArrayList<>();
        try {
            connection = connectPool.getConnection();
            statement = connection.prepareStatement("SELECT `uuid` FROM "+ DataTable.Player_Data.getName()+";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uid = resultSet.getString("uuid");
                users.add(UUID.fromString(uid));
            }
        }
        catch (SQLException e) {
            legendaryRunePlus.info("获取所有用户失败！",Level.SEVERE,e);
        } finally {
            close(connection);
        }
        return users;
    }

}
