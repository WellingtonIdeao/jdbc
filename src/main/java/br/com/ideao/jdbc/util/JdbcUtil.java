package br.com.ideao.jdbc.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcUtil {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig("/hikari.properties");
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void dataSourceClose() {
        dataSource.close();
    }

    public static boolean dataSourceIsClosed() {
       return dataSource.isClosed();
    }
}
