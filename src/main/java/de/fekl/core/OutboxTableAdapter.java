package de.fekl.core;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class OutboxTableAdapter<I, O extends MappedRow<?>> {

    private final OutboxTableConfig config;
    private final ResultPublisher<O> resultPublisher;
    private final OutboxTableRepository<I> repository;
    private final RowMapper<I, O> rowMapper;

    void processNextRows() {
        var fetchedRows = repository.fetchRows();
        List<O> mappedRows = rowMapper.map(fetchedRows);

        if (config.batchSize() > 1) {
            processInBatches(mappedRows);
        } else {
            processOneByOne(mappedRows);
        }
    }

    private void processInBatches(List<O> rows) {
        var partitions = partition(rows, config.batchSize());
        for (var partition : partitions) {
            resultPublisher.publishAll(partition);
            switch (config.ackMode()) {
                case DELETE -> repository.deleteRows(getRowIds(partition));
                case UPDATE -> repository.updateRows(getRowIds(partition));
            }
        }
    }

    private void processOneByOne(List<O> rows) {
        for (var row : rows) {
            resultPublisher.publish(row);
            switch (config.ackMode()) {
                case DELETE -> repository.deleteRow(row.rowId());
                case UPDATE -> repository.updateRow(row.rowId());
            }
        }
    }

    private List<Object> getRowIds(List<O> partition) {
        return partition.stream().map(MappedRow::rowId).toList();
    }

    private static <T> List<List<T>> partition(List<T> list, int n) {
        var partitions = new ArrayList<List<T>>(list.size() / n + 1);

        for (int i = 0; i < list.size(); i += n) {
            partitions.add(list.subList(i, Math.min(i + n, list.size())));
        }

        return partitions;
    }
}
