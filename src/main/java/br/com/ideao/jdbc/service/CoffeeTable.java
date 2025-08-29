package br.com.ideao.jdbc.service;

import java.sql.Connection;
import java.sql.ResultSet;
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

    public void populateTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
           stmt.executeUpdate("INSERT INTO coffee " +
                   "VALUES ('Colombian', 101, 7.99, 0, 0)");
           stmt.executeUpdate("INSERT INTO coffee " +
                   "VALUES ('French_Roast', 49, 8.99, 0, 0)");
           stmt.executeUpdate("INSERT INTO coffee " +
                   "VALUES ('Espresso', 150, 9.99, 0, 0)");
           stmt.executeUpdate("INSERT INTO coffee " +
                   "VALUES ('Colombian_Decaf', 101, 8.99, 0, 0)");
           stmt.executeUpdate("INSERT INTO coffee " +
                   "VALUES ('French_Roast_Decaf', 49, 9.99, 0, 0)");
        }
    }

    public void viewTable() throws SQLException {
        String query = "SELECT cof_name, sup_id, price, sales, total FROM coffee";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
           while(rs.next()) {
              String coffeeName = rs.getString("cof_name");
              int supplierID = rs.getInt("sup_id");
              float price =  rs.getFloat("price");
              int sales = rs.getInt("sales");
              int total = rs.getInt("total");

              System.out.println(coffeeName + ", " + supplierID + ", " + price + ", " + sales + ", " +total);
           }
        }
    }
}
