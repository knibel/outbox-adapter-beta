package de.fekl.core;

import java.util.List;
import java.util.Map;

public interface OutboxTableRepository {

    List<Map<String, Object>> fetchRows();

    void deleteRow(Object rowId);

    void updateRow(Object rowId);

    void deleteRows(List<Object> rowIds);

    void updateRows(List<Object> rowIds);
}
