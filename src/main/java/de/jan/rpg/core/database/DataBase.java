package de.jan.rpg.core.database;

import de.jan.rpg.core.Core;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class DataBase {

    private Connection connection;
    private boolean isConnected;

    public DataBase() {
        handelOnlineMode();
    }

    public void createTable(String tableName, String tableValues) {
        if(!isConnected) return;
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableValues + ")");
            statement.execute();
            Core.LOGGER.info("create table {} was successful", tableName);
        } catch (SQLException exception) {
            Core.LOGGER.error("create table {} has failed", tableName, exception);
        }
    }

    public void prepareStatement(String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            Core.LOGGER.info("query call was successful");
        } catch (SQLException exception) {
            Core.LOGGER.info("query call has failed", exception);
        }
    }

    public Object getResult(String sql) {
        if(!isConnected) return false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Object result = resultSet.getObject(1);
                resultSet.close();
                statement.close();
                return result;
            }
            resultSet.close();
            statement.close();
            return null;

        } catch (SQLException exception) {
            Core.LOGGER.error("result has failed", exception);
            return null;
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
                Core.LOGGER.info("set onlineMode to offline");
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
