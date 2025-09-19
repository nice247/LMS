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
@Table(name = "borrowings")
public class Borrowing {

    @EmbeddedId
    private BorrowingId id;   // المفتاح المركب (bookId + memberId)

    @ManyToOne
    @MapsId("bookId")        // يربط الـ bookId من BorrowingId بالعمود book_id
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("memberId")      // يربط الـ memberId من BorrowingId بالعمود member_id
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate borrowDate;
    private LocalDate returnDate;
    // إضافة هذه الدوال إلى كيان Borrowing
    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue(LocalDate currentDate) {
        return isActive() && borrowDate.plusDays(14).isBefore(currentDate); // افترضنا مدة استعارة 14 يوم
    }
}