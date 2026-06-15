package com.baha.oop.controller;

import com.baha.oop.dto.BorrowRequest;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Borrowing;
import com.baha.oop.model.BorrowingHistory;
import com.baha.oop.model.Member;
import com.baha.oop.model.User;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.UserRepository;
import com.baha.oop.service.BorrowingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingRestController {

    private final BorrowingService borrowingService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public BorrowingRestController(BorrowingService borrowingService,
                                   UserRepository userRepository,
                                   MemberRepository memberRepository) {
        this.borrowingService = borrowingService;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public ResponseEntity<List<Borrowing>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }

    @PostMapping("/borrow")
    public ResponseEntity<Borrowing> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.ok(borrowingService.borrowBook(request));
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnBook(@Valid @RequestBody BorrowRequest request) {
        borrowingService.returnBook(request.getBookId(), request.getMemberId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Borrowing>> getActiveBorrowings() {
        return ResponseEntity.ok(borrowingService.getActiveBorrowings());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Borrowing>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBorrowings());
    }

    @GetMapping("/history")
    public ResponseEntity<List<BorrowingHistory>> getAllBorrowingHistory() {
        return ResponseEntity.ok(borrowingService.getAllBorrowingHistory());
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<BorrowingHistory>> getMyBorrowingHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Member member = memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", user.getMemberId()));
        return ResponseEntity.ok(borrowingService.getBorrowingHistoryByMember(member.getName()));
    }
}
