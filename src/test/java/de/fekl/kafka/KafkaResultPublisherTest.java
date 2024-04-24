package de.fekl.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fekl.core.MappedRow;
import de.fekl.core.MappedRowImpl;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static de.fekl.kafka.KafkaResultPublisherTest.TOPIC;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getRecords;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getSingleRecord;

@EmbeddedKafka(topics = TOPIC)
@TestInstance(PER_CLASS)
@ExtendWith(InstancioExtension.class)
class KafkaResultPublisherTest {

    static final String TOPIC = "test";

    KafkaConsumer<String, String> consumer;
    KafkaResultPublisher cut;

    @WithSettings
    private final static Settings settings = Settings.create()
            .mapType(MappedRow.class, MappedRowImpl.class)
            .mapType(JsonNode.class, ObjectNode.class);

    @BeforeAll
    void beforeAll(EmbeddedKafkaBroker broker) {
        consumer = createConsumer(broker);
        cut = new KafkaResultPublisher(Map.of(
                KafkaResultPublisher.TOPIC_CONFIG_KEY, TOPIC,
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString()
        ));
    }

    @ParameterizedTest
    @InstancioSource
    public void publish_should_send(MappedRow<JsonNode> mappedRow) {
        cut.publish(mappedRow);

        var publishedRecord = getSingleRecord(consumer, TOPIC);
        assertThat(publishedRecord.value()).isEqualTo(mappedRow.mappedValue().toString());
        assertThat(publishedRecord.key()).isEqualTo(mappedRow.rowId().toString());
    }

    @ParameterizedTest
    @InstancioSource
    public void publishAll_should_send(List<MappedRow<JsonNode>> mappedRows) {
        cut.publishAll(mappedRows);

        var publishedRecords = getRecords(consumer);
        assertThat(publishedRecords).hasSameSizeAs(mappedRows);
    }

    private static KafkaConsumer<String, String> createConsumer(EmbeddedKafkaBroker broker) {
        var props = new Properties();
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
        props.put(GROUP_ID_CONFIG, "testGroup");
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

        var consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(List.of(TOPIC));

        return consumer;
    }
}