package com.baha.oop.controller;

import com.baha.oop.service.BookService;
import com.baha.oop.service.BorrowingService;
import com.baha.oop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// This controller handles requests for the home dashboard page.
@Controller
@RequiredArgsConstructor
public class MainController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowingService borrowingService;
    private final com.baha.oop.repository.UserRepository userRepository;

    // This method handles GET requests for the "/" path (Home dashboard).
    @GetMapping("/")
    public String home(Model model) {
        // Count all books, members, active borrowings, and overdue borrowings for dashboard stats.
        long booksCount = bookService.getAllBooks().size();
        long membersCount = memberService.getAllMembers().size();
        long activeBorrowingsCount = borrowingService.getActiveBorrowings().size();
        long overdueBorrowingsCount = borrowingService.getOverdueBorrowings().size();

        // Check if the currently logged-in user has the MEMBER role.
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));

        // If the user is a MEMBER, calculate how many of their own borrowings are overdue.
        long myOverdueBorrowingsCount = 0;
        if (isMember && auth != null) {
            String username = auth.getName();
            com.baha.oop.model.User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getMemberId() != null) {
                myOverdueBorrowingsCount = borrowingService.getOverdueBorrowings().stream()
                        .filter(b -> b.getMember().getId().equals(user.getMemberId()))
                        .count();
            }
        }

        // Add these values to the Thymeleaf model so they can be shown on the home page.
        model.addAttribute("booksCount", booksCount);
        model.addAttribute("membersCount", membersCount);
        model.addAttribute("activeBorrowingsCount", activeBorrowingsCount);
        model.addAttribute("overdueBorrowingsCount", overdueBorrowingsCount);
        model.addAttribute("myOverdueBorrowingsCount", myOverdueBorrowingsCount);
        model.addAttribute("isMember", isMember);

        return "index";
    }
}