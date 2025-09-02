package br.com.ideao.jdbc.service;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class CoffeeTableJdbcRowSet {
    private Properties properties;
    private RowSetFactory rsFactory;

    public CoffeeTableJdbcRowSet() {

        try (InputStream in = getClass().getClassLoader().
                getResourceAsStream("jdbc.properties")) {

            this.rsFactory = RowSetProvider.newFactory();
            this.properties = new Properties();
            this.properties.load(in);

        } catch (IOException e) {
            throw new RuntimeException("Could not read jdbc.properties resource file: " + e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewTable() throws SQLException {
        try (JdbcRowSet jdbcRs = rsFactory.createJdbcRowSet()) {
            settingRowSet(jdbcRs);
            jdbcRs.setCommand("SELECT * FROM coffee");
            jdbcRs.execute();
            outputRowSet(jdbcRs);
        }
    }
    public void insertRow(String coffeeName, int supplierId, float price, int sales, int total) throws SQLException {
        try (JdbcRowSet uprs = rsFactory.createJdbcRowSet()) {
            settingRowSet(uprs);
            uprs.setCommand("SELECT * FROM coffee");
            uprs.execute();

            uprs.moveToInsertRow();

            uprs.updateString("cof_name", coffeeName);
            uprs.updateInt("sup_id", supplierId);
            uprs.updateFloat("price", price);
            uprs.updateInt("sales", sales);
            uprs.updateInt("total", total);

            uprs.insertRow();
            uprs.beforeFirst();
        }
    }

    private void settingRowSet(RowSet rowSet) {
        try {
            rowSet.setUrl(properties.getProperty("jdbcUrl"));
            rowSet.setUsername(properties.getProperty("username"));
            rowSet.setPassword(properties.getProperty("password"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void outputRowSet(RowSet rowSet) throws SQLException {
       rowSet.beforeFirst();
       while (rowSet.next()) {
           String coffeeName = rowSet.getString("cof_name");
           int supplierId = rowSet.getInt("sup_id");
           float price = rowSet.getFloat("price");
           int sales = rowSet.getInt("sales");
           int total = rowSet.getInt("total");

           System.out.println(coffeeName + ", " + supplierId +", " + price +", " + sales + ", " + total);
       }
    }

    public void modifyPrices(String coffeName, float price) throws SQLException {
        try (JdbcRowSet uprs = rsFactory.createJdbcRowSet()) {
           settingRowSet(uprs);
           uprs.setCommand("SELECT * FROM coffee WHERE cof_name = ?");
           uprs.setString(1, coffeName);
           uprs.execute();
           uprs.first();

           uprs.updateFloat("price", price);
           uprs.updateRow();
        }
    }
}
