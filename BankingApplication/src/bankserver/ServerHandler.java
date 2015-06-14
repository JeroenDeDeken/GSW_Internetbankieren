package bankserver;

import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 *
 * @author Jeroen
 */
class ServerHandler {
    
    private PrintWriter output;
    
    /**
     * create a new serverHandler object which takes care of the communication
     * @param out 
     */
    public ServerHandler(PrintWriter out) {
        output = out;
    }
    
    /**
     * Process the input coming from the central server.
     * This can be a new transaction to book or a change to the state of
     * an existing transaction.
     * @param input 
     */
    public void processInput(String input) {
        if (input.startsWith(Transaction.STATE_MARK)) { // existing transaction
            int index = input.indexOf(Transaction.SPLIT_STRING);
            String state = input.substring(3, index);
            TransactionState transactionState = TransactionState.valueOf(state);
            Transaction transaction = splitTransaction(input.substring(index));
            DBConnector.changeTransactionState(transaction, transactionState);
        }
        else { // new transaction
            Transaction transaction = splitTransaction(input);
            if (bookTransaction(transaction)) {
                sendSucces(transaction);
            }
        }
    }

    /**
     * Send a new transaction to the central server for further processing.
     * @param transaction 
     */
    protected void sendTransaction(Transaction transaction) {
        output.println(transaction.toString());
    }
    
    /**
     * Split a message string and create a transaction object from it.
     * @param input
     * @return the transaction represented by the input string
     */
    protected Transaction splitTransaction(String input) {
        String[] strings = input.split(Pattern.quote(Transaction.SPLIT_STRING));
        
        long transactionId = 0;
        String debitor = "";
        String creditor = "";
        double amount = 0;
        String message = "";
        
        for (String string : strings) {
            if (string.startsWith(Transaction.ID_MARK)) {
                transactionId = Long.valueOf(string.substring(3));
            }
            if (string.startsWith(Transaction.CREDITOR_MARK)) {
                creditor = string.substring(3);
            }
            if (string.startsWith(Transaction.DEBITOR_MARK)) {
                debitor = string.substring(3);
            }
            if (string.startsWith(Transaction.AMOUNT_MARK)) {
                amount = Double.valueOf(string.substring(3));
            }
            if (string.startsWith(Transaction.MESSAGE_MARK)) {
                message = string.substring(3);
            }
        }
        
        return new Transaction(transactionId, debitor, creditor, amount, message);
    }
    
    /**
     * Book a transaction on the database and credit the account with the amount
     * @param transaction
     * @return 
     */
    private boolean bookTransaction(Transaction transaction) {
        DBConnector.insertTransaction(transaction);
        
        boolean retval = false;
        
        if (creditAccount(transaction.getCreditor(), transaction.getAmount())){
            DBConnector.changeTransactionState(transaction, TransactionState.SUCCEEDED);
            retval = true;
        }
        
        return true;
    }
    
    /**
     * Find the account for the given account number and add the amount to the balance
     * @param accountNumber
     * @param amount
     * @return 
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
     * Find the account for the given account number
     * @param accountNumber
     * @return true when the account is found
     */
    public boolean findAccount(String accountNumber) {
        return DBConnector.checkCustomerForAccount(accountNumber);
    }

    /**
     * Send back a message to the central when a transaction is processed successfully
     * @param transaction 
     */
    private void sendSucces(Transaction transaction) {
        String message = Transaction.STATE_MARK + TransactionState.SUCCEEDED;
        message += transaction.toString();
        output.println(message);
    }

    /**
     * Send the name of this bank to the central server
     * @param bankName 
     */
    protected void sendBankName(String bankName) {
        System.out.println("Handler: Send bankname " + bankName);
        String message = Transaction.NAME_MARK + bankName;
        output.println(message);
    }
}
