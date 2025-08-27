package br.com.ideao.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SupplierTable {
    private Connection connection;

    public SupplierTable(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS supplier (" +
                "sup_id INTEGER NOT NULL," +
                "sup_name VARCHAR(40) NOT NULL," +
                "street VARCHAR(40) NOT NULL," +
                "city VARCHAR(20) NOT NULL," +
                "state CHAR(2) NOT NULL," +
                "zip CHAR(5)," +
                "PRIMARY KEY (sup_id))";

        try (Statement stmt = connection.createStatement()) {
           stmt.executeUpdate(query);
        }
    }
}
