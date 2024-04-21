package de.fekl.core;

import java.util.List;

public interface ResultPublisher<T> {

    void publish(T message);

    void publishAll(List<T> messages);
}
