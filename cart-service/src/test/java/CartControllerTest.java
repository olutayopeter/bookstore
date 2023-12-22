import com.interswitch.cartservice.controller.CartController;
import com.interswitch.cartservice.dto.ErrorResponse;
import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.cartservice.exception.CartItemNotFoundException;
import com.interswitch.cartservice.service.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    public void testAddItemToCart_Success() {
        // Mocking service response
        ShoppingCartDTO cartItem = new ShoppingCartDTO(1L, 10L, 2,new BigDecimal("100.20"));
        Mockito.when(cartService.addItemToCart(1L, cartItem)).thenReturn(cartItem);

        // Test the controller method
        ResponseEntity<ShoppingCartDTO> responseEntity = cartController.addItemToCart(1L, cartItem);

        // Verify the response entity and status code
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Verify that the service method was called
        Mockito.verify(cartService, Mockito.times(1)).addItemToCart(1L, cartItem);
    }

    @Test
    public void testGetCartItems_Success() {
        // Mocking service response
        List<ShoppingCartDTO> cartItems = Arrays.asList(
                new ShoppingCartDTO(1L, 10L, 2,new BigDecimal("500")),
                new ShoppingCartDTO(1L, 20L, 1,new BigDecimal("1000.20"))
        );
        Mockito.when(cartService.getCartItems(1L)).thenReturn(cartItems);

        // Test the controller method
        ResponseEntity<List<ShoppingCartDTO>> responseEntity = cartController.getCartItems(1L);

        // Verify the response entity and status code
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the service method was called
        Mockito.verify(cartService, Mockito.times(1)).getCartItems(1L);
    }

    @Test
    public void testHandleCartItemNotFoundException() {
        // Mocking exception
        CartItemNotFoundException exception = new CartItemNotFoundException("Cart item not found");

        // Test the exception handler method
        ResponseEntity<ErrorResponse> responseEntity = cartController.handleCartItemNotFoundException(exception);

        // Verify the response entity and status code
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getBody().getStatus());
        assertEquals("Cart item not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testHandleGenericException() {
        // Mocking generic exception
        Exception exception = new Exception("Internal server error");

        // Test the exception handler method
        ResponseEntity<ErrorResponse> responseEntity = cartController.handleGenericException(exception);

        // Verify the response entity and status code
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().getStatus());
        assertEquals("Internal server error", responseEntity.getBody().getMessage());
    }

}

