package com.learnkafka.events.consumer;

import com.learnkafka.events.consumer.services.ILibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 20, 2023
 * @since 17
 */
@Component
@Slf4j
public class LibraryEventsConsumer {

    private final ILibraryEventsService service;

    @Autowired
    public LibraryEventsConsumer(ILibraryEventsService service) {
        this.service = service;
    }

    @KafkaListener(topics = {"library-events"})
    public void onMessage(final ConsumerRecord<Integer, String> consumerRecord) {
        log.info("ConsumerRecord: {}", consumerRecord);

        try {
            service.process(consumerRecord);
        } catch (IllegalAccessException ex) {
            log.error(ex.getMessage());
        }
    }
}