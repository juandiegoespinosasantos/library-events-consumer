package com.learnkafka.events.consumer.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = -8461799036995399428L;

    @Id
    private Integer id;

    private String name;

    private String author;

    @OneToOne
    @JoinColumn(name = "id")
    private LibraryEvent libraryEvent;
}