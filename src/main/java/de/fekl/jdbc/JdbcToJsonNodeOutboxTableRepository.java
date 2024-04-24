package de.fekl.jdbc;

import de.fekl.core.OutboxTableRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JdbcToJsonNodeOutboxTableRepository implements OutboxTableRepository {

    private final @NonNull OutboxTableConfig config;
    private final @NonNull JdbcTemplate jdbc;

    @SneakyThrows
    @Override
    public List<Map<String, Object>> fetchRows() {
        return jdbc.query("select * from " + config.tableName() + " order by " + config.sortKey() + " ASC FETCH NEXT ? ROWS ONLY",
                new ColumnMapRowMapper(), config.fetchSize());
    }

    @Override
    public void deleteRow(Object rowId) {
        jdbc.update("delete " + config.tableName() + " where " + config.idColumn() + " = ?",
                rowId);
    }

    @Override
    public void updateRow(Object rowId) {
        jdbc.update("update " + config.tableName() + " set " + config.statusColumn() + " = ? where " + config.idColumn() + " = ?",
                config.statusValue(), rowId);
    }

    @Override
    public void deleteRows(List<Object> rowIds) {
        jdbc.update(formatSql(rowIds, "delete " + config.tableName() + " where " + config.idColumn() + " in (%s)"),
                rowIds.toArray());
    }

    @Override
    public void updateRows(List<Object> rowIds) {
        jdbc.update(
                formatSql(rowIds, "update " + config.tableName() + " set " + config.statusColumn() + " = ? where " + config.idColumn() + " in (%s)"),
                Stream.concat(Stream.of(config.statusValue()), rowIds.stream()).toList().toArray());
    }

    private String formatSql(List<Object> rowIds, String sqlFormat) {
        String inSql = String.join(",", Collections.nCopies(rowIds.size(), "?"));
        return String.format(sqlFormat, inSql);
    }

}
