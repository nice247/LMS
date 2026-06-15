package com.baha.oop.service;

import com.baha.oop.exception.BusinessRuleException;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.*;
import com.baha.oop.repository.BookRepository;
import com.baha.oop.repository.BorrowingHistoryRepository;
import com.baha.oop.repository.BorrowingRepository;
import com.baha.oop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private static final int MAX_ACTIVE_BORROWINGS_PER_MEMBER = 5;

    private final BorrowingRepository borrowingRepository;
    private final BorrowingHistoryRepository borrowingHistoryRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    @Transactional
    public Borrowing borrowBook(com.baha.oop.dto.BorrowRequest request) {
        Long bookId = request.getBookId();
        Long memberId = request.getMemberId();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book '" + book.getTitle() + "' is not available for borrowing");
        }

        Optional<Borrowing> existingBorrowing = borrowingRepository.findActiveBorrowing(memberId, bookId);
        if (existingBorrowing.isPresent()) {
            throw new BusinessRuleException("Member already has an active borrowing for this book");
        }

        Long activeCount = borrowingRepository.countActiveBorrowingsByMember(memberId);
        if (activeCount >= MAX_ACTIVE_BORROWINGS_PER_MEMBER) {
            throw new BusinessRuleException("Member has reached the maximum limit of " + MAX_ACTIVE_BORROWINGS_PER_MEMBER + " active borrowings");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        BorrowingId borrowingId = new BorrowingId(bookId, memberId);
        Borrowing borrowing = new Borrowing();
        borrowing.setId(borrowingId);
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setReturnDate(null);
        if (request.isDeliveryRequired()) {
            borrowing.setDeliveryAddress(request.getDeliveryAddress());
            borrowing.setDeliveryStatus(DeliveryStatus.PENDING);
        } else {
            borrowing.setDeliveryStatus(DeliveryStatus.NONE);
        }

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public void returnBook(Long bookId, Long memberId) {
        BorrowingId borrowingId = new BorrowingId(bookId, memberId);
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing", "id", borrowingId));

        BorrowingHistory history = BorrowingHistory.builder()
                .title(borrowing.getBook().getTitle())
                .member(borrowing.getMember().getName())
                .borrowDate(borrowing.getBorrowDate())
                .returnDate(LocalDate.now())
                .build();

        borrowingHistoryRepository.save(history);

        Book book = borrowing.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

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