import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.checkoutservice.entity.Checkout;
import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.entity.enumeration.PaymentStatus;
import com.interswitch.checkoutservice.repository.CheckoutRepository;
import com.interswitch.checkoutservice.service.CheckoutService;
import com.interswitch.checkoutservice.service.impl.CheckoutServiceImpl;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.exception.UserNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    public void testProcessCheckout_SuccessfulPayment() {
        // Arrange
        Long userId = 1L;
        PaymentOption paymentOption = PaymentOption.WEB;
        BigDecimal totalAmount = new BigDecimal("100.00");

        UserResponse user = new UserResponse(userId, "John Doe", "john.doe@example.com");
        List<ShoppingCartDTO> cartItems = Arrays.asList(
                new ShoppingCartDTO(1L, 2L, 3, new BigDecimal("30.00")),
                new ShoppingCartDTO(1L, 3L, 1, new BigDecimal("70.00"))
        );

        when(restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, userId))
                .thenReturn(user);

        when(restTemplate.getForObject("http://cart-service/carts/{userId}", List.class, userId))
                .thenReturn(cartItems);

        when(checkoutRepository.save(any(Checkout.class)))
                .thenReturn(new Checkout(userId, Arrays.asList(2L, 3L), 4, paymentOption, PaymentStatus.SUCCESS));

        // Act
        String result = checkoutService.processCheckout(userId, paymentOption);

        // Assert
        assertEquals("Checkout process completed successfully", result);
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test(expected = UserNotFoundException.class)
    public void testProcessCheckout_UserNotFound() {
        // Arrange
        Long userId = 1L;
        PaymentOption paymentOption = PaymentOption.WEB;

        when(restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, userId))
                .thenReturn(null);

        // Act
        checkoutService.processCheckout(userId, paymentOption);
    }

}

