package com.interswitch.inventoryservice.service.impl;

import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.entity.Book;
import com.interswitch.inventoryservice.entity.enumeration.Genre;
import com.interswitch.inventoryservice.exception.*;
import com.interswitch.inventoryservice.repository.BookRepository;
import com.interswitch.inventoryservice.service.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository)
    {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return convertToDTO(book);
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        validateGenre(bookDTO.getGenre());
        // Check if the book already exists
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateBookException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        // Check if a book with the same title and author already exists
        if (bookRepository.existsByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor())) {
            throw new DuplicateBookException("Book with the title " + bookDTO.getTitle() + " and author " + bookDTO.getAuthor() + "  already exists.");
        }

        Book newBook = new Book(bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getGenre(), bookDTO.getIsbn(),bookDTO.getAvailableQuantity());
        newBook.setAvailableQuantity(0);
        Book savedBook = bookRepository.save(newBook);
        return convertToDTO(savedBook);
    }
    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        validateGenre(bookDTO.getGenre());
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();
            existingBook.setTitle(bookDTO.getTitle());
            existingBook.setAuthor(bookDTO.getAuthor());
            existingBook.setIsbn(bookDTO.getIsbn());
            existingBook.setGenre(bookDTO.getGenre());
            existingBook.setAvailableQuantity(bookDTO.getAvailableQuantity());
            Book updatedBook = bookRepository.save(existingBook);
            return convertToDTO(updatedBook);
        } else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
    }

    @Override
    public void updateAvailableQuantity(Long bookId, int quantity) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            int updatedQuantity = book.getAvailableQuantity() + quantity;
            if (updatedQuantity < 0) {
                throw new InsufficientStockException("Insufficient stock for book with id: " + bookId);
            }
            book.setAvailableQuantity(updatedQuantity);
            bookRepository.save(book);
        } else {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }
    }

    @Override
    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
    }

    @Override
    public List<BookDTO> searchBooks(String query) {
        try {
            List<Book> books = bookRepository.searchBooks(query);
            return books.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookSearchException("Error occurred while searching for books: " + e.getMessage());
        }
    }

    private BookDTO convertToDTO(Book book) {
        return new BookDTO(book.getTitle(), book.getAuthor(), book.getGenre(), book.getIsbn(),book.getAvailableQuantity());
    }

    private void validateGenre(Genre genre) {
        if (genre == null) {
            throw new InvalidGenreException("Genre cannot be null");
        }
    }
}