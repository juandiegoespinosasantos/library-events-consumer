package com.learnkafka.events.consumer.model.entities;

import com.learnkafka.events.consumer.enums.ELibraryEventTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 24, 2023
 * @since 17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LibraryEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = -6299727368364812738L;

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ELibraryEventTypes type;

    @OneToOne(mappedBy = "libraryEvent", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Book book;
}