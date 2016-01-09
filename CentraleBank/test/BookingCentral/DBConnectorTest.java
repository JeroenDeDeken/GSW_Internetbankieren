package BookingCentral;

import java.util.Set;
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
public class DBConnectorTest {
    
    public DBConnectorTest() {
    }
    
    /**
     * executed once before testing
     */
    @BeforeClass
    public static void setUpClass() {
        DBConnector.debug = true;
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * executed before every test
     */
    @Before
    public void setUp() {        
        DBConnector.createDatabase();
    }
    
    @After
    public void tearDown() {
        DBConnector.removeDatabase();
    }

    /**
     * Test of connect method, of class DBConnector.
     */
    @Test
    public void testConnect() {
        System.out.println("connect");
        boolean expResult = true;
        boolean result = DBConnector.connect();
        assertEquals(expResult, result);
    }

    /**
     * Test the transaction methods in the dbconnector class.
     * This will add a new transaction, change the transactionstate 
     * and request all unprocessed transactions.
     * Sometimes this method fails due to the order of processing the tests.
     */
    @Test
    public void testTransaction() {
        long transactionId = UUID.randomUUID().getMostSignificantBits();
        String debitor = "NL00RABO0123456789";
        String creditor = "NL00RABO9876543210";
        double amount = 100.0;
        String message = "Test transaction";

        System.out.println("insertTransaction");
        Transaction transaction = new Transaction(transactionId, debitor, creditor, amount, message);
        boolean expResult = true;
        boolean result = DBConnector.insertTransaction(transaction);
        assertEquals(expResult, result);
        
        System.out.println("getUnprocessedTransactions");
        Iterable<Transaction> resultSet = DBConnector.getUnprocessedTransactions();
        int count = 0;
        for(Transaction returnedTransaction : resultSet) {
            assertEquals(transaction, returnedTransaction);
            count++;
        }
        assertEquals(1, count);
        
        System.out.println("changeTransactionState");
        TransactionState state = TransactionState.SUCCEEDED;
        result = DBConnector.changeTransactionState(transaction, state);
        assertEquals(expResult, result);
        
        System.out.println("getUnprocessedTransactions");
        Set<Transaction> set = DBConnector.getUnprocessedTransactions();
        result = set.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of disconnect method, of class DBConnector.
     * This method may cause others to fail. Run tests seperatly.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        boolean expResult = true;
        boolean result = DBConnector.disconnect();
        assertEquals(expResult, result);
    }
}