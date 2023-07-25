package com.learnkafka.events.consumer.services;

import com.learnkafka.events.consumer.model.entities.LibraryEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 24, 2023
 * @since 17
 */
public interface ILibraryEventsService {

    LibraryEvent process(ConsumerRecord<Integer, String> consumerRecord) throws IllegalAccessException;
}