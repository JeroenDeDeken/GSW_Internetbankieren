/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankServer;

import bankserver.Account;
import bankserver.ClientService;
import bankserver.Transaction;
import java.util.List;
import javax.xml.ws.Holder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roy
 */
public class ClientServiceTest {
    
    public ClientServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class ClientService.
     */
    @Test
    public void testGetInstance() {
        System.out.println("testGetInstance");
        ClientService result = ClientService.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of register method, of class ClientService.
     */
    @Test
    public void testRegisterSuccess() {
        System.out.println("testRegisterSuccess");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user123", password = "password123", residence = "residense";
        ClientService.registerStatus status = instance.register(username, password, residence, sessionID);
        assertEquals(ClientService.registerStatus.success, status);
        assertNotNull(sessionID.value);
    }

    /**
     * Test of register method, of class ClientService.
     */
    @Test
    public void testRegisterExists() {
        System.out.println("testRegisterExists");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user123", password = "password123", residence = "residense";
        ClientService.registerStatus status = instance.register(username, password, residence, sessionID);
        assertEquals(ClientService.registerStatus.usernameAlreadyExists, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of register method, of class ClientService.
     */
    @Test
    public void testRegisterUsernameToShort() {
        System.out.println("testRegisterUsernameToShort");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "123", password = "password123", residence = "residense";
        ClientService.registerStatus status = instance.register(username, password, residence, sessionID);
        assertEquals(ClientService.registerStatus.usernameToShort, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of register method, of class ClientService.
     */
    @Test
    public void testRegisterMissing() {
        System.out.println("testRegisterMissing");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user123", password = "", residence = "residense";
        ClientService.registerStatus status = instance.register(username, password, residence, sessionID);
        assertEquals(ClientService.registerStatus.missingFields, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of register method, of class ClientService.
     */
    @Test
    public void testRegisterPasswordToShort() {
        System.out.println("testRegisterPasswordToShort");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user123", password = "123", residence = "residense";
        ClientService.registerStatus status = instance.register(username, password, residence, sessionID);
        assertEquals(ClientService.registerStatus.passwordToShort, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of login method, of class ClientService.
     */
    @Test
    public void testLoginSuccess() {
        System.out.println("testLoginSuccess");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user123", password = "password123";
        ClientService.loginStatus status = instance.login(username, password, sessionID);
        assertEquals(ClientService.loginStatus.success, status);
        assertNotNull(sessionID.value);
    }

    /**
     * Test of login method, of class ClientService.
     */
    @Test
    public void testLoginNotFound() {
        System.out.println("testLoginNotFound");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user12345", password = "password123";
        ClientService.loginStatus status = instance.login(username, password, sessionID);
        assertEquals(ClientService.loginStatus.notFound, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of login method, of class ClientService.
     */
    @Test
    public void testLoginMissingFields() {
        System.out.println("testLoginMissingFields");
        ClientService instance = ClientService.getInstance();
        
        Holder<Integer> sessionID = new Holder<>();
        String username = "user12345", password = "";
        ClientService.loginStatus status = instance.login(username, password, sessionID);
        assertEquals(ClientService.loginStatus.missingFields, status);
        assertNull(sessionID.value);
    }

    /**
     * Test of logout method, of class ClientService.
     */
    @Test
    public void testLogout() {
        System.out.println("testLogout");
        int sessionID = 0;
        ClientService instance = new ClientService();
        instance.logout(sessionID);
    }
}
