// BookController.java
package com.baha.oop.controller;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Book;
import com.baha.oop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBooks(@RequestParam(required = false) String title,
                            @RequestParam(required = false) String author,
                            @RequestParam(required = false) String available,
                            Model model) {
        List<Book> books;

        if (title != null && !title.isEmpty()) {
            books = bookService.searchBooksByTitle(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookService.searchBooksByAuthor(author);
        } else if (available != null && !available.isEmpty()) {
            boolean isAvailable = Boolean.parseBoolean(available);
            books = isAvailable ? bookService.getAvailableBooks() : bookService.getAllBooks();
        } else {
            books = bookService.getAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("available", available);

        return "books";
    }

    @GetMapping("/new")
    public String showBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("message", "تم حفظ الكتاب بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في حفظ الكتاب: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "book-form";
        } catch (ResourceNotFoundException e) {
            return "redirect:/books";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "تم حذف الكتاب بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في حذف الكتاب: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/books";
    }

}