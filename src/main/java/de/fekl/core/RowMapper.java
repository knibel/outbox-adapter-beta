package de.fekl.core;

import java.util.List;

public interface RowMapper<I, O extends MappedRow<?>> {
    List<O> map(I input);
}
