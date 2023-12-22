package com.interswitch.searchservice.controller;

import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.exception.BookSearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final WebClient webClient;

    @Autowired
    public SearchController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://inventory-service").build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        try {
            // Use WebClient to call the Book Inventory Microservice
            List<BookDTO> books = webClient.get()
                    .uri("/books/search?query={query}", query)
                    .retrieve()
                    .bodyToFlux(BookDTO.class)
                    .collectList()
                    .block();

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            // Handle the exception by throwing a custom exception
            throw new BookSearchException("Error occurred while searching for books: " + e.getMessage());
        }
    }
}
