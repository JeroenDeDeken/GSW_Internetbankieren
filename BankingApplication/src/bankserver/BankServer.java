package bankserver;

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
    public static final String BANKING_CODE = "GSW";
    private static final String SOAP_URL = "http://localhost:8080/BankServer";
    
    private static String bankName = "RABO";
    
    private static CentralConnection mCentralConnection;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBConnector.createDatabase();
        launchSoapClientService();
        mCentralConnection = new CentralConnection(bankName);
    }
    
    private static void launchSoapClientService() {
        try {
            Endpoint.publish(SOAP_URL, ClientService.getInstance());
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
    
    public boolean isValidIBAN(String IBAN) {
        if (IBAN == null) return false;
        if (IBAN.length() != 13) return false;
        if (!IBAN.substring(0, 3).matches("[a-zA-Z]+")) return false;
        if (!IBAN.substring(3, 13).matches("[0-9]+")) return false;
        
        return true;
    }
}
