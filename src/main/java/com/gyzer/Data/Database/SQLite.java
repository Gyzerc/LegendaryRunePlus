package com.gyzer.Data.Database;

import com.gyzer.Data.Database.Provider.DataProvider;
import com.gyzer.Data.Database.Provider.DataTable;
import com.gyzer.Data.UserData;
import com.gyzer.LegendaryRunePlus;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLite extends DataProvider {
    private final LegendaryRunePlus legendaryRunePlus = LegendaryRunePlus.getLegendaryRunePlus();
    private File folder;

    public SQLite() {
        initDataBase();
    }

    @Override
    protected void initDataBase() throws RuntimeException {
        folder = new File(legendaryRunePlus.getDataFolder(),  "data.db");
        if (!folder.exists()){
            try {
                legendaryRunePlus.msg("&d成功创建本地 &ESQLite &d数据库.");
                folder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        createTable(DataTable.Player_Data);
    }
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + folder);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<UserData> getUserData(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
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
            legendaryRunePlus.info("获取玩家数据时出错！ ", Level.SEVERE,ex);
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
            connection = getConnection();
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
            connection = getConnection();
            statement = connection.prepareStatement("SELECT `uuid` FROM "+ DataTable.Player_Data.getName()+";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uid = resultSet.getString("uuid");
                users.add(UUID.fromString(uid));
            }
            close(connection);
        }
        catch (SQLException e) {
            legendaryRunePlus.info("获取所有用户失败！",Level.SEVERE,e);
        }
        return users;
    }

    @Override
    public void createTable(DataTable table) {
        Connection connection = null;
        Statement stat = null;
        if (!isExist(table)){
            try {
                connection = getConnection();
                stat = connection.createStatement();
                stat.executeUpdate(table.getBuilder().toString());
                close(connection);
            } catch (SQLException e) {
                legendaryRunePlus.info("创建 SQLite 数据表失败",Level.SEVERE,e);
                return;
            }
        }
        legendaryRunePlus.msg("&d成功连接 &eSQLite &d数据库.");
    }

    @Override
    public boolean isExist(DataTable table) {
        if (!folder.exists()){
            return false;
        }
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT `"+table.getBuilder().getMainKey()+"` FROM `"+table.getName()+"` LIMIT 1;");
            statement.executeQuery();
            close(connection);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    @Override
    public void setConnection() throws SQLException {

    }

    @Override
    public void closeDataBase() {
    }

}
