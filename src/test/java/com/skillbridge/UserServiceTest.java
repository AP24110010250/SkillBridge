package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.skillbridge.exception.InvalidLoginException;
import com.skillbridge.exception.UserAlreadyExistsException;
import com.skillbridge.model.User;
import com.skillbridge.service.UserService;
import com.skillbridge.util.PasswordUtil;

public class UserServiceTest {

    @Test
    public void testPasswordHashing() {
        String plain = "mySecretPassword";
        String hash1 = PasswordUtil.hashPassword(plain);
        String hash2 = PasswordUtil.hashPassword(plain);

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
        assertTrue(PasswordUtil.verifyPassword(plain, hash1));
        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash1));
    }

    @Test
    public void testUserRegistrationAndLogin() throws Exception {
        UserService userService = new UserService();

        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@skillbridge.com";
        User user = userService.registerUser("Test Candidate", uniqueEmail, "pass1234", "STUDENT", "Bio test", "Weekends");

        assertNotNull(user);
        assertTrue(user.getUserId() > 0);
        assertEquals("STUDENT", user.getRole());

        // Login with correct credentials
        User loggedIn = userService.loginUser(uniqueEmail, "pass1234");
        assertNotNull(loggedIn);
        assertEquals(user.getUserId(), loggedIn.getUserId());

        // Login with wrong password
        assertThrows(InvalidLoginException.class, () -> {
            userService.loginUser(uniqueEmail, "wrongPass");
        });

        // Duplicate registration
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser("Duplicate Candidate", uniqueEmail, "newpass", "STUDENT", "Bio", "Evenings");
        });
    }
}
