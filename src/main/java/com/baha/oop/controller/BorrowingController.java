// BorrowingController.java
package com.baha.oop.controller;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Book;
import com.baha.oop.model.Borrowing;
import com.baha.oop.model.BorrowingHistory;
import com.baha.oop.model.Member;
import com.baha.oop.service.BookService;
import com.baha.oop.service.BorrowingService;
import com.baha.oop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @GetMapping
    public String listBorrowings(Model model) {
        List<Borrowing> borrowings = borrowingService.getAllBorrowings();
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBorrowings();
        List<BorrowingHistory> borrowingHistory = borrowingService.getAllBorrowingHistory(); // الجديد

        model.addAttribute("borrowings", borrowings);
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("overdueBorrowings", overdueBorrowings);
        model.addAttribute("borrowingHistory", borrowingHistory); // الجديد

        return "borrowings";
    }

    @GetMapping("/new")
    public String showBorrowForm(Model model) {
        List<Book> availableBooks = bookService.getAvailableBooks();
        List<Member> members = memberService.getAllMembers();

        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("members", members);
        model.addAttribute("borrowing", new Borrowing());

        return "borrow-form";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam Long bookId,
                             @RequestParam Long memberId,
                             RedirectAttributes redirectAttributes) {
        try {
            borrowingService.borrowBook(bookId, memberId);
            redirectAttributes.addFlashAttribute("message", "تم تسجيل الاستعارة بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في تسجيل الاستعارة: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/borrowings";
    }

    @GetMapping("/return")
    public String showReturnForm(Model model) {
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("currentDate", LocalDate.now()); // تأكد من إضافة هذا السطر
        return "return-form";
    }

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
    }// إضافة دوال جديدة لعرض السجلات التاريخية
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
}