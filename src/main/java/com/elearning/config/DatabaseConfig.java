package com.elearning.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static volatile HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConfig.class) {
                if (dataSource == null) {
                    dataSource = initDataSource();
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void runMigrations() {
        try {
            log.info("Running Flyway database migrations");
            org.flywaydb.core.Flyway flyway = org.flywaydb.core.Flyway.configure()
                    .dataSource(getDataSource())
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
            log.info("Flyway migrations completed successfully");
        } catch (Exception e) {
            log.warn("Database migration skipped or deferred: {}", e.getMessage());
        }
    }

    public static void closePool() {
        synchronized (DatabaseConfig.class) {
            if (dataSource != null && !dataSource.isClosed()) {
                log.info("Closing database connection pool");
                dataSource.close();
                dataSource = null;
            }
        }
    }

    private static HikariDataSource initDataSource() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String url = getEnv(dotenv, "DB_URL", "jdbc:mysql://localhost:3306/ELearningSystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = getEnv(dotenv, "DB_USER", "elearning_user");
        String password = getEnv(dotenv, "DB_PASSWORD", "elearning_pass");

        log.info("Initializing HikariCP pool for database at {}", url);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setPoolName("ELearningPool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setInitializationFailTimeout(-1);

        return new HikariDataSource(config);
    }

    private static String getEnv(Dotenv dotenv, String key, String defaultValue) {
        String val = dotenv.get(key);
        if (val != null && !val.isBlank()) {
            return val;
        }
        String sysVal = System.getenv(key);
        if (sysVal != null && !sysVal.isBlank()) {
            return sysVal;
        }
        return defaultValue;
    }
}
