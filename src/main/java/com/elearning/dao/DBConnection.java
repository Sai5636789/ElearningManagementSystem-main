package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

    public Connection c;
    public Statement s;

    public DBConnection() {
        try {
            c = DatabaseConfig.getConnection();
            s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            log.error("Failed to acquire connection or create statement", e);
        }
    }

    public void Close() {
        close();
    }

    @Override
    public void close() {
        try {
            if (s != null && !s.isClosed()) {
                s.close();
            }
        } catch (SQLException e) {
            log.error("Error closing statement", e);
        }
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            log.error("Error closing connection", e);
        }
    }
}
