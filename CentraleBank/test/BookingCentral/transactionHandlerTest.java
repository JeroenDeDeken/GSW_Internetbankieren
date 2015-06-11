/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookingCentral;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeroen
 */
public class transactionHandlerTest {
    
    private static TransactionHandler handler;
    
    public transactionHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        handler = new TransactionHandler();
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

    @Test
    public void testProcessInput() {
        handler.processInput(null);
    }
}
