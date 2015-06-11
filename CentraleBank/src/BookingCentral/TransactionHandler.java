package BookingCentral;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Jeroen
 */
public class TransactionHandler {
    
    private PrintWriter output;

    public TransactionHandler(PrintWriter out) {
        output = out;
    }

    public void processInput(String input) {
        if (input.startsWith("<S>")) { // existing transaction
            int index = input.indexOf("|");
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
    
    protected Transaction splitTransaction(String input) {
        String[] strings = input.split("|");
        
        long transactionId = 0;
        String debitor = "";
        String creditor = "";
        double amount = 0;
        String message = "";
        
        for (String string : strings) {
            if (string.startsWith("<T>")) {
                transactionId = Long.valueOf(string.substring(3));
            }
            if (string.startsWith("<C>")) {
                creditor = string.substring(3);
            }
            if (string.startsWith("<D>")) {
                debitor = string.substring(3);
            }
            if (string.startsWith("<A>")) {
                amount = Double.valueOf(string.substring(3));
            }
            if (string.startsWith("<M>")) {
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
    public static boolean bookTransaction(Transaction transaction) {
        return DBConnector.insertTransaction(transaction);
    }

    /**
     * Send a transaction to the client so it can be processed.
     * @param transaction to send
     * @param out output printwriter to send a message to the client
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
