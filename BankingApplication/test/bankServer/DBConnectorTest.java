package bankServer;

import bankserver.DBConnector;
import bankserver.Account;
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
        boolean result = DBConnector.connect();
        assertTrue(result);
    }

    /**
     * Test the customer account methods in the dbconnector.
     * This will create a new account, check the initial balance
     * and add some money to the balance, this new value is checked as well.
     */
    @Test
    public void testCustomerAccount() {
        double newBalance = 100.0;
        Account account = new Account(0, 100);
        String IBANNumber = account.getIBAN();
        
        //Check if a customer account can be created with a new IBAN
        System.out.println("createNewCustomerAccount");
        boolean createResult = DBConnector.createNewCustomerAccount(account);
        assertTrue(createResult);

        //Check if the inital balance is set correctly
        System.out.println("getAccountBalance");
        Double expGetResult = 0.0;
        Double getResult = DBConnector.getAccountBalance(IBANNumber);
        assertEquals(expGetResult, getResult);
    
        //Check if the balance can be set correctly
        System.out.println("setAccountBalance");
        boolean setResult = DBConnector.setAccountBalance(IBANNumber, newBalance);
        assertTrue(setResult);
        
        //Check if the balance is adjusted to the newly set
        System.out.println("getAccountBalance");
        getResult = DBConnector.getAccountBalance(IBANNumber);
        assertEquals(newBalance, getResult, 0.0);
    }

    /**
     * Test the transaction methods in the dbconnector class.
     * This will add a new transaction, change the transactionstate 
     * and request all unprocessed transactions.
     * Sometimes this method fails because of the following order in the tests.
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
        Transaction transaction = new Transaction(transactionId, debitor, creditor, amount, message, TransactionState.INITIAL);
        boolean result = DBConnector.insertTransaction(transaction);
        assertTrue(result);
        
        //Set the status of the transaction to waiting (for processing)
        System.out.println("changeTransactionState");
        TransactionState state = TransactionState.WAITING;
        result = DBConnector.changeTransactionState(transaction, state);
        assertTrue(result);
        
        //Check if the transactions is within the unprocessed transactions
        System.out.println("getUnprocessedTransactions");
        Iterable<Transaction> resultSet = DBConnector.getUnprocessedTransactions();
        int count = 0;
        for(Transaction returnedTransaction : resultSet) {
            assertEquals(transaction.getTransactionId(), returnedTransaction.getTransactionId());
            count++;
        }
        assertEquals(1, count);
    }

    /**
     * Test of disconnect method, of class DBConnector.
     * Make sure this test is the last to run because others will fail otherwise.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        boolean result = DBConnector.disconnect();
        assertTrue(result);
    }
}