package de.fekl.core;

import java.util.List;
import java.util.Map;

public interface RowMapper<O extends MappedRow<?>> {
    List<O> map(List<Map<String, Object>> input);
}
