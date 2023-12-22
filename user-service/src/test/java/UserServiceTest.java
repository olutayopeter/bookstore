
import com.interswitch.userservice.dto.request.UserRequest;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.entity.User;
import com.interswitch.userservice.repository.UserRepository;
import com.interswitch.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testCreateUser() {
        // Create a user request
        UserRequest userRequest = new UserRequest();
        userRequest.setFullName("Olutayo Adelodun");
        userRequest.setUsername("olutayopeter");
        userRequest.setPassword("olutayo123");

        // Call the createUser method
        UserResponse userResponse = userService.createUser(userRequest);

        // Verify that the user is created and returned
        assertNotNull(userResponse.getUserId());
        assertEquals("Olutayo Adelodun", userResponse.getFullName());
        assertEquals("olutayo123", userResponse.getUsername());

        // Verify that the password is hashed
        User user = userRepository.findById(userResponse.getUserId()).orElse(null);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches("olutayo123", user.getPassword()));
    }

    @Test
    public void testValidateCredentials_ValidCredentials() {
        // Create a user and save to the database
        User user = new User();
        user.setFullName("Edith Otulugbu");
        user.setUsername("edith.otulugbu");
        user.setPassword(passwordEncoder.encode("otulugbu123"));
        userRepository.save(user);

        // Call the validateCredentials method with valid credentials
        boolean isValid = userService.validateCredentials("edith.otulugbu", "otulugbu123");

        // Verify that the credentials are valid
        assertTrue(isValid);
    }

    @Test
    public void testValidateCredentials_InvalidCredentials() {
        // Call the validateCredentials method with invalid credentials
        boolean isValid = userService.validateCredentials("nonexistent.user", "invalidPassword");

        // Verify that the credentials are invalid
        assertFalse(isValid);
    }


}
