package br.pucminas.lumen_coin_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationConfig implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        dropNotNullIfNeeded("tb_benefits", "company_id");
        dropNotNullIfNeeded("tb_benefit_redemptions", "company_id");
    }

    private void dropNotNullIfNeeded(String table, String column) {
        String sql = """
                SELECT is_nullable
                FROM information_schema.columns
                WHERE table_name = ? AND column_name = ?
                """;

        String isNullable = jdbcTemplate.queryForObject(sql, String.class, table, column);

        if ("NO".equals(isNullable)) {
            jdbcTemplate.execute(
                    "ALTER TABLE " + table + " ALTER COLUMN " + column + " DROP NOT NULL");
            log.info("Migration: dropped NOT NULL from {}.{}", table, column);
        }
    }
}
