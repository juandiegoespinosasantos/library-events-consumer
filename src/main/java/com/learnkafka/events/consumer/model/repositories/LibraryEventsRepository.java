package com.learnkafka.events.consumer.model.repositories;

import com.learnkafka.events.consumer.model.entities.LibraryEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 24, 2023
 * @since 17
 */
@Repository
public interface LibraryEventsRepository extends CrudRepository<LibraryEvent, Integer> {
}