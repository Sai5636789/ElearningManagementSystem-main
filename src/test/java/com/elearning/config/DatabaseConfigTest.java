package com.elearning.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConfigTest {

    @Test
    @DisplayName("Verify dataSource initialization does not return null")
    void testDataSourceInitialization() {
        DataSource ds = DatabaseConfig.getDataSource();
        assertNotNull(ds, "DataSource should be initialized");
    }
}
