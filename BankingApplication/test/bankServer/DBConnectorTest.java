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
     * Test the customer account methods in the dbconnector.
     * This will create a new account, check the initial newBalance
     * and add some money to the newBalance, this new value is checked as well.
     */
    @Test
    public void testCustomerAccount() { // Account class gives an Uncompilable source code error
//        double newBalance = 100.0;
//        Account account = new Account(0, 100);
//        String IBANNumber = account.getIBAN();
//        
//        //TODO fix the tests
//        //createNewCustomerAccount & removeCustomerAccount werken niet meer omdat er gewerkt wordt met gebruikers en rekeningen, niet iban-rekeningen
//        System.out.println("createNewCustomerAccount");
//        boolean expCreateResult = true;
//        boolean createResult = DBConnector.createNewCustomerAccount(account);
//        assertEquals(expCreateResult, createResult);
//
//        System.out.println("getAccountBalance");
//        double expGetResult = 0.0;
//        double getResult = DBConnector.getAccountBalance(IBANNumber);
//        assertEquals(expGetResult, getResult, 0.0);
//    
//        System.out.println("setAccountBalance");
//        boolean expSetResult = true;
//        boolean setResult = DBConnector.setAccountBalance(IBANNumber, newBalance);
//        assertEquals(expSetResult, setResult);
//        
//        System.out.println("getAccountBalance");
//        expGetResult = newBalance;
//        getResult = DBConnector.getAccountBalance(IBANNumber);
//        assertEquals(expGetResult, getResult, 0.0);
    }

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
        Transaction transaction = new Transaction(transactionId, debitor, creditor, amount, message, TransactionState.INITIAL);
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
            assertEquals(transaction.getTransactionId(), returnedTransaction.getTransactionId());
        }
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
}
