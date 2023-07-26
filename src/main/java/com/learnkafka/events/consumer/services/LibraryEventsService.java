package com.learnkafka.events.consumer.services;

import com.google.gson.Gson;
import com.learnkafka.events.consumer.model.entities.LibraryEvent;
import com.learnkafka.events.consumer.model.repositories.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 24, 2023
 * @since 17
 */
@Service
@Slf4j
public class LibraryEventsService implements ILibraryEventsService {

    private final LibraryEventsRepository repository;
    private final Gson gson;

    @Autowired
    public LibraryEventsService(LibraryEventsRepository repository, Gson gson) {
        this.repository = repository;
        this.gson = gson;
    }

    @Override
    public LibraryEvent process(final ConsumerRecord<Integer, String> consumerRecord) throws IllegalAccessException {
        LibraryEvent libraryEvent = gson.fromJson(consumerRecord.value(), LibraryEvent.class);
        log.info("libraryEvent: {}", libraryEvent);

        switch (libraryEvent.getType()) {
            case NEW -> {
                return save(libraryEvent);
            }
            case UPDATE -> {
                validate(libraryEvent);
                return save(libraryEvent);
            }
            default -> log.error("Invalid Library Event Type");
        }

        return null;
    }

    private LibraryEvent save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        log.info("Saved!");

        return repository.save(libraryEvent);
    }

    private void validate(final LibraryEvent libraryEvent) throws IllegalAccessException {
        Integer id = libraryEvent.getId();
        if (id == null) throw new IllegalAccessException("Library Event ID is missing!");

        Optional<LibraryEvent> opt = repository.findById(id);
        if (opt.isEmpty()) throw new IllegalAccessException("Not a valid Library Event!");

        log.info("Validation successful!");
    }
}