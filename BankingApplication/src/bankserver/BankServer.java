package bankserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 *
 * @author Jeroen
 */
public class BankServer {
    
    private static BankServer bs;
    
    public static BankServer getInstance() {
        if (bs == null) bs = new BankServer();
        return bs;
    }

    //The banking code used for IBAN
    //Local
    public static final String BANKING_NAME = "ABN Amro";
    public static final String BANKING_CODE = "ABN";
    private static final String SOAP_URL = "http://localhost:8080/BankServer";
    private static final String CENTRAL_URL = "localhost";
    private static final int CENTRAL_PORT = 4444;
    //Test PC
//    public static final String BANKING_NAME = "Rabobank";
//    public static final String BANKING_CODE = "RAB";
//    private static final String SOAP_URL = "http://localhost:8080/BankServer";
//    private static final String CENTRAL_URL = "192.168.1.2";
//    private static final int CENTRAL_PORT = 4444;
    
    private static CentralConnection mCentralConnection;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBConnector.createDatabase();
        launchSoapClientService();
        
        mCentralConnection = new CentralConnection(BANKING_CODE, CENTRAL_URL, CENTRAL_PORT);
        Thread t = new Thread(mCentralConnection);
        t.start();
    }
    
    /**
     * Launches the SOAP webservice for bank clients.
     */
    private static void launchSoapClientService() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            HttpContext c = server.createContext("/BankServer");
            Endpoint e = Endpoint.create(ClientService.getInstance());
            e.publish(c);
            server.setExecutor(null);
            server.start();
//            Endpoint.publish(SOAP_URL, ClientService.getInstance());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    /**
     * The entered transaction should be processed
     * @param transaction
     * @return false when transaction could not be processed
     */
    public TransactionState processTransaction(Transaction transaction) {
        TransactionState retVal = TransactionState.FAILED;
        
        DBConnector.insertTransaction(transaction);
        
        if (debitAccount(transaction.getDebitor(), transaction.getAmount())) {
            if (creditAccount(transaction.getCreditor(), transaction.getAmount())){
                retVal = TransactionState.SUCCEEDED;
            }
            else {
                retVal = TransactionState.WAITING;
                if (mCentralConnection != null) mCentralConnection.sendTransactionToCentral(transaction);
            }
        }
        DBConnector.changeTransactionState(transaction, retVal);
        
        return retVal;
    }

    /**
     * Used to check if the given account is customer of this bank
     * @param accountNumber
     * @return true when the account is found else false
     */
    public boolean findAccount(String accountNumber) {
        return DBConnector.checkCustomerForAccount(accountNumber);
    }
    
    /**
     * Add a given amount to an account
     * @param accountNumber
     * @param amount
     * @return true when successfully added the money
     */
    public synchronized boolean creditAccount(String accountNumber, double amount) {
        boolean retVal = false;
        
        if (findAccount(accountNumber)) {
            double oldBalance = DBConnector.getAccountBalance(accountNumber);
            double newBalance = oldBalance + amount;
            DBConnector.setAccountBalance(accountNumber, newBalance);
            retVal = true;
        }
        
        return retVal;
    }
    
    /**
     * After making a transaction the account will be checked,
     * the necessary money will be retracted,
     * when this fails the transaction is declined.
     * @param accountNumber
     * @param amount
     * @return true when the money is retracted, false when the balance is to low
     */
    private synchronized boolean debitAccount(String accountNumber, double amount) {
        boolean retVal = false;
        
        if (findAccount(accountNumber)) {
            double oldBalance = DBConnector.getAccountBalance(accountNumber);
            double newBalance = oldBalance - amount;
            // check if this account has credit
            double credit = DBConnector.getAccountCredit(accountNumber);
            if (newBalance + credit >= 0) {
                DBConnector.setAccountBalance(accountNumber, newBalance);
                retVal = true;
            }
        }
        
        return retVal;
    }
    
    /**
     * Check all unprocessed transactions on the database and send them to the banking central.
     * When the banking central is unavailable the state will not be changed so it can later on be processed
     */
    private void sendUnprocessedTransactions() {
        Iterable<Transaction> transactionList = DBConnector.getUnprocessedTransactions();
        
        for(Transaction transaction : transactionList) {
            if (mCentralConnection != null) mCentralConnection.sendTransactionToCentral(transaction);
        }
    }
    
    /**
     * Performs checks if the given string is a valid IBAN code.
     * A valid IBAN consists of the first 3 a letter, and the next 10 of a number
     * @param IBAN The string to check
     * @return true when the given string is valid
     */
    public boolean isValidIBAN(String IBAN) {
        if (IBAN == null) return false;
        if (IBAN.length() != 13) return false;
        if (!IBAN.substring(0, 3).matches("[a-zA-Z]+")) return false;
        if (!IBAN.substring(3, 13).matches("[0-9]+")) return false;
            
        return true;
    }
}
