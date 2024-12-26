package de.jan.rpg.core.database;

import de.jan.rpg.api.dataBase.DataBase;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.player.CorePlayerManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class CoreDataBase implements DataBase {

    private Connection connection;
    private boolean isConnected;

    public CoreDataBase() {
        handelOnlineMode();
        createTable("corePlayer", "uuid VARCHAR(100), language VARCHAR(100), firstJoin VARCHAR(100), totalJoin INT");
    }

    @Override
    public void createTable(@NotNull String tableName, @NotNull String tableValues) {
        executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableValues + ")");
    }

    @Override
    public void insertData(@NotNull String tableName, @NotNull String columns, @NotNull Object... values) {
        StringBuilder formattedValues = new StringBuilder();
        for(Object value : values) {
            if(value instanceof Number || value instanceof Boolean)formattedValues.append(value).append(", ");
            else formattedValues.append("'").append(value.toString().replace("'", "''")).append("', ");
        }

        if(formattedValues.length() > 0) formattedValues.setLength(formattedValues.length() - 2);
        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + formattedValues + ")";
        executeUpdate(query);
    }

    @Override
    public void updateData(@NotNull String tableName, @NotNull String setClause, @NotNull String condition) {
        executeUpdate("UPDATE " + tableName + " SET " + setClause + " WHERE " + condition);
    }

    @Override
    public void removeData(@NotNull String tableName, @NotNull String condition) {
        executeUpdate("DELETE FROM " + tableName + " WHERE " + condition);
    }

    @Override
    public Object selectData(@NotNull String tableName, @NotNull String sql) {
        return executeQuery("SELECT " + sql + " FROM " + tableName);
    }

    private Object executeQuery(@NotNull String sql) {
        if(!isConnected) return null;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if(!resultSet.next()) {
                resultSet.close();
                statement.close();
                return null;
            }

            if(columnCount == 1 && resultSet.isLast()) {
                Object singleValue = resultSet.getObject(1);
                resultSet.close();
                statement.close();
                return singleValue;
            }

            List<Map<String, Object>> results = new ArrayList<>();
            do {
                Map<String, Object> row = new HashMap<>();
                for(int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    row.put(columnName, columnValue);
                }
                results.add(row);
            } while (resultSet.next());

            resultSet.close();
            statement.close();
            return results;

        } catch (SQLException exception) {
            Core.LOGGER.error("Error while executing query", exception);
            return null;
        }
    }

    //refresh dataBase with all corePlayer (only call when server stop)
    public void refresh(CorePlayerManager corePlayerManager) {
        if(!isConnected) return;
        StringBuilder updateQuery = new StringBuilder("UPDATE corePlayer SET ");
        corePlayerManager.getPlayerMap().forEach((uuid, corePlayer) -> updateQuery.append("totalJoin = ").append(corePlayer.getTotalJoin()).append(", coins = ").append(corePlayer.getCoins()).append(" WHERE uuid = '").append(corePlayer.getUUID()).append("'; "));
        executeUpdate(updateQuery.toString());
    }

    private void executeUpdate(String query) {
        if(!isConnected) return;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            Core.LOGGER.info("query call was successful");
        } catch (SQLException exception) {
            Core.LOGGER.info("query call has failed", exception);
        }
    }

    private void connect(String host, int port, String database, String user, String password) {
        if(isConnected) return;

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            Core.LOGGER.info("connection to {}:{} was successful", host, port);
            isConnected = true;
        } catch (SQLException exception) {
            isConnected = false;
            Core.LOGGER.error("connection has failed", exception);
        }
    }

    public void disconnect() {
        if(!isConnected) return;
        try {
            connection.close();
            isConnected = false;
            Core.LOGGER.info("connection closed");
        } catch (SQLException exception) {
            Core.LOGGER.info("closing connection has failed", exception);
        }
    }

    private void handelOnlineMode() {
        String filePath = "./plugins/core/database.json";
        try {
            File file = new File(filePath);
            if(!file.exists()) {
                JSONObject defaultJson = new JSONObject();
                defaultJson.put("onlineMode", false);
                JSONObject login = new JSONObject();
                login.put("host", "null");
                login.put("port", 25565);
                login.put("database", "null");
                login.put("user", "null");
                login.put("password", "null");
                defaultJson.put("login", login);

                file.getParentFile().mkdirs();

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(defaultJson.toString(4));
                } catch (Exception exception) {
                    Core.LOGGER.error("could not create database.json", exception);
                }
            }

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            boolean onlineMode = jsonObject.getBoolean("onlineMode");

            if(!onlineMode) {
                Core.LOGGER.info("activate onlineMode, no interaction with dataBase possible");
                Core.offlineMode = true;
                return;
            }

            JSONObject login = jsonObject.getJSONObject("login");
            String host = login.getString("host");
            String password = login.getString("password");
            String database = login.getString("database");
            String user = login.getString("user");
            int port = login.getInt("port");

            connect(host, port, database, user, password);
        } catch (Exception exception) {
            Core.LOGGER.error("could not load database.json", exception);
        }
    }
}
