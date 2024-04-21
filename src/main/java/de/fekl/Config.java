package de.fekl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fekl.core.MappedRow;
import de.fekl.core.OutboxTableAdapter;
import de.fekl.jdbc.JdbcToJsonNodeOutboxTableRepository;
import de.fekl.jdbc.RowMapToJsonNodeMapper;
import de.fekl.kafka.KafkaResultPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Configuration
public class Config {

    @Bean
    JsonMapper jsonMapper() {
        return new JsonMapper();
    }

    @Bean
    List<OutboxTableAdapter<List<Map<String, Object>>, MappedRow<JsonNode>>> outboxTableAdapters(
            ApplicationConfigurationProperties configurationProperties,
            JdbcTemplate jdbcTemplate,
            JsonMapper jsonMapper

    ) {
        return configurationProperties.getAdapters().values().stream()
                .map(adapterConfig -> createAdapter(jdbcTemplate, jsonMapper, adapterConfig))
                .toList();
    }

    private static OutboxTableAdapter<List<Map<String, Object>>, MappedRow<JsonNode>> createAdapter(
            JdbcTemplate jdbcTemplate,
            JsonMapper jsonMapper,
            ApplicationConfigurationProperties.AdapterConfig adapterConfig) {
        return new OutboxTableAdapter<>(
                adapterConfig.table(),
                new KafkaResultPublisher(adapterConfig.kafka()),
                new JdbcToJsonNodeOutboxTableRepository(adapterConfig.table(), jdbcTemplate),
                new RowMapToJsonNodeMapper(adapterConfig.table().idColumn(), jsonMapper)

        );
    }

}
