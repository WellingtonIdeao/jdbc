package br.com.ideao.jdbc.service;

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
            jdbcRs.setUrl(properties.getProperty("jdbcUrl"));
            jdbcRs.setUsername(properties.getProperty("username"));
            jdbcRs.setPassword(properties.getProperty("password"));
            jdbcRs.setCommand("SELECT * FROM coffee");
            jdbcRs.execute();

            while (jdbcRs.next()) {
                String coffeeName = jdbcRs.getString("cof_name");
                int supplierId = jdbcRs.getInt("sup_id");
                float price = jdbcRs.getFloat("price");
                int sales = jdbcRs.getInt("sales");
                int total = jdbcRs.getInt("total");

                System.out.println(coffeeName + ", " + supplierId +", " + price +", " + sales + ", " + total);
            }
        }
    }
}
