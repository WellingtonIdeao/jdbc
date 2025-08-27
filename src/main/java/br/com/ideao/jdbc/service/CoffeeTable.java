package br.com.ideao.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CoffeeTable {
    private Connection connection;

    public CoffeeTable(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS coffee (" +
                "cof_name VARCHAR(32) NOT NULL, " +
                "sup_id INTEGER NOT NULL, " +
                "price NUMERIC(10,2) NOT NULL, " +
                "sales INTEGER NOT NULL, " +
                "total INTEGER NOT NULL, " +
                "PRIMARY KEY (cof_name), " +
                "FOREIGN KEY (sup_id) REFERENCES supplier (sup_id))";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }
}
