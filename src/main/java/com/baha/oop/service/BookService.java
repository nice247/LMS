package com.baha.oop.service;

import com.baha.oop.exception.BusinessRuleException;
import com.baha.oop.exception.DuplicateResourceException;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Book;
import com.baha.oop.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    @Transactional
    public Book saveBook(Book book) {
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
            if (existingBook.isPresent() && !existingBook.get().getId().equals(book.getId())) {
                throw new DuplicateResourceException("A book with ISBN " + book.getIsbn() + " already exists");
            }
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        if (!book.isAvailable()) {
            throw new BusinessRuleException("Cannot delete a book that is currently borrowed");
        }
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

    @Transactional
    public Book updateBookAvailability(Long id, boolean available) {
        Book book = getBookById(id);
        book.setAvailable(available);
        return bookRepository.save(book);
    }
}