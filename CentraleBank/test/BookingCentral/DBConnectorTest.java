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
 * @author Roy
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
     * Create the test database before every test
     */
    @Before
    public void setUp() {        
        DBConnector.createDatabase();
    }
    
    /**
     * Delete the test database after every test
     */
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

        //Generate and insert a test transaction
        System.out.println("insertTransaction");
        Transaction transaction = new Transaction(transactionId, debitor, creditor, amount, message);
        boolean result = DBConnector.insertTransaction(transaction);
        assertTrue(result);
        
        //Check if the inserted transaction is with the unprocessed transactions
        System.out.println("getUnprocessedTransactions");
        Iterable<Transaction> resultSet = DBConnector.getUnprocessedTransactions();
        int count = 0;
        for(Transaction returnedTransaction : resultSet) {
            assertEquals(transaction, returnedTransaction);
            count++;
        }
        assertEquals(1, count);
        
        //Check if the transaction state can be set to succeeded
        System.out.println("changeTransactionState");
        TransactionState state = TransactionState.SUCCEEDED;
        result = DBConnector.changeTransactionState(transaction, state);
        assertTrue(result);
        
        //Check if the unprocessed transactions are empty now
        System.out.println("getUnprocessedTransactions");
        Set<Transaction> set = DBConnector.getUnprocessedTransactions();
        result = set.isEmpty();
        assertTrue(result);
    }

    /**
     * Test of disconnect method, of class DBConnector.
     * This method may cause others to fail. Run tests seperatly.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        boolean result = DBConnector.disconnect();
        assertTrue(result);
    }
}