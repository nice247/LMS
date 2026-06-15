package com.baha.oop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book", indexes = {
        @Index(name = "idx_book_isbn", columnList = "isbn"),
        @Index(name = "idx_book_title", columnList = "title")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(name = "book_seq", sequenceName = "book_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String title;

    private String author;
    @Column(unique = true)
    private String isbn;

    private boolean available = true;

    private java.math.BigDecimal price;
    private boolean forSale = false;
}
