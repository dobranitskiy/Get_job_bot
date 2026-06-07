package com.homework.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:vacancies.db";
    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection get() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
    }
}
