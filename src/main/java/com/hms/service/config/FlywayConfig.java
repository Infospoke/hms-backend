/*
 * package com.hms.service.config;
 * 
 * import org.flywaydb.core.Flyway; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration;
 * 
 * @Configuration public class FlywayConfig {
 * 
 * @Bean public Flyway flyway() { Flyway flyway = Flyway.configure()
 * .dataSource("jdbc:postgresql://localhost:5432/hms", "postgres", "root")
 * .locations("classpath:db/migration/postgresql") .baselineOnMigrate(true)
 * .load();
 * 
 * flyway.migrate(); return flyway; } }
 */