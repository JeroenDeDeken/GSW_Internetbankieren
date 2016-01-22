package bankServer;

import bankserver.CentralConnection;
import bankserver.DBConnector;
import bankserver.Transaction;
import bankserver.TransactionState;
import static java.lang.Thread.sleep;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CentralConnectionTest {
    
    public static final String BANKING_CODE = "TEST";
    public static final String CENTRAL_URL = "localhost";
    public static final int CENTRAL_PORT = 4444;
    
    private static final int SHORT_NETWORK_TIMEOUT = 1000;
    private static final int LONG_NETWORK_TIMEOUT = 2000;
    
    public CentralConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        DBConnector.debug = true;
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
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
     * Test sending a transaction to central bank.
     * Check if the transaction is send and processed.
     * 
     * If it could not connect a fail with the message 'Could not connect to the CentraleBank' is thrown.
     */
    @Test
    public void testSend() {
        CentralConnection cc = new CentralConnection(BANKING_CODE, CENTRAL_URL, CENTRAL_PORT);
        Thread t = new Thread(cc);
        t.start();
        
        try { //Wait for the bank to have registred with the central bank
            sleep(SHORT_NETWORK_TIMEOUT);
        } catch (InterruptedException ex) {
            Logger.getLogger(CentralConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Transaction transaction = getTestTransaction();
        DBConnector.insertTransaction(transaction);
        
        boolean succes = cc.sendTransactionToCentral(transaction);
        if (succes) {
            System.out.println("transaction send succesfully to central bank");
            assertEquals(TransactionState.SENDTOCENTRAL, transaction.getState());
        } else {
            System.out.println("transaction failed to send central bank");
            assertEquals(TransactionState.WAITING, transaction.getState());
        }
        
        try { //Wait for the transaction to be processed by the bank
            sleep(LONG_NETWORK_TIMEOUT);
        } catch (InterruptedException ex) {
            Logger.getLogger(CentralConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        transaction = DBConnector.getTransactionForId(transaction.getTransactionId());
        if (transaction.getState().equals(TransactionState.WAITING)) {
            fail("Could not connect to the CentraleBank");
        }
        assertEquals(TransactionState.SUCCEEDED, transaction.getState());
    }
    
    /**
     * @return The test transaction used for the test
     */
    private Transaction getTestTransaction() {
        long transactionId = UUID.randomUUID().getMostSignificantBits();
        String debitor = "TEST0123456789";
        String creditor = "TEST9876543210";
        double amount = 100.0;
        String message = "Test transaction";

        return new Transaction(transactionId, debitor, creditor, amount, message, TransactionState.WAITING);
    }
}
