package de.fekl.core;

import lombok.NonNull;

public record OutboxTableConfig(
        @NonNull String tableName,
        String sortKey,
        @NonNull String idColumn,
        String statusColumn,
        String statusValue,
        int fetchSize,
        int batchSize,
        AckMode ackMode
) {

}
