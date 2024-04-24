package de.fekl.core;

import lombok.Builder;

@Builder
public record OutboxTableAdapterConfig(
        int batchSize,
        AckMode ackMode,
        ErrorHandling errorHandling
) {
    public OutboxTableAdapterConfig {
        if (ackMode == null) ackMode = AckMode.DELETE;
        if (errorHandling == null) errorHandling = ErrorHandling.THROW;
    }

}
