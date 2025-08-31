package br.com.ideao.jdbc.service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException {
        String updateSalesSql = "UPDATE coffee SET sales = ? WHERE cof_name = ?";
        String updateSTotalSql = "UPDATE coffee SET total = total + ? WHERE cof_name = ?";

        try (PreparedStatement updateSales = connection.prepareStatement(updateSalesSql);
             PreparedStatement updateTotal = connection.prepareStatement(updateSTotalSql)) {

            connection.setAutoCommit(false);

            for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
                updateSales.setInt(1, e.getValue());
                updateSales.setString(2, e.getKey());
                updateSales.executeUpdate();

                updateTotal.setInt(1, e.getValue());
                updateTotal.setString(2, e.getKey());
                updateTotal.executeUpdate();

                connection.commit();
            }
        } catch (SQLException e) {
            if(connection != null) {
               try  {
                   System.err.print("Transaction is being rolled back");
                   connection.rollback();
               } catch (SQLException ex) {
                   throw new RuntimeException(ex);
               }
            }
            throw new RuntimeException(e);
        } finally {
            if(connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    public void updateCoffeeSalesWithStatement(HashMap<String, Integer> salesForWeek) throws SQLException {
        try (Statement updateSales = connection.createStatement();
             Statement updateTotal = connection.createStatement()) {
           connection.setAutoCommit(false);
           for (Map.Entry<String, Integer> e: salesForWeek.entrySet()) {
               updateSales.executeUpdate("UPDATE coffee SET sales = " + e.getValue() +
                       " WHERE cof_name = '" + e.getKey() + "'");
               updateTotal.executeUpdate("UPDATE coffee SET total = total + " + e.getValue() +
                       " WHERE cof_name = '" + e.getKey() + "'");

               connection.commit();
           }
        } catch (SQLException e) {
            if(connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if(connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    public void viewTableByNameSqlInjection(String nameWithSqlInjection) throws SQLException {
        String query = "SELECT * FROM coffee " +
                "WHERE cof_name = '" + nameWithSqlInjection+"'";
        try (Statement stmtVulnerable = connection.createStatement();
             ResultSet rs = stmtVulnerable.executeQuery(query)){
            while(rs.next()) {
                String coffeeName = rs.getString(1);
                int supplierID = rs.getInt(2);
                float price = rs.getFloat(3);
                int sales = rs.getInt(4);
                int total = rs.getInt(5);
                System.out.println(coffeeName + ", " + supplierID + ", " + price + ", " + sales + ", " + total);
            }
        }
    }
}
