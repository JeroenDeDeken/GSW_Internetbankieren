package bankserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    public static final String bankCode = "GSW";
    private static final String soapUrl = "http://localhost:8080/BankServer";

    // socket connection
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerHandler handler;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        DBConnector.createDatabase();
        launchSoapClientService();
        
        try {
            Socket socket = new Socket("localhost", 4444);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            
            String inputLine;
            handler = new ServerHandler(out);
            
            while ((inputLine = in.readLine()) != null) {
                handler.processInput(inputLine);
            }
            
            out.close();
            in.close();
            socket.close();
        }
        catch (Exception e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }
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
    public TransactionState processTransaction(Transaction transaction) {
        TransactionState retVal = TransactionState.FAILED;
        
        DBConnector.insertTransaction(transaction);
        
        if (debitAccount(transaction.getDebitor(), transaction.getAmount())) {
            if (creditAccount(transaction.getCreditor(), transaction.getAmount())){
                retVal = TransactionState.SUCCEEDED;
            }
            else {
                retVal = TransactionState.WAITING;
                sendTransactionToCentral(transaction);
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
        try {
            handler.sendTransaction(transaction);
            sendSucceeded = true;
        }
        catch (Exception e) {
            System.out.println("error sending transaction to server");
        }
        
        if (sendSucceeded) {
            DBConnector.changeTransactionState(transaction, TransactionState.SENDTOCENTRAL);
        }
        else {
            DBConnector.changeTransactionState(transaction, TransactionState.WAITING);
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
