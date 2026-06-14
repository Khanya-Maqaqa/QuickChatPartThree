/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.mycompany.poepartthree.Login;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author kvmaq
 */
public class LoginTest {
    
    private Login login;

    @BeforeEach
    void setUp() {
        // Initialise a Login object with dummy stored credentials
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
    }

    //  checkUserName
    @Test
    void testCheckUserName_Valid() {
        assertTrue(login.checkUserName("kyl_1"),"Username 'kyl_1' should be valid (contains '_' and is ≤5 chars)");
    }

    // Username exceeds 5 chars return false 
    @Test
    void testCheckUserName_TooLong() {
        assertFalse(login.checkUserName("kyle!!!!!!!"),"Username 'kyle!!!!!!!' is too long and should fail");
    }

    // Username has no underscore return false
    @Test
    void testCheckUserName_NoUnderscore() {
        assertFalse(login.checkUserName("kyle1"),
                "Username 'kyle1' has no underscore and should fail");
    }

    //  checkPasswordComplexity
    // Password meets all complexity requirements return true 
    @Test
    void testCheckPasswordComplexity_Valid() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"),
                "Password 'Ch&&sec@ke99!' should meet all complexity requirements");
    }

    // Plain lowercase password return false 
    @Test
    void testCheckPasswordComplexity_Invalid() {
        assertFalse(login.checkPasswordComplexity("password"),
                "Password 'password' should fail (no uppercase, digit, or special char)");
    }

    //  checkCellPhoneNumber
    // Valid +27 number return true 
    @Test
    void testCheckCellPhoneNumber_Valid() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"),
                "Phone '+27838968976' should be accepted as a valid SA number");
    }

    // Number without international code return false
    @Test
    void testCheckCellPhoneNumber_Invalid() {
        assertFalse(login.checkCellPhoneNumber("08966553"),
                "Phone '08966553' has no international code and should fail");
    }

    // registerUser
    // All valid inputs return successful registration message 
    @Test
    void testRegisterUser_Success() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("User successfully registered.", result,
                "Valid inputs should produce 'User successfully registered.'");
    }

    // Invalid username return registration failure message
    @Test
    void testRegisterUser_InvalidUsername() {
        String result = login.registerUser("kyle!!!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Registration failed: invalid username.", result);
    }

    // Invalid password return registration failure message
    @Test
    void testRegisterUser_InvalidPassword() {
        String result = login.registerUser("kyl_1", "password", "+27838968976");
        assertEquals("Registration failed: invalid password.", result);
    }

    // Invalid phone return registration failure message
    @Test
    void testRegisterUser_InvalidPhone() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553");
        assertEquals("Registration failed: invalid cell phone number.", result);
    }

    //  loginUser  (assertTrue / assertFalse)
    // Correct credentials return true
    @Test
    void testLoginUser_Success() {
        // Must register first so stored credentials are set
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"),
                "Correct credentials should allow login");
    }

    // Wrong password return false 
    @Test
    void testLoginUser_Failure() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongpass"),
                "Wrong password should prevent login");
    }

    //  returnLoginStatus
    // Successful login returns welcome message containing first and last name 
    @Test
    void testReturnLoginStatus_Success() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        String status = login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle Smith it is great to see you again.", status);
    }

    //Failed login returns appropriate error message
    @Test
    void testReturnLoginStatus_Failure() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        String status = login.returnLoginStatus("kyl_1", "wrongpass");
        assertEquals("Username or password incorrect, please try again.", status);
    }
    
    public LoginTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
    }

    

    @org.junit.jupiter.api.AfterEach
    public void tearDown() throws Exception {
    }
    
    
    
   
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
