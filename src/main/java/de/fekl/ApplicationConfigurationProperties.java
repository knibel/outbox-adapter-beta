package de.fekl;

import de.fekl.core.OutboxTableAdapterConfig;
import de.fekl.jdbc.OutboxTableConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties("outbox")
public class ApplicationConfigurationProperties {
    private final Map<String, AdapterConfig> adapters = new HashMap<>();

    record AdapterConfig(OutboxTableConfig table, OutboxTableAdapterConfig adapter, Map<String, Object> kafka) {

    }
}


