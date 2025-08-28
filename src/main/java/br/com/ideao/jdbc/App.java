package br.com.ideao.jdbc;

import br.com.ideao.jdbc.service.CoffeeTable;
import br.com.ideao.jdbc.service.SupplierTable;
import br.com.ideao.jdbc.util.JdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
       try (Connection con = JdbcUtil.getConnection()) {
           System.out.println("Connected to database");
           SupplierTable st = new SupplierTable(con);
           CoffeeTable ct = new CoffeeTable(con);

           st.createTable();
           ct.createTable();

           st.populateTable();
           ct.populateTable();
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

       if(!JdbcUtil.dataSourceIsClosed()) {
           JdbcUtil.dataSourceClose();
       }
       System.out.println("Disconnected to database");
    }
}
