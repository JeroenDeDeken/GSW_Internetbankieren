package bankServer;

import bankserver.DBConnector;
import bankserver.Transaction;
import bankserver.TransactionState;
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
        DBConnector.createDatabase();
    }
    
    @AfterClass
    public static void tearDownClass() {
        DBConnector.removeDatabase();
    }
    
    /**
     * executed before every test
     */
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
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
     * Test of disconnect method, of class DBConnector.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        boolean expResult = true;
        boolean result = DBConnector.disconnect();
        assertEquals(expResult, result);
    }

    /**
     * Test the customer account methods in the dbconnector.
     * This will create a new account, check the initial balance
     * and add some money to the balance, this new value is checked as well.
     */
//    @Test
//    public void testCustomerAccount() {
//        System.out.println("createNewCustomerAccount");
//        String IBANNumber = "NL00RABO0123456789";
//        boolean expCreateResult = true;
//        boolean createResult = DBConnector.createNewCustomerAccount(IBANNumber);
//        assertEquals(expCreateResult, createResult);
//
//        System.out.println("getAccountBalance");
//        double expGetResult = 0.0;
//        double getResult = DBConnector.getAccountBalance(IBANNumber);
//        assertEquals(expGetResult, getResult, 0.0);
//    
//        System.out.println("setAccountBalance");
//        double balance = 100.0;
//        boolean expSetResult = true;
//        boolean setResult = DBConnector.setAccountBalance(IBANNumber, balance);
//        assertEquals(expSetResult, setResult);
//        
//        System.out.println("getAccountBalance");
//        expGetResult = 100.0;
//        getResult = DBConnector.getAccountBalance(IBANNumber);
//        assertEquals(expGetResult, getResult, 0.0);
//        
//        System.out.println("removeCustomerAccount");
//        boolean expRemoveResult = true;
//        boolean removeResult = DBConnector.removeCustomerAccount(IBANNumber);
//        assertEquals(expRemoveResult, removeResult);
//    }

    /**
     * Test the transaction methods in the dbconnector class.
     * This will add a new transaction, change the transactionstate 
     * and request all unprocessed transactions.
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
        
        System.out.println("changeTransactionState");
        TransactionState state = TransactionState.WAITING;
        result = DBConnector.changeTransactionState(transaction, state);
        assertEquals(expResult, result);
        
        System.out.println("getUnprocessedTransactions");
        Iterable<Transaction> resultSet = DBConnector.getUnprocessedTransactions();
        for(Transaction returnedTransaction : resultSet) {
            assertEquals(transaction, returnedTransaction);
        }
    }
}
