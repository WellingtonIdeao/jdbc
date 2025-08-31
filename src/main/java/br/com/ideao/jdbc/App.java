package br.com.ideao.jdbc;

import br.com.ideao.jdbc.service.CoffeeTable;
import br.com.ideao.jdbc.service.SupplierTable;
import br.com.ideao.jdbc.util.JdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class App {
    public static void main(String[] args) {
       try (Connection con = JdbcUtil.getConnection()) {
           System.out.println("Connected to database");
           SupplierTable st = new SupplierTable(con);
           CoffeeTable ct = new CoffeeTable(con);

           HashMap<String, Integer> salesForWeek = new LinkedHashMap<>();
           salesForWeek.put("Amaretto", 15);


           st.createTable();
           ct.createTable();

//           st.populateTable();
//           ct.populateTable();
//           ct.modifyPrices(1.10f);
//           ct.batchUpdate();
//           ct.parameterizedBatchUpdate();
//           ct.insertRow("kona", 49, 15.99f, 0, 0);
//           ct.updateCoffeeSales(salesForWeek);
//           ct.updateCoffeeSalesWithStatement(salesForWeek);
//           ct.viewTableByNameSqlInjection("' or '1'='1");
//           ct.viewTableByNameSqlInjection("dummy' or '1'='1");
           ct.viewTableByNameSqlInjection("';DELETE FROM coffee WHERE cof_name = 'dummy");
//           ct.viewTable();
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

       if(!JdbcUtil.dataSourceIsClosed()) {
           JdbcUtil.dataSourceClose();
       }
       System.out.println("Disconnected to database");
    }
}
