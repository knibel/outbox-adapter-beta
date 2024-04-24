package de.fekl.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import de.fekl.core.MappedRow;
import de.fekl.core.ResultPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class KafkaResultPublisher implements ResultPublisher<MappedRow<JsonNode>> {

    public static final String TOPIC_CONFIG_KEY = "topic";
    
    private final Map<String, Object> config;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final KafkaProducer<String, String> producer = initProducer();

    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    @Override
    public void publish(MappedRow<JsonNode> mappedRow) {
        send(createRecord(mappedRow)).get();
    }

    @Override
    public void publishAll(List<MappedRow<JsonNode>> mappedRows) {
        mappedRows.stream()
                .map(this::createRecord)
                .forEach(this::send);
        flush();
    }

    private Future<RecordMetadata> send(ProducerRecord<String, String> record) {
        return getProducer().send(record);
    }

    private void flush() {
        getProducer().flush();
    }

    private KafkaProducer<String, String> initProducer() {
        return new KafkaProducer<>(createClientProperties());
    }

    private ProducerRecord<String, String> createRecord(MappedRow<JsonNode> mappedRow) {
        return new ProducerRecord<>((String) config.get(TOPIC_CONFIG_KEY), mappedRow.rowId().toString(), mappedRow.mappedValue().toString());
    }

    @SneakyThrows(UnknownHostException.class)
    private Properties createClientProperties() {
        var properties = new Properties();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.putAll(config);
        return properties;
    }
}
