package com.baha.oop.controller;

import com.baha.oop.model.Book;
import com.baha.oop.model.Borrowing;
import com.baha.oop.model.BorrowingHistory;
import com.baha.oop.model.Member;
import com.baha.oop.service.BookService;
import com.baha.oop.service.BorrowingService;
import com.baha.oop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final BookService bookService;
    private final MemberService memberService;
    private final com.baha.oop.repository.UserRepository userRepository;

    @GetMapping
    public String listBorrowings(Model model) {
        List<Borrowing> borrowings = borrowingService.getAllBorrowings();
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBorrowings();
        List<BorrowingHistory> borrowingHistory = borrowingService.getAllBorrowingHistory();

        model.addAttribute("borrowings", borrowings);
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("overdueBorrowings", overdueBorrowings);
        model.addAttribute("borrowingHistory", borrowingHistory);

        return "borrowings";
    }

    // This method shows the form to borrow a book.
    @GetMapping("/new")
    public String showBorrowForm(Model model) {
        List<Book> availableBooks = bookService.getAvailableBooks();
        List<Member> members = memberService.getAllMembers();

        // Check if the current user is a MEMBER.
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));

        // If the user is a MEMBER, get their specific Member record to lock the selection.
        if (isMember) {
            String username = auth.getName();
            com.baha.oop.model.User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getMemberId() != null) {
                Member currentMember = memberService.getMemberById(user.getMemberId());
                model.addAttribute("currentMember", currentMember);
                model.addAttribute("currentMemberId", user.getMemberId());
            }
        }

        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("members", members);
        model.addAttribute("borrowing", new Borrowing());

        return "borrow-form";
    }

    // This method handles the POST request to confirm a book borrowing.
    @PostMapping("/borrow")
    public String borrowBook(@RequestParam Long bookId,
                             @RequestParam Long memberId,
                             @RequestParam(required = false, defaultValue = "false") boolean deliveryRequired,
                             @RequestParam(required = false) String deliveryAddress,
                             RedirectAttributes redirectAttributes) {
        try {
            com.baha.oop.dto.BorrowRequest req = new com.baha.oop.dto.BorrowRequest();
            req.setBookId(bookId);
            req.setMemberId(memberId);
            req.setDeliveryRequired(deliveryRequired);
            req.setDeliveryAddress(deliveryAddress);
            borrowingService.borrowBook(req);
            redirectAttributes.addFlashAttribute("message", "تم تسجيل الاستعارة بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في تسجيل الاستعارة: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        // If the user is a MEMBER, redirect them to their own borrowing history.
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));
        if (isMember) {
            return "redirect:/borrowings/my-history";
        }
        return "redirect:/borrowings";
    }

    // This method shows the return book form.
    @GetMapping("/return")
    public String showReturnForm(Model model) {
        List<Borrowing> activeBorrowings;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));

        // If the user is a MEMBER, only show their own active borrowings. Otherwise show all.
        if (isMember) {
            String username = auth.getName();
            com.baha.oop.model.User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getMemberId() != null) {
                activeBorrowings = borrowingService.getActiveBorrowings().stream()
                        .filter(b -> b.getMember().getId().equals(user.getMemberId()))
                        .toList();
            } else {
                activeBorrowings = List.of();
            }
        } else {
            activeBorrowings = borrowingService.getActiveBorrowings();
        }

        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("currentDate", LocalDate.now());
        return "return-form";
    }

    // This method handles the POST request to confirm a book return.
    @PostMapping("/return")
    public String returnBook(@RequestParam Long bookId,
                             @RequestParam Long memberId,
                             RedirectAttributes redirectAttributes) {
        try {
            borrowingService.returnBook(bookId, memberId);
            redirectAttributes.addFlashAttribute("message", "تم تسجيل الإرجاع بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في تسجيل الإرجاع: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        // If the user is a MEMBER, redirect them to their own borrowing history page.
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));
        if (isMember) {
            return "redirect:/borrowings/my-history";
        }
        return "redirect:/borrowings";
    }

    @GetMapping("/active")
    public String listActiveBorrowings(Model model) {
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        model.addAttribute("borrowings", activeBorrowings);
        model.addAttribute("title", "الاستعارات النشطة");
        return "borrowings-list";
    }

    @GetMapping("/overdue")
    public String listOverdueBorrowings(Model model) {
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBorrowings();
        model.addAttribute("borrowings", overdueBorrowings);
        model.addAttribute("title", "الاستعارات المتأخرة");
        return "borrowings-list";
    }

    @GetMapping("/history")
    public String showBorrowingHistory(Model model) {
        List<BorrowingHistory> borrowingHistory = borrowingService.getAllBorrowingHistory();
        model.addAttribute("borrowingHistory", borrowingHistory);
        return "borrowing-history";
    }

    @GetMapping("/history/member")
    public String showBorrowingHistoryByMember(@RequestParam String memberName, Model model) {
        List<BorrowingHistory> borrowingHistory = borrowingService.getBorrowingHistoryByMember(memberName);
        model.addAttribute("borrowingHistory", borrowingHistory);
        model.addAttribute("title", "السجل التاريخي للعضو: " + memberName);
        return "borrowing-history";
    }

    @GetMapping("/history/book")
    public String showBorrowingHistoryByBook(@RequestParam String bookTitle, Model model) {
        List<BorrowingHistory> borrowingHistory = borrowingService.getBorrowingHistoryByBook(bookTitle);
        model.addAttribute("borrowingHistory", borrowingHistory);
        model.addAttribute("title", "السجل التاريخي للكتاب: " + bookTitle);
        return "borrowing-history";
    }

    // This method shows the personal borrowing history and active borrowings for the logged-in Member.
    @GetMapping("/my-history")
    public String showMyBorrowingHistory(Model model) {
        // Get the current logged-in user's username.
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        com.baha.oop.model.User user = userRepository.findByUsername(username).orElse(null);
        
        List<BorrowingHistory> borrowingHistory;
        List<Borrowing> activeBorrowings;
        // If the user has a member account, load their active borrowings and borrowing history.
        if (user != null && user.getMemberId() != null) {
            Member member = memberService.getMemberById(user.getMemberId());
            borrowingHistory = borrowingService.getBorrowingHistoryByMember(member.getName());
            activeBorrowings = borrowingService.getActiveBorrowings().stream()
                    .filter(b -> b.getMember().getId().equals(user.getMemberId()))
                    .toList();
        } else {
            borrowingHistory = List.of();
            activeBorrowings = List.of();
        }
        
        model.addAttribute("borrowingHistory", borrowingHistory);
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("title", "سجل استعاراتي");
        return "borrowing-history";
    }
}