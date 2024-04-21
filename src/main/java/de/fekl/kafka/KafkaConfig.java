package de.fekl.kafka;

public record KafkaConfig(
        String bootstrapServers,
        String topic
) {
}
