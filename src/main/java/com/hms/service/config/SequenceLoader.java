package com.hms.service.config;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceLoader implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        try {
            executeSqlFile("db/migration/postgresql/datascript.sql");
            log.info("Stored Procedure SP_SALES_DASHBOARD_CARDS created/updated successfully");
        } catch (Exception e) {
            log.error("❌ Failed to create stored procedure", e);
        }
    }

    private void executeSqlFile(String path) throws Exception {

        ClassPathResource resource = new ClassPathResource(path);

        if (!resource.exists()) {
            throw new RuntimeException("SQL file not found: " + path);
        }

        String sql = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
        }
    }
}

 