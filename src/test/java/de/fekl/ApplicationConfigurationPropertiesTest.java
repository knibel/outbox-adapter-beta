package de.fekl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "outbox.adapters.x.table.table-name=test",
        "outbox.adapters.x.table.id-column=test",
        "outbox.adapters.x.kafka.topic=test",
        "outbox.adapters.y.table.table-name=test",
        "outbox.adapters.y.table.id-column=test"
})
class ApplicationConfigurationPropertiesTest {
    @Autowired
    ApplicationConfigurationProperties applicationConfigurationProperties;

    @Test
    void test() {
        System.out.println(applicationConfigurationProperties);
    }
}