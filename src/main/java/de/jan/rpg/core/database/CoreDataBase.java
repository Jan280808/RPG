package de.jan.rpg.core.database;

import de.jan.rpg.api.dataBase.DataBase;
import de.jan.rpg.core.Core;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@Getter
public class CoreDataBase implements DataBase {

    private Connection connection;
    private boolean isConnected;

    public CoreDataBase() {
        handelOnlineMode();
    }

    @Override
    public void createTable(@NotNull String tableName, @NotNull String tableValues) {
        executeAsyncUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableValues + ")");
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
        executeAsyncUpdate(query);
    }

    @Override
    public void updateData(@NotNull String tableName, @NotNull String columns, @NotNull Object... values) {

    }

    @Override
    public void updateDataFromUUID(@NotNull String tableName, @NotNull UUID uuid, @NotNull String columns, @NotNull Object... values) {
        if(tableName.isEmpty() || columns.isEmpty() || values.length == 0) throw new IllegalArgumentException("Table name, columns, and values must not be empty");
        String[] columnNames = columns.split(",");
        if(columnNames.length != values.length) throw new IllegalArgumentException("Number of columns must match number of values");
        String placeholders = String.join(" = ?, ", columnNames) + " = ?";
        String query = "UPDATE " + tableName + " SET " + placeholders + " WHERE uuid = ?";
        Object[] allValues = new Object[values.length + 1];
        System.arraycopy(values, 0, allValues, 0, values.length);
        allValues[values.length] = uuid.toString();
        executeAsyncUpdate(query, allValues);
    }

    @Override
    public void removeData(@NotNull String tableName, @NotNull String condition) {
        executeAsyncUpdate("DELETE FROM " + tableName + " WHERE " + condition);
    }

    @Override
    public Object selectData(@NotNull String tableName, @NotNull String sql) {
        return executeQuery("SELECT " + sql + " FROM " + tableName);
    }

    @Override
    public Map<String, Object> selectDataFromUUID(@NotNull String tableName, @NotNull String sql, @NotNull UUID uuid) {
        return executeQueryWithUUID("SELECT " + sql + " FROM " + tableName + " WHERE uuid = ?", uuid);
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

    private Map<String, Object> executeQueryWithUUID(@NotNull String sql, @NotNull UUID uuid) {
        if(!isConnected) return null;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if(!resultSet.next()) {
                Core.LOGGER.info("No data found for UUID: {}", uuid);
                resultSet.close();
                return null;
            }

            Map<String, Object> row = new HashMap<>();
            for(int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                row.put(columnName, columnValue);
            }

            resultSet.close();
            return row;

        } catch (SQLException e) {
            Core.LOGGER.error("SQLException occurred while executing query", e);
            return null;
        }
    }

    private void executeAsyncUpdate(String query, Object... values) {
        if(!isConnected) return;
        try(PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for(int i = 0; i < values.length; i++) preparedStatement.setObject(i + 1, values[i]);
            preparedStatement.executeUpdate();
            Core.LOGGER.info("Query call was successful");
        } catch (SQLException exception) {
            Core.LOGGER.error("Query call has failed", exception);
        }
    }

    private void connect(String host, int port, String database, String user, String password) {
        if(isConnected) return;
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", user, password);
            Core.LOGGER.info("connection to {}:{} was successful", host, port);
            isConnected = true;
        } catch (SQLException exception) {
            isConnected = false;
            activateOfflineMode();
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
                defaultJson.put("offlineMode", false);
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
            boolean offlineMode = jsonObject.getBoolean("offlineMode");

            if(offlineMode) {
                activateOfflineMode();
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

    private void activateOfflineMode() {
        if(Core.offlineMode) return;
        Core.LOGGER.warn("OfflineMode is activated, no interaction with dataBase possible");
        Core.offlineMode = true;
    }
}
