package BookingCentral;

import java.io.PrintWriter;
import java.util.regex.Pattern;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Jeroen
 */
public class TransactionHandler {
    
    private PrintWriter output;

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
        if (input.startsWith(Transaction.STATE_MARK)) { // existing transaction
            int index = input.indexOf(Transaction.SPLIT_STRING);
            String state = input.substring(3, index);
            TransactionState transactionState = TransactionState.valueOf(state);
            Transaction transaction = splitTransaction(input.substring(index));
            DBConnector.changeTransactionState(transaction, transactionState);
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
     * Write a transaction to the database for processing
     * @param transaction 
     * @return true when successfully added the transaction
     */
    private boolean bookTransaction(Transaction transaction) {
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
    private static void returnTransactionState(Transaction transaction, TransactionState state) {
        throw new NotImplementedException();
    }
}
