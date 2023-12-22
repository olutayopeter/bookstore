import com.interswitch.checkoutservice.controller.CheckoutController;
import com.interswitch.checkoutservice.dto.CheckoutDTO;
import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.entity.enumeration.PaymentStatus;
import com.interswitch.checkoutservice.service.CheckoutService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    @Test
    public void testProcessCheckout_SuccessfulCheckout() {
        // Arrange
        Long userId = 1L;
        PaymentOption paymentOption = PaymentOption.WEB;
        when(checkoutService.processCheckout(userId, paymentOption)).thenReturn("Checkout process completed successfully");

        // Act
        ResponseEntity<String> response = checkoutController.processCheckout(userId, paymentOption);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Checkout process completed successfully", response.getBody());
    }

    @Test
    public void testProcessCheckout_FailedCheckout() {
        // Arrange
        Long userId = 1L;
        PaymentOption paymentOption = PaymentOption.WEB;
        when(checkoutService.processCheckout(userId, paymentOption)).thenThrow(new RuntimeException("Checkout failed"));

        // Act
        ResponseEntity<String> response = checkoutController.processCheckout(userId, paymentOption);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Checkout failed: Checkout failed", response.getBody());
    }

    @Test
    public void testGetPurchaseHistory_Successful() {
        // Arrange
        Long userId = 1L;
        List<CheckoutDTO> purchaseHistory = Arrays.asList(
                new CheckoutDTO(userId, Arrays.asList(1L, 2L), 2, PaymentOption.WEB, PaymentStatus.SUCCESS),
                new CheckoutDTO(userId, Arrays.asList(3L, 4L), 2, PaymentOption.USSD, PaymentStatus.SUCCESS)
        );
        when(checkoutService.getPurchaseHistory(userId)).thenReturn(purchaseHistory);

        // Act
        ResponseEntity<List<CheckoutDTO>> response = checkoutController.getPurchaseHistory(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchaseHistory, response.getBody());
    }

    @Test
    public void testGetPurchaseHistory_Failed() {
        // Arrange
        Long userId = 1L;
        when(checkoutService.getPurchaseHistory(userId)).thenThrow(new RuntimeException("Failed to retrieve purchase history"));

        // Act
        ResponseEntity<List<CheckoutDTO>> response = checkoutController.getPurchaseHistory(userId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}

