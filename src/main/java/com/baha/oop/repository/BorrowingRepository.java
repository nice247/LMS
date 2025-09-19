// BorrowingRepository.java
package com.baha.oop.repository;

import com.baha.oop.model.Borrowing;
import com.baha.oop.model.BorrowingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, BorrowingId> {
    List<Borrowing> findByMemberId(Long memberId);
    List<Borrowing> findByBookId(Long bookId);
    List<Borrowing> findByReturnDateIsNull();
    List<Borrowing> findByReturnDateIsNullAndBorrowDateBefore(LocalDate date);

    @Query("SELECT b FROM Borrowing b WHERE b.member.id = :memberId AND b.book.id = :bookId AND b.returnDate IS NULL")
    Optional<Borrowing> findActiveBorrowing(@Param("memberId") Long memberId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.member.id = :memberId AND b.returnDate IS NULL")
    Long countActiveBorrowingsByMember(@Param("memberId") Long memberId);
}