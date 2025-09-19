package com.baha.oop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(name = "book_seq", sequenceName = "BOOK_SEQ", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String title;

    private String author;
    @Column(unique = true)
    private String isbn;

    private boolean available = true;
}
