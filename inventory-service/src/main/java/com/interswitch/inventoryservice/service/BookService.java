package com.interswitch.inventoryservice.service;

import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.exception.BookSearchException;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    BookDTO getBookById(Long id);

    List<BookDTO> getAllBooks();

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);

    List<BookDTO> searchBooks(String query) throws BookSearchException;

    void updateAvailableQuantity(Long bookId, int quantity);
}
