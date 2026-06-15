package com.baha.oop.repository;

import com.baha.oop.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByMemberId(Long memberId);
    List<Purchase> findByStatus(com.baha.oop.model.PurchaseStatus status);
    List<Purchase> findByBookIdAndStatus(Long bookId, com.baha.oop.model.PurchaseStatus status);
}
