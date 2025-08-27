package br.com.ideao.jdbc;

import br.com.ideao.jdbc.util.JdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
       try (Connection con = JdbcUtil.getConnection()) {
           System.out.println("Connected to database");
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

       if(!JdbcUtil.dataSourceIsClosed()) {
           JdbcUtil.dataSourceClose();
       }
       System.out.println("Disconnected to database");
    }
}
