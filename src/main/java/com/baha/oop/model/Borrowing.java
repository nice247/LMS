package com.baha.oop.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "borrowings", indexes = {
        @Index(name = "idx_borrowing_return_date", columnList = "returnDate")
})
public class Borrowing {

    @EmbeddedId
    private BorrowingId id;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate borrowDate;
    private LocalDate returnDate;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue(LocalDate currentDate) {
        return isActive() && borrowDate.plusDays(14).isBefore(currentDate);
    }
}