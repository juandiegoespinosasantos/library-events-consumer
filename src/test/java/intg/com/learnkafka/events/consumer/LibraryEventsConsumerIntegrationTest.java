package com.learnkafka.events.consumer;

import com.google.gson.Gson;
import com.learnkafka.events.consumer.enums.ELibraryEventTypes;
import com.learnkafka.events.consumer.model.entities.Book;
import com.learnkafka.events.consumer.model.entities.LibraryEvent;
import com.learnkafka.events.consumer.model.repositories.LibraryEventsRepository;
import com.learnkafka.events.consumer.services.ILibraryEventsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 25, 2023
 * @since 17
 */
@SpringBootTest
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsConsumerIntegrationTest {

    @SpyBean
    private ILibraryEventsService spyLibraryEventsService;

    @SpyBean
    private LibraryEventsConsumer spyLibraryEventsConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    @Autowired
    private LibraryEventsRepository repository;

    @Autowired
    private Gson gson;

    @BeforeEach
    void setUp() {
        endpointRegistry.getListenerContainers()
                .forEach(container -> ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void publishNewLibraryEvent() throws ExecutionException, InterruptedException, IllegalAccessException {
        // Given
        String json = "{\"id\":null,\"type\":\"NEW\",\"book\":{\"id\":123,\"name\":\"Kafka Using Spring Boot Pt. II\"," +
                "\"author\":\"Dilip\"}}";
        kafkaTemplate.sendDefault(json).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Then
        Mockito.verify(spyLibraryEventsConsumer, Mockito.times(1)).onMessage(Mockito.isA(ConsumerRecord.class));
        Mockito.verify(spyLibraryEventsService, Mockito.times(1)).process(Mockito.isA(ConsumerRecord.class));

        List<LibraryEvent> list = (List<LibraryEvent>) repository.findAll();
        Assertions.assertEquals(1, list.size());
        list.forEach(item -> {
            Assertions.assertNotNull(item);
            Assertions.assertEquals(123, item.getBook().getId());
        });
    }

    @Test
    void publishUpdateLibraryEvent() throws ExecutionException, InterruptedException, IllegalAccessException {
        // Given
        String json = "{\"id\":null,\"type\":\"NEW\",\"book\":{\"id\":123,\"name\":\"Kafka Using Spring Boot Pt. II\"," +
                "\"author\":\"Dilip\"}}";
        LibraryEvent libraryEvent = gson.fromJson(json, LibraryEvent.class);
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        repository.save(libraryEvent);

        Book updatedBook = Book.builder()
                .id(123)
                .name("Kafka Using Spring Boot 2.x")
                .author("Dilip")
                .build();
        libraryEvent.setType(ELibraryEventTypes.UPDATE);
        libraryEvent.setBook(updatedBook);
        String updatedJson = gson.toJson(libraryEvent);
        kafkaTemplate.sendDefault(libraryEvent.getId(), updatedJson).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Then
        Mockito.verify(spyLibraryEventsConsumer, Mockito.times(1)).onMessage(Mockito.isA(ConsumerRecord.class));
        Mockito.verify(spyLibraryEventsService, Mockito.times(1)).process(Mockito.isA(ConsumerRecord.class));

        LibraryEvent savesLibraryEvent = repository.findById(libraryEvent.getId()).get();
        Assertions.assertEquals(updatedBook.getName(), savesLibraryEvent.getBook().getName());
    }

    @Test
    void publishUpdateLibraryEventWithNullLibraryEvent() throws ExecutionException, InterruptedException, IllegalAccessException {
        // Given
        String json = "{\"id\":null,\"type\":\"UPDATE\",\"book\":{\"id\":123,\"name\":\"Kafka Using Spring Boot Pt. II\"," +
                "\"author\":\"Dilip\"}}";
        kafkaTemplate.sendDefault(json).get();

        // When
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(5, TimeUnit.SECONDS);

        // Then
        Mockito.verify(spyLibraryEventsConsumer, Mockito.times(1)).onMessage(Mockito.isA(ConsumerRecord.class));
        Mockito.verify(spyLibraryEventsService, Mockito.times(1)).process(Mockito.isA(ConsumerRecord.class));
    }
}