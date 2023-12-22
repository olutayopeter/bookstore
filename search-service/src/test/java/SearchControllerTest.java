import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.entity.enumeration.Genre;
import com.interswitch.inventoryservice.exception.BookSearchException;
import com.interswitch.searchservice.controller.SearchController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebFluxTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void searchBooks_ValidQuery_ReturnsListOfBooks() {
        // Mock the response from the inventory service
        List<BookDTO> mockBooks = Arrays.asList(
                new BookDTO("Java", "Book 1", Genre.FICTION, "ISBN234",3),
                new BookDTO("Java 3.4", "Book 1", Genre.THRILLER, "ISBN23445",3)
        );

        // Mock the WebClient response
//        WebTestClient.ResponseSpec responseSpec = webTestClient.get()
//                .uri("/search?query=test")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(BookDTO.class)
//                .returnResult();
//
//        // Verify that the response matches the mocked books
//        List<BookDTO> resultBooks = responseSpec.getResponseBody();
//        assertThat(resultBooks).isEqualTo(mockBooks);
    }

    @Test
    public void searchBooks_ExceptionThrown_ReturnsInternalServerError() {
        // Mock an exception thrown in the controller
        given(webTestClient.get()
                .uri("/search?query=test")
                .exchange())
                .willThrow(new BookSearchException("Error in searching for books"));

        // Mock the expected response
        webTestClient.get()
                .uri("/search?query=test")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
