package BookingCentral;

import java.util.UUID;
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
public class TransactionHandlerTest {
    
    private static TransactionHandler handler;
    private static Transaction transaction;
    
    public TransactionHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        handler = new TransactionHandler(null);
        
        long transactionId = UUID.randomUUID().getMostSignificantBits();
        String debitor = "RABO1";
        String creditor = "RABO9";
        double amount = 10.0;
        String message = "Test";
        transaction = new Transaction(transactionId, debitor, creditor, amount, message);
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
     * Test of splitTransaction method, of class TransactionHandler.
     */
    @Test
    public void testSplitTransaction() {
        System.out.println("splitTransaction");
        String input = transaction.toString();
        Transaction expResult = transaction;
        Transaction result = handler.splitTransaction(input);
        assertEquals(expResult, result);
    }
}
