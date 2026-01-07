package com.example.possystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    private static final String URL = "jdbc:sqlite:pos.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDB() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Product list
            stmt.execute("CREATE TABLE IF NOT EXISTS product (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "stock INTEGER NOT NULL," +
                    "status TEXT," +
                    "image_path TEXT" +
                    ")");

            // Customer Record list
            stmt.execute("CREATE TABLE IF NOT EXISTS customer_record (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "customer_name TEXT NOT NULL," +
                    "phone TEXT," +
                    "type TEXT," +
                    "total REAL," +
                    "create_time TEXT" +
                    ")");

            // Order list
            stmt.execute("CREATE TABLE IF NOT EXISTS order_item (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "record_id INTEGER NOT NULL," +
                    "product_name TEXT," +
                    "quantity INTEGER," +
                    "price REAL," +
                    "FOREIGN KEY(record_id) REFERENCES customer_record(id)" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
