// BookService.java
package com.baha.oop.service;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Book;
import com.baha.oop.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    public Book saveBook(Book book) {
        // التحقق من الترقيم الدولي إذا كان موجوداً
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
            if (existingBook.isPresent() && !existingBook.get().getId().equals(book.getId())) {
                throw new RuntimeException("رقم ISBN يجب أن يكون فريداً");
            }
        }
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailable(true);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public Book updateBookAvailability(Long id, boolean available) {
        Book book = getBookById(id);
        book.setAvailable(available);
        return bookRepository.save(book);
    }
}