package de.fekl.jdbc;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record OutboxTableConfig(
        @NonNull String tableName,
        String sortKey,
        @NonNull String idColumn,
        String statusColumn,
        String statusValue,
        int fetchSize
) {

}
