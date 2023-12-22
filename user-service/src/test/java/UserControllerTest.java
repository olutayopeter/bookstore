import com.interswitch.userservice.controller.UserController;
import com.interswitch.userservice.dto.request.UserRequest;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.entity.User;
import com.interswitch.userservice.repository.UserRepository;
import com.interswitch.userservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

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
        userRequest.setFullName("James Peter");
        userRequest.setUsername("james.peter");
        userRequest.setPassword("jamespeter123");

        // Call the createUser method in the controller
        ResponseEntity<UserResponse> responseEntity = userController.createUser(userRequest);

        // Verify that the user is created and returned
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getUserId());
        assertEquals("James Peter", responseEntity.getBody().getFullName());
        assertEquals("james.peter", responseEntity.getBody().getUsername());

        // Verify that the password is hashed
        User user = userRepository.findById(responseEntity.getBody().getUserId()).orElse(null);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches("jamespeter123", user.getPassword()));
    }

    @Test
    public void testGetUserById_ValidUser() {
        // Create a user and save to the database
        User user = new User();
        user.setFullName("James Peter");
        user.setUsername("james.peter");
        user.setPassword(passwordEncoder.encode("jamespeter123"));
        userRepository.save(user);

        // Call the getUserById method in the controller
        ResponseEntity<UserResponse> responseEntity = userController.getUserById(user.getId());

        // Verify that the user is retrieved and returned
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("James Peter", responseEntity.getBody().getFullName());
        assertEquals("james.peter", responseEntity.getBody().getUsername());
    }

    @Test
    public void testGetUserById_InvalidUser() {
        // Call the getUserById method with an invalid user ID
        ResponseEntity<UserResponse> responseEntity = userController.getUserById(-1L);

        // Verify that the user is not found
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


}
