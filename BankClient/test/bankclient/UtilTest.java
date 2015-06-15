/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

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
public class UtilTest {
    
    public UtilTest() {
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
     * Test of toSHA512 method, of class Util.
     */
    @Test
    public void testToSHA512() throws Exception {
        System.out.println("testToSHA512");
        String str = "test123";
        String expResult = "daef4953b9783365cad66152237256cc46c5167cd16ab50fa597aa08ff964eb24fb19687f34d7665f778fcb6c5358fca5b81e1662cf9f73a2671c53f991";
        String result = Util.toSHA512(str);
        assertEquals(expResult, result);
    }

    /**
     * Test of isValidIBAN method, of class Util.
     */
    @Test
    public void testIsValidIBAN() {
        System.out.println("testIsValidIBAN");
        assertTrue(Util.isValidIBAN("ABN9000000000"));
        assertTrue(Util.isValidIBAN("RAB0000000001"));
        assertFalse(Util.isValidIBAN("RA30000000001"));
        assertFalse(Util.isValidIBAN("RAB000000001"));
        assertFalse(Util.isValidIBAN("RAB000000001K"));
    }
    
}
