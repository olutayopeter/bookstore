import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.inventoryservice.controller.BookController;
import com.interswitch.inventoryservice.controller.BookControllerExceptionHandler;
import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.dto.ErrorResponse;
import com.interswitch.inventoryservice.entity.enumeration.Genre;
import com.interswitch.inventoryservice.exception.BookNotFoundException;
import com.interswitch.inventoryservice.exception.BookSearchException;
import com.interswitch.inventoryservice.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new BookControllerExceptionHandler()) // Use if you have a separate exception handler
                .build();
    }

    @Test
    public void testGetAllBooks() throws Exception {
        List<BookDTO> books = Arrays.asList(
                new BookDTO("Title1", "Author1", Genre.FICTION, "ISBN1",8),
                new BookDTO("Title2", "Author2", Genre.THRILLER, "ISBN2",10)
        );

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));

        verify(bookService, times(1)).getAllBooks();
        verifyNoMoreInteractions(bookService);
    }

    // Similar tests for other methods (getBookById, createBook, updateBook, deleteBook)...

    @Test
    public void testHandleBookNotFoundException() throws Exception {
        when(bookService.getBookById(anyLong())).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Book not found"));

        verify(bookService, times(1)).getBookById(anyLong());
        verifyNoMoreInteractions(bookService);
    }

    @Test
    public void testHandleInvalidGenreException() throws Exception {
        BookDTO invalidBook = new BookDTO("InvalidTitle", "InvalidAuthor", null, "InvalidISBN",20);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid genre: null"));

        verifyNoInteractions(bookService);
    }

    @Test
    void searchBooks_Success() {
        // Mock the behavior of bookService.searchBooks()
        String query = "java";
        List<BookDTO> mockedBooks = Collections.singletonList(new BookDTO("Java Book"));
        when(bookService.searchBooks(query)).thenReturn(mockedBooks);

        // Call the controller method
        ResponseEntity<List<BookDTO>> responseEntity = bookController.searchBooks(query);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody() instanceof List);
        assertEquals(1, ((List<?>) responseEntity.getBody()).size());
        assertEquals("Java Book", ((List<?>) responseEntity.getBody()));

        // Verify that the bookService method was called
        verify(bookService, times(1)).searchBooks(query);
    }

    @Test
    void searchBooks_Exception() {
        // Mocking an exception scenario for bookService.searchBooks()
        String query = "nonexistent";
        when(bookService.searchBooks(query)).thenThrow(new BookSearchException("Invalid search query"));

        // Call the controller method
        ResponseEntity<?> responseEntity = bookController.searchBooks(query);

        // Verify the expected behavior for exception scenario
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
        assertEquals("Invalid search query", ((ErrorResponse) responseEntity.getBody()).getMessage());

        // Verify that the bookService method was called
        verify(bookService, times(1)).searchBooks(query);
    }


}

