package de.fekl.jdbc;

import de.fekl.core.OutboxTableConfig;
import de.fekl.core.OutboxTableRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
public class JdbcToJsonNodeOutboxTableRepository implements OutboxTableRepository<List<Map<String, Object>>> {

    private final @NonNull OutboxTableConfig config;
    private final @NonNull JdbcTemplate jdbcTemplate;

    @Getter(lazy = true, value = PRIVATE)
    private final String fetchStatement = initFetchStatement();

    @Getter(lazy = true, value = PRIVATE)
    private final String deleteStatement = initDeleteStatement();

    @Getter(lazy = true, value = PRIVATE)
    private final String updateStatement = initUpdateStatement();

    @Getter(lazy = true, value = PRIVATE)
    private final String deleteManyStatement = initDeleteManyStatement();

    @Getter(lazy = true, value = PRIVATE)
    private final String updateManyStatement = initUpdateManyStatement();

    @SneakyThrows
    @Override
    public List<Map<String, Object>> fetchRows() {
        return jdbcTemplate.query(getFetchStatement(), new ColumnMapRowMapper(), config.fetchSize());
    }

    @Override
    public void deleteRow(Object rowId) {
        jdbcTemplate.update(getDeleteStatement(), rowId);
    }

    @Override
    public void updateRow(Object rowId) {
        jdbcTemplate.update(getUpdateStatement(), config.statusValue(), rowId);
    }

    @Override
    public void deleteRows(List<Object> rowIds) {
        jdbcTemplate.update(formatSql(rowIds, getDeleteManyStatement()), rowIds.toArray());
    }

    private String formatSql(List<Object> rowIds, String sqlFormat) {
        String inSql = String.join(",", Collections.nCopies(rowIds.size(), "?"));
        return String.format(sqlFormat, inSql);
    }

    @Override
    public void updateRows(List<Object> rowIds) {
        String inSql = String.join(",", Collections.nCopies(rowIds.size(), "?"));
        jdbcTemplate.update(
                formatSql(rowIds, getUpdateManyStatement()),
                Stream.concat(Stream.of(config.statusValue()), rowIds.stream()).toList().toArray());
    }

    private String toParameterString(List<Object> rowIds) {
        return rowIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
    }

    private String initUpdateStatement() {
        return "update " + config.tableName() + " set " + config.statusColumn() + " = ? where " + config.idColumn() + " = ?";
    }

    private String initUpdateManyStatement() {
        return "update " + config.tableName() + " set " + config.statusColumn() + " = ? where " + config.idColumn() + " in (%s)";
    }

    private String initDeleteStatement() {
        return "delete " + config.tableName() + " where " + config.idColumn() + " = ?";
    }

    private String initDeleteManyStatement() {
        return "delete " + config.tableName() + " where " + config.idColumn() + " in (%s)";
    }

    private String initFetchStatement() {
        return "select * from " + config.tableName() + " order by " + config.sortKey() + " ASC FETCH NEXT ? ROWS ONLY";
    }
}
