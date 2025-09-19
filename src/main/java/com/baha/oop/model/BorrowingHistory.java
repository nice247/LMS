package com.baha.oop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "borrowing_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowingHistory {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "book_title")
    private String title;

    @Column(name = "member_name")
    private String member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

}