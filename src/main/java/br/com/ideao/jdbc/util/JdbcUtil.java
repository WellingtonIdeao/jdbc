package br.com.ideao.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcUtil {
    private static final String user = "root";
    private static final String pass = "password";
    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/test";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, pass);
    }
}
