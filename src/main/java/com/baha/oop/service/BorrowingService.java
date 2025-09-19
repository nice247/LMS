// BorrowingService.java
package com.baha.oop.service;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.*;
import com.baha.oop.repository.BookRepository;
import com.baha.oop.repository.BorrowingHistoryRepository;
import com.baha.oop.repository.BorrowingRepository;
import com.baha.oop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BorrowingHistoryRepository borrowingHistoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    @Transactional
    public Borrowing borrowBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        // التحقق من أن الكتاب متاح
        if (!book.isAvailable()) {
            throw new RuntimeException("الكتاب غير متاح للاستعارة");
        }

        // التحقق من أن العضو ليس لديه استعارة نشطة لنفس الكتاب
        Optional<Borrowing> existingBorrowing = borrowingRepository.findActiveBorrowing(memberId, bookId);
        if (existingBorrowing.isPresent()) {
            throw new RuntimeException("العضو لديه استعارة نشطة لهذا الكتاب بالفعل");
        }

        // تحديث حالة الكتاب إلى غير متاح
        book.setAvailable(false);
        bookRepository.save(book);

        // إنشاء استعارة جديدة
        BorrowingId borrowingId = new BorrowingId(bookId, memberId);
        Borrowing borrowing = new Borrowing();
        borrowing.setId(borrowingId);
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setReturnDate(null);

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public void returnBook(Long bookId, Long memberId) {
        BorrowingId borrowingId = new BorrowingId(bookId, memberId);
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing", "id", borrowingId));

        // نقل سجل الاستعارة إلى الجدول التاريخي
        BorrowingHistory history = BorrowingHistory.builder()
                .title(borrowing.getBook().getTitle())
                .member(borrowing.getMember().getName())
                .borrowDate(borrowing.getBorrowDate())
                .returnDate(LocalDate.now()) // تاريخ الإرجاع الحالي
                .build();

        borrowingHistoryRepository.save(history);

        // تحديث حالة الكتاب إلى متاح
        Book book = borrowing.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        // حذف سجل الاستعارة من الجدول الرئيسي
        borrowingRepository.delete(borrowing);
    }

    public List<BorrowingHistory> getAllBorrowingHistory() {
        return borrowingHistoryRepository.findAll();
    }


    public List<Borrowing> getActiveBorrowings() {
        return borrowingRepository.findByReturnDateIsNull();
    }

    public List<Borrowing> getOverdueBorrowings() {
        LocalDate fourteenDaysAgo = LocalDate.now().minusDays(14);
        return borrowingRepository.findByReturnDateIsNullAndBorrowDateBefore(fourteenDaysAgo);
    }
    // إضافة دوال جديدة للتعامل مع السجلات التاريخية
    public List<BorrowingHistory> getBorrowingHistoryByMember(String memberName) {
        return borrowingHistoryRepository.findByMemberName(memberName);
    }

    public List<BorrowingHistory> getBorrowingHistoryByBook(String bookTitle) {
        return borrowingHistoryRepository.findByBookTitle(bookTitle);
    }


    public Long countBorrowingHistoryByMember(String memberName) {
        return borrowingHistoryRepository.countByMemberName(memberName);
    }

}