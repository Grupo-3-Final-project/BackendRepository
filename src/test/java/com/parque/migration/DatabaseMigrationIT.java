package com.parque.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "migration"})
class DatabaseMigrationIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsWithFlywayMigrations() {
        Integer tables = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'PUBLIC' and table_name = 'ATTRACTIONS'",
                Integer.class
        );
        Integer migrations = jdbcTemplate.queryForObject(
                "select count(*) from \"flyway_schema_history\"",
                Integer.class
        );

        assertEquals(1, tables);
        assertTrue(migrations != null && migrations > 0);
    }
}
