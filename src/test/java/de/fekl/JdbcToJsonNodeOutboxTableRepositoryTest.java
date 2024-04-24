package de.fekl;

import de.fekl.jdbc.JdbcToJsonNodeOutboxTableRepository;
import de.fekl.jdbc.OutboxTableConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcToJsonNodeOutboxTableRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    JdbcToJsonNodeOutboxTableRepository cut;

    @BeforeEach
    void beforeEach() {
        cut = new JdbcToJsonNodeOutboxTableRepository(
                new OutboxTableConfig(
                        "test",
                        "id",
                        "id",
                        "status",
                        "DONE",
                        3),
                jdbcTemplate
        );
    }

    @Sql(statements = {
            "Insert into TEST (ID, FIELD) values (1, 'hi1')",
            "Insert into TEST (ID, FIELD) values (2, 'hi2')"
    })
    @Test
    void fetchRows_should_fetch_rows() {
        var resultSet = cut.fetchRows();

        assertThat(resultSet.size()).isEqualTo(2);
    }

    @Sql(statements = {
            "Insert into TEST (ID, FIELD) values (1, 'hi1')",
            "Insert into TEST (ID, FIELD) values (2, 'hi2')"
    })
    @Test
    void deleteRow_should_delete_row() {
        cut.deleteRow(1);

        var resultSet = cut.fetchRows();
        assertThat(resultSet.size()).isEqualTo(1);
    }

    @Sql(statements = {
            "Insert into TEST (ID, FIELD) values (1, 'hi1')",
            "Insert into TEST (ID, FIELD) values (2, 'hi2')",
            "Insert into TEST (ID, FIELD) values (3, 'hi3')"
    })
    @Test
    void deleteRows_should_delete_row() {
        cut.deleteRows(List.of(1, 2));

        var resultSet = cut.fetchRows();
        assertThat(resultSet.size()).isEqualTo(1);
    }

    @Sql(statements = {
            "Insert into TEST (ID, FIELD) values (1, 'hi1')",
            "Insert into TEST (ID, FIELD) values (2, 'hi2')"
    })
    @Test
    void updateRow_should_update_row() {
        cut.updateRow(1);

        var resultSet = cut.fetchRows();
        assertThat(resultSet.size()).isEqualTo(2);
        assertThat(resultSet.getFirst().get("STATUS")).isEqualTo("DONE");
    }

    @Sql(statements = {
            "Insert into TEST (ID, FIELD) values (1, 'hi1')",
            "Insert into TEST (ID, FIELD) values (2, 'hi2')",
            "Insert into TEST (ID, FIELD) values (3, 'hi3')"
    })
    @Test
    void updateRows_should_update_row() {
        cut.updateRows(List.of(1, 2));

        var resultSet = cut.fetchRows();
        assertThat(resultSet.size()).isEqualTo(3);
        assertThat(resultSet.getFirst().get("STATUS")).isEqualTo("DONE");
    }

}