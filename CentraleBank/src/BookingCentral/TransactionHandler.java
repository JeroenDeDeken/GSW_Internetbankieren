package BookingCentral;

import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 *
 * @author Jeroen
 */
public class TransactionHandler {
    
    private PrintWriter output;
    protected String bankName = null;

    /**
     * create a new transactionhandler
     * @param out output printwriter to send a message to the client
     */
    public TransactionHandler(PrintWriter out) {
        output = out;
    }

    /**
     * Process the input from the bank server,
     * this can be a new transaction or changes on an existing transaction
     * @param input
     */
    public void processInput(String input) {
        if (input.startsWith(Transaction.NAME_MARK)) {
            bankName = input.substring(Transaction.NAME_MARK.length());
        }
        else if (input.startsWith(Transaction.STATE_MARK)) { // existing transaction
            int index = input.indexOf(Transaction.SPLIT_STRING);
            String state = input.substring(Transaction.STATE_MARK.length(), index);
            TransactionState transactionState = TransactionState.valueOf(state);
            Transaction transaction = splitTransaction(input.substring(index));
            DBConnector.changeTransactionState(transaction, transactionState);
            returnTransactionState(transaction, transactionState);
        }
        else { // insert new transaction
            Transaction transaction = splitTransaction(input);
            bookTransaction(transaction);
        }
    }
    
    /**
     * Split an input string on the delimiters and create a transaction with it.
     * @param input
     * @return
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
                transactionId = Long.valueOf(string.substring(Transaction.ID_MARK.length()));
            }
            if (string.startsWith(Transaction.CREDITOR_MARK)) {
                creditor = string.substring(Transaction.CREDITOR_MARK.length());
            }
            if (string.startsWith(Transaction.DEBITOR_MARK)) {
                debitor = string.substring(Transaction.DEBITOR_MARK.length());
            }
            if (string.startsWith(Transaction.AMOUNT_MARK)) {
                amount = Double.valueOf(string.substring(Transaction.AMOUNT_MARK.length()));
            }
            if (string.startsWith(Transaction.MESSAGE_MARK)) {
                message = string.substring(Transaction.MESSAGE_MARK.length());
            }
        }
        
        return new Transaction(transactionId, debitor, creditor, amount, message);
    }
    
    
    /**
     * Write a transaction to the database for processing
     * @param transaction 
     * @return true when successfully added the transaction
     */
    private synchronized boolean bookTransaction(Transaction transaction) {
        return DBConnector.insertTransaction(transaction);
    }

    /**
     * Send a transaction to the client so it can be processed.
     * @param transaction to send
     * @return true when successfully send to the client
     */
    public boolean sendToBank(Transaction transaction) {
        try {
            output.println(transaction.toString());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * When a bank acknowledges a transaction is processed change the state
     * and send a message back to the original bank.
     * @param transaction
     * @param state 
     */
    private void returnTransactionState(Transaction transaction, TransactionState state) {
        CentralServerRunnable bankToSendTo = BankingCentral.findBankByAccountNumber(transaction.getDebitor());
        
        bankToSendTo.sendTransactionState(transaction, state);
    }
    
    /**
     * When another bank has processed a transaction coming from the bank this instance
     * is connected to the state of the transaction has to be processed.
     * @param transaction
     * @param state
     * @return true when succeeded
     */
    public boolean sendTransactionState(Transaction transaction, TransactionState state) {
        try {
            String message = Transaction.STATE_MARK + TransactionState.SUCCEEDED;
            message += transaction.toString();
            output.println(message);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
