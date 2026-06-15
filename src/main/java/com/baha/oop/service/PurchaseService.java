package com.baha.oop.service;

import com.baha.oop.dto.PurchaseRequest;
import com.baha.oop.exception.BusinessRuleException;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Book;
import com.baha.oop.model.DeliveryStatus;
import com.baha.oop.model.Member;
import com.baha.oop.model.Purchase;
import com.baha.oop.repository.BookRepository;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// This service class contains the business logic for managing book purchases.
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    // This method starts a purchase request. The request will be 'PENDING' until a librarian approves it.
    @Transactional
    public Purchase purchaseBook(PurchaseRequest request) {
        // Find the book by its ID.
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));

        // Check if the book is available and if it is actually for sale.
        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book '" + book.getTitle() + "' is not available");
        }

        if (!book.isForSale()) {
            throw new BusinessRuleException("Book '" + book.getTitle() + "' is not for sale");
        }

        // Find the member who wants to buy the book.
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", request.getMemberId()));

        // Create a new Purchase request with status 'PENDING'.
        Purchase purchase = Purchase.builder()
                .book(book)
                .member(member)
                .purchaseDate(LocalDateTime.now())
                .pricePaid(book.getPrice())
                .status(com.baha.oop.model.PurchaseStatus.PENDING)
                .build();

        // If a delivery address is provided, set delivery status to 'PENDING'.
        if (request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty()) {
            purchase.setDeliveryAddress(request.getDeliveryAddress());
            purchase.setDeliveryStatus(DeliveryStatus.PENDING);
        } else {
            purchase.setDeliveryStatus(DeliveryStatus.NONE);
        }

        return purchaseRepository.save(purchase);
    }

    // This method is called by the LIBRARIAN to approve a purchase request.
    @Transactional
    public void approvePurchase(Long id) {
        // Find the purchase request.
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", id));
        
        // Ensure the purchase is still pending.
        if (purchase.getStatus() != com.baha.oop.model.PurchaseStatus.PENDING) {
            throw new BusinessRuleException("Purchase request is not pending");
        }
        
        // Make the book unavailable.
        Book book = purchase.getBook();
        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book '" + book.getTitle() + "' is no longer available");
        }
        book.setAvailable(false);
        bookRepository.save(book);

        // Approve the purchase request.
        purchase.setStatus(com.baha.oop.model.PurchaseStatus.APPROVED);
        purchaseRepository.save(purchase);

        // Automatically reject other pending requests for the same book.
        List<Purchase> otherPending = purchaseRepository.findByBookIdAndStatus(book.getId(), com.baha.oop.model.PurchaseStatus.PENDING);
        for (Purchase op : otherPending) {
            if (!op.getId().equals(id)) {
                op.setStatus(com.baha.oop.model.PurchaseStatus.REJECTED);
                purchaseRepository.save(op);
            }
        }
    }

    // This method is called by the LIBRARIAN to reject a purchase request.
    @Transactional
    public void rejectPurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", id));
        
        // Ensure the purchase is still pending.
        if (purchase.getStatus() != com.baha.oop.model.PurchaseStatus.PENDING) {
            throw new BusinessRuleException("Purchase request is not pending");
        }
        
        // Change status to 'REJECTED'.
        purchase.setStatus(com.baha.oop.model.PurchaseStatus.REJECTED);
        purchaseRepository.save(purchase);
    }

    // Return all purchases in the database.
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Return all purchases made by a specific member.
    public List<Purchase> getPurchasesByMemberId(Long memberId) {
        return purchaseRepository.findByMemberId(memberId);
    }
}
