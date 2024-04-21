package de.fekl.core;

public record MappedRowImpl<T>(Object rowId, T mappedValue) implements MappedRow<T> {
}
