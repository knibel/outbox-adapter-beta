package de.fekl.core;

import java.util.List;

public interface OutboxTableRepository<T> {

    T fetchRows();

    void deleteRow(Object rowId);

    void updateRow(Object rowId);

    void deleteRows(List<Object> rowIds);

    void updateRows(List<Object> rowIds);
}
