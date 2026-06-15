package com.baha.oop.controller;

import com.baha.oop.dto.PurchaseRequest;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Member;
import com.baha.oop.model.Purchase;
import com.baha.oop.model.User;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.UserRepository;
import com.baha.oop.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseRestController {

    private final PurchaseService purchaseService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/buy")
    public ResponseEntity<Purchase> buyBook(@Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.purchaseBook(request));
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<Purchase>> getMyPurchaseHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Member member = memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", user.getMemberId()));
                
        return ResponseEntity.ok(purchaseService.getPurchasesByMemberId(member.getId()));
    }
}
