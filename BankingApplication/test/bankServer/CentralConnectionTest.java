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
    
    @After
    public void tearDown() {
        DBConnector.removeDatabase();
    }
    
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
        
        long transactionId = UUID.randomUUID().getMostSignificantBits();
        String debitor = "NL00RABO0123456789";
        String creditor = "NL00RABO9876543210";
        double amount = 100.0;
        String message = "Test transaction";

        System.out.println("insertTransaction");
        Transaction transaction = new Transaction(transactionId, debitor, creditor, amount, message, TransactionState.WAITING);
        boolean expResult = true;
        boolean result = DBConnector.insertTransaction(transaction);
        assertEquals(expResult, result);
        
        boolean succes = cc.sendTransactionToCentral(transaction);
        transaction = DBConnector.getTransactionForId(transaction.getTransactionId());
        if (succes) {
            assertEquals(TransactionState.SENDTOCENTRAL, transaction.getState());
        } else {
            assertEquals(TransactionState.WAITING, transaction.getState());
        }
        
        try { //Wait for the transaction to be processed by the bank
            sleep(LONG_NETWORK_TIMEOUT);
        } catch (InterruptedException ex) {
            Logger.getLogger(CentralConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        transaction = DBConnector.getTransactionForId(transaction.getTransactionId());
        assertEquals(TransactionState.SUCCEEDED, transaction.getState());
    }
}
