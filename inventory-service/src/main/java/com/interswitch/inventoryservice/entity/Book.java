package com.interswitch.inventoryservice.entity;

import com.interswitch.inventoryservice.entity.enumeration.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Genre is required")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN code")
    private String isbn;

    private int availableQuantity;

    public Book(String title, int availableQuantity) {

        this.title = title;
        this.availableQuantity = availableQuantity;
    }

    public Book(String title, String author, Genre genre,String isbn, int availableQuantity) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.availableQuantity = availableQuantity;
    }

}
