import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.cartservice.entity.ShoppingCart;
import com.interswitch.cartservice.repository.CartRepository;
import com.interswitch.cartservice.service.CartService;
import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.entity.enumeration.Genre;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.exception.UserNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    @Test
    public void testAddItemToCart_Success() {
        // Mocking user and book responses
        UserResponse userResponse = new UserResponse("olutayo", "John Doe");
        BookDTO bookResponse = new BookDTO("Book1", "Author1", Genre.FICTION, "ISBN123", 10);

        // Mocking restTemplate responses
        Mockito.when(restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, 1L))
                .thenReturn(userResponse);
        Mockito.when(restTemplate.getForObject("http://book-service/books/{bookId}", BookDTO.class, "Book1"))
                .thenReturn(bookResponse);
        Mockito.when(cartRepository.save(Mockito.any(ShoppingCart.class)))
                .thenReturn(new ShoppingCart(1L, 10L, 2));

        // Mocking restTemplate put operation for updating book inventory
        Mockito.doNothing().when(restTemplate)
                .put("http://book-service/books/{bookId}", bookResponse, "Book1");

        // Test the service method
        ShoppingCartDTO cartItem = new ShoppingCartDTO(1L, 10L, 2,new BigDecimal("100.20"));
        ShoppingCartDTO result = cartService.addItemToCart(1L, cartItem);

        // Verify the result
        assertNotNull(result);
        assertEquals(cartItem.getUserId(), result.getUserId());
        assertEquals(cartItem.getBookId(), result.getBookId());
        assertEquals(cartItem.getQuantity(), result.getQuantity());

        // Verify that save and restTemplate methods were called
//        Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any(ShoppingCart.class));
//        Mockito.verify(restTemplate, Mockito.times(2)).getForObject(Mockito.anyString(), Mockito.any(), Mockito.anyVararg());
//        Mockito.verify(restTemplate, Mockito.times(1)).put(Mockito.anyString(), Mockito.any(), Mockito.anyVararg());
    }

    @Test
    public void testAddItemToCart_UserNotFound() {
        // Mocking restTemplate response for UserNotFoundException
        Mockito.when(restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, 1L))
                .thenReturn(null);

        // Test the service method
        ShoppingCartDTO cartItem = new ShoppingCartDTO(1L, 20L, 2,new BigDecimal("700.20"));
        assertThrows(UserNotFoundException.class, () -> cartService.addItemToCart(1L, cartItem));

        // Verify that restTemplate method was called
      //  Mockito.verify(restTemplate, Mockito.times(1)).getForObject(Mockito.anyString(), Mockito.any(), Mockito.anyVararg());
    }

    // Similar tests can be written for BookNotFoundException, InsufficientStockException, and other scenarios

    @Test
    public void testGetCartItems() {
        // Mocking cart items from the repository
        List<ShoppingCart> cartItems = Arrays.asList(
                new ShoppingCart(1L, 10L, 2),
                new ShoppingCart(1L, 20L, 1)
        );

        // Mocking repository response
        Mockito.when(cartRepository.findByUserId(1L)).thenReturn(cartItems);

        // Test the service method
        List<ShoppingCartDTO> result = cartService.getCartItems(1L);

        // Verify the result
        assertNotNull(result);
        assertEquals(cartItems.size(), result.size());

        // Verify that repository method was called
        Mockito.verify(cartRepository, Mockito.times(1)).findByUserId(1L);
    }

    // Additional tests for edge cases, exceptions, and other scenarios can be added
}

