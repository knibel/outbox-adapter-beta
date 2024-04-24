package de.fekl.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OutboxTableAdapterTest {

    @Test
    void test() {
        OutboxTableRepository mock = mock(OutboxTableRepository.class);
        var cut = new OutboxTableAdapter<>(
                OutboxTableAdapterConfig.builder().errorHandling(ErrorHandling.LOG).build(),
                mock(ResultPublisher.class),
                mock,
                mock(RowMapper.class)
        );

        when(mock.fetchRows()).thenThrow(RuntimeException.class);
        cut.processNextRows();
    }

    @Test
    void test2() {
        OutboxTableRepository mock = mock(OutboxTableRepository.class);
        var cut = new OutboxTableAdapter<>(
                OutboxTableAdapterConfig.builder().build(),
                mock(ResultPublisher.class),
                mock,
                mock(RowMapper.class)
        );

        when(mock.fetchRows()).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> cut.processNextRows());
    }

}