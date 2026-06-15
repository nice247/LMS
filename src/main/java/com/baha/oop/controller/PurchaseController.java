package com.baha.oop.controller;

import com.baha.oop.dto.PurchaseRequest;
import com.baha.oop.model.Book;
import com.baha.oop.model.Member;
import com.baha.oop.model.User;
import com.baha.oop.repository.BookRepository;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.UserRepository;
import com.baha.oop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// This controller handles UI requests for book purchases and purchase requests.
@Controller
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    // This method shows the form to buy a book (or request a purchase).
    @GetMapping("/buy/{id}")
    public String showBuyForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null || !book.isForSale() || !book.isAvailable()) {
            redirectAttributes.addFlashAttribute("message", "الكتاب غير متاح للشراء");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/books";
        }
        
        // Find current member if logged in as MEMBER to pre-fill their info.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getMemberId() != null) {
            Member currentMember = memberRepository.findById(user.getMemberId()).orElse(null);
            model.addAttribute("currentMember", currentMember);
            model.addAttribute("currentMemberId", user.getMemberId());
        }
        
        model.addAttribute("book", book);
        model.addAttribute("members", memberRepository.findAll());
        return "purchase-form";
    }

    // This method processes the form submission to make a purchase or request.
    @PostMapping("/buy")
    public String executePurchase(@RequestParam Long bookId, 
                                  @RequestParam Long memberId,
                                  @RequestParam(required = false) String deliveryAddress,
                                  RedirectAttributes redirectAttributes) {
        try {
            PurchaseRequest req = new PurchaseRequest();
            req.setBookId(bookId);
            req.setMemberId(memberId);
            req.setDeliveryAddress(deliveryAddress);
            purchaseService.purchaseBook(req);
            
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isMember = auth != null && auth.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MEMBER"));
            
            // Show a different message to members because their purchase requires librarian approval.
            if (isMember) {
                redirectAttributes.addFlashAttribute("message", "تم تقديم طلب الشراء بنجاح! ينتظر موافقة صاحب المكتبة.");
            } else {
                redirectAttributes.addFlashAttribute("message", "تم تسجيل عملية الشراء بنجاح!");
            }
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "حدث خطأ: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/purchases/buy/" + bookId;
        }
    }

    // List all purchase requests (Librarian only).
    @GetMapping
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        return "purchases";
    }

    // Approve a pending purchase request (Librarian only).
    @PostMapping("/{id}/approve")
    public String approvePurchase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.approvePurchase(id);
            redirectAttributes.addFlashAttribute("message", "تم قبول طلب الشراء بنجاح!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "فشل قبول الطلب: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/purchases";
    }

    // Reject a pending purchase request (Librarian only).
    @PostMapping("/{id}/reject")
    public String rejectPurchase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.rejectPurchase(id);
            redirectAttributes.addFlashAttribute("message", "تم رفض طلب الشراء.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-warning");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "فشل رفض الطلب: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/purchases";
    }
}
