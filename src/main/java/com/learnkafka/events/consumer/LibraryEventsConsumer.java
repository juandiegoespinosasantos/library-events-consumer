package com.learnkafka.events.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("ConsumerRecord: {}", consumerRecord);
    }
}