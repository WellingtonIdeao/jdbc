package br.com.ideao.jdbc.service;

import java.sql.*;

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

    public void modifyPrices(float percentage) throws SQLException {
        try (Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
             ResultSet uprs = stmt.executeQuery("SELECT * FROM coffee")) {
            while(uprs.next()) {
               float f = uprs.getFloat("price");
               uprs.updateFloat("price", f * percentage);
               uprs.updateRow();
           }
        }
    }

    public void batchUpdate() throws SQLException {
        connection.setAutoCommit(false);
        try (Statement stmt = connection.createStatement()) {
            stmt.addBatch("INSERT INTO coffee " +
                    "VALUES('Amaretto', 49, 9.99, 0, 0)");
            stmt.addBatch("INSERT INTO coffee " +
                    "VALUES('Hazelnut', 49, 9.99, 0, 0)");
            stmt.addBatch("INSERT INTO coffee " +
                    "VALUES('Amaretto_decaf', 49, 10.99, 0, 0)");
            stmt.addBatch("INSERT INTO coffee " +
                    "VALUES('Hazelnut_decaf', 49, 10.99, 0, 0)");

            int[] updateCounts = stmt.executeBatch();
            connection.commit();
        } catch (BatchUpdateException b) {
            throw new RuntimeException(b);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void parameterizedBatchUpdate() throws SQLException {
        connection.setAutoCommit(false);
        String sql = "INSERT INTO coffee VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Amaretto");
            pstmt.setInt(2, 49);
            pstmt.setFloat(3, 9.99f);
            pstmt.setInt(4, 0);
            pstmt.setInt(5, 0);
            pstmt.addBatch();

            pstmt.setString(1, "Hazelnut");
            pstmt.setInt(2, 49);
            pstmt.setFloat(3, 9.99f);
            pstmt.setInt(4, 0);
            pstmt.setInt(5, 0);
            pstmt.addBatch();

            int[] updateCounts = pstmt.executeBatch();
            connection.commit();
        } catch (BatchUpdateException b) {
            throw new RuntimeException(b);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void insertRow(String coffeeName, int supplierID,  float price,
                          int sales, int total) throws SQLException {
        try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = stmt.executeQuery("SELECT * FROM coffee")) {
           uprs.moveToInsertRow();
           uprs.updateString("cof_name", coffeeName);
           uprs.updateInt("sup_id", supplierID);
           uprs.updateFloat("price", price);
           uprs.updateInt("sales", sales);
           uprs.updateInt("total", total);

           uprs.insertRow();
           uprs.beforeFirst();
        }
    }
}
