package bankserver;

import javax.xml.ws.Endpoint;

/**
 *
 * @author Jeroen
 */
public class BankServer {

    private static final String soapUrl = "http://localhost:8080/BankServer";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DBConnector.createDatabase();
        launchSoapClientService();
    }
    
    private static void launchSoapClientService() {
        try {
            Endpoint.publish(soapUrl, ClientService.getInstance());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    /**
     * The entered transaction should be processed
     * @param transaction
     * @return false when transaction could not be processed
     */
    private boolean processTransaction(Transaction transaction) {
        boolean retVal = false;
        
        DBConnector.logTransaction(transaction);
        
        if (debitAccount(transaction.getDebitor(), transaction.getAmount())) {
            if (creditAccount(transaction.getCreditor(), transaction.getAmount())){
                DBConnector.changeTransactionState(transaction, TransactionState.SUCCEEDED);
            }
            else {
                DBConnector.changeTransactionState(transaction, TransactionState.WAITING);
                sendTransactionToCentral(transaction);
            }
            retVal = true;
        }
        else {
            DBConnector.changeTransactionState(transaction, TransactionState.FAILED);
        }

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
    public boolean creditAccount(String accountNumber, double amount) {
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
    private boolean debitAccount(String accountNumber, double amount) {
        boolean retVal = false;
        
        if (findAccount(accountNumber)) {
            double oldBalance = DBConnector.getAccountBalance(accountNumber);
            double newBalance = oldBalance - amount;
            // TODO: IMPLEMENT POSSIBLE NEGATIVE VALUES
            if (newBalance >= 0) {
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
            sendTransactionToCentral(transaction);
        }
    }
    
    /**
     * When the credit account is no customer of this bank send the transaction to 
     * the banking central for processing
     * @param transaction 
     */
    private void sendTransactionToCentral(Transaction transaction) {
        boolean sendSucceeded = false;
        
        // send the transaction to the banking central
        if (sendSucceeded) {
            DBConnector.changeTransactionState(transaction, TransactionState.SENDTOCENTRAL);
        }
        else {
            DBConnector.changeTransactionState(transaction, TransactionState.WAITING);
        }
    }
}
