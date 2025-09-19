// MainController.java
package com.baha.oop.controller;

import com.baha.oop.service.BookService;
import com.baha.oop.service.BorrowingService;
import com.baha.oop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BorrowingService borrowingService;

    @GetMapping("/")
    public String home(Model model) {
        long booksCount = bookService.getAllBooks().size();
        long membersCount = memberService.getAllMembers().size();
        long activeBorrowingsCount = borrowingService.getActiveBorrowings().size();
        long overdueBorrowingsCount = borrowingService.getOverdueBorrowings().size();

        model.addAttribute("booksCount", booksCount);
        model.addAttribute("membersCount", membersCount);
        model.addAttribute("activeBorrowingsCount", activeBorrowingsCount);
        model.addAttribute("overdueBorrowingsCount", overdueBorrowingsCount);

        return "index";
    }
}