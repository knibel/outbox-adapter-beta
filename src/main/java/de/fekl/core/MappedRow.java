package de.fekl.core;

public interface MappedRow<T> {

    Object rowId();

    T mappedValue();
}
