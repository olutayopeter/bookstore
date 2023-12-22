import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.entity.Book;
import com.interswitch.inventoryservice.entity.enumeration.Genre;
import com.interswitch.inventoryservice.exception.BookSearchException;
import com.interswitch.inventoryservice.repository.BookRepository;
import com.interswitch.inventoryservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void getAllBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Arrays.asList(
                new Book("Book1", "Author1", Genre.FICTION, "ISBN1", 10),
                new Book("Book2", "Author2", Genre.THRILLER, "ISBN2", 5)
        ));

        // Act
        List<BookDTO> result = bookService.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Book1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals(Genre.FICTION, result.get(0).getGenre());
        assertEquals("ISBN1", result.get(0).getIsbn());
        assertEquals(10, result.get(0).getAvailableQuantity());

        assertEquals("Book2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals(Genre.THRILLER, result.get(1).getGenre());
        assertEquals("ISBN2", result.get(1).getIsbn());
        assertEquals(5, result.get(1).getAvailableQuantity());

        // Verify that the repository method was called once
        verify(bookRepository, times(1)).findAll();
    }

    // Rest of the tests remain the same...

    @Test
    public void searchBooks_Success() {
        // Mock the behavior of bookRepository.searchBooks()
        String query = "java";
        List<Book> mockedBooks = Collections.singletonList(new Book("Java Book", 10));
        when(bookRepository.searchBooks(query)).thenReturn(mockedBooks);

        // Call the service method
        List<BookDTO> result = bookService.searchBooks(query);

        // Verify the expected behavior
        assertEquals(1, result.size());
        assertEquals("Java Book", result.get(0).getTitle());
        assertEquals(10, result.get(0).getAvailableQuantity());

        // Verify that the bookRepository method was called
        verify(bookRepository, times(1)).searchBooks(query);
    }

    @Test
    public void searchBooks_Exception() {
        // Mocking an exception scenario for bookRepository.searchBooks()
        String query = "nonexistent";
        when(bookRepository.searchBooks(query)).thenThrow(new RuntimeException("Database connection failed"));

        // Verify that BookSearchException is thrown when an exception occurs
        assertThrows(BookSearchException.class, () -> bookService.searchBooks(query));
    }
}
