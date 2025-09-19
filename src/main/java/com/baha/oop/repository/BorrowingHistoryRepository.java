package com.baha.oop.repository;

import com.baha.oop.model.BorrowingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingHistoryRepository extends JpaRepository<BorrowingHistory, Long> {

    @Query("SELECT bh FROM BorrowingHistory bh WHERE bh.member LIKE %:memberName%")
    List<BorrowingHistory> findByMemberName(@Param("memberName") String memberName);

    @Query("SELECT bh FROM BorrowingHistory bh WHERE bh.title LIKE %:bookTitle%")
    List<BorrowingHistory> findByBookTitle(@Param("bookTitle") String bookTitle);

    @Query("SELECT COUNT(bh) FROM BorrowingHistory bh WHERE bh.member LIKE %:memberName%")
    Long countByMemberName(@Param("memberName") String memberName);
}