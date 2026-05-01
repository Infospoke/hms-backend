package com.hms.service.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SequenceSyncRunner implements ApplicationRunner {
 
    @Autowired
    private JdbcTemplate jdbcTemplate;
 
    @Override
    public void run(ApplicationArguments args) {
        syncSequence("tb_user", "user_id", "user_seq");
        syncSequence("tb_role","role_id","role_seq");
    }
 
    private void syncSequence(String table, String column, String sequence) {
        String sql = String.format(
            "SELECT setval('%s', COALESCE((SELECT MAX(%s) FROM %s), 1))",
            sequence, column, table
        );
        jdbcTemplate.execute(sql);
    }
}


