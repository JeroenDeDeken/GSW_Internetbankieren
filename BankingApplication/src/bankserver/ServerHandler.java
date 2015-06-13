package bankserver;

import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 *
 * @author Jeroen
 */
class ServerHandler {
    
    private PrintWriter output;
    
    public ServerHandler(PrintWriter out) {
        output = out;
    }
    
    public void processInput(String input) {
        Transaction transaction = splitTransaction(input);
        if (bookTransaction(transaction)) {
            sendSucces(transaction);
        }
    }

    protected void sendTransaction(Transaction transaction) {
        output.println(transaction.toString());
    }
    
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
    
    private boolean bookTransaction(Transaction transaction) {
        DBConnector.insertTransaction(transaction);
        
        boolean retval = false;
        
        if (creditAccount(transaction.getCreditor(), transaction.getAmount())){
            DBConnector.changeTransactionState(transaction, TransactionState.SUCCEEDED);
            retval = true;
        }
        
        return true;
    }
    
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
    
    public boolean findAccount(String accountNumber) {
        return DBConnector.checkCustomerForAccount(accountNumber);
    }

    private void sendSucces(Transaction transaction) {
        String message = Transaction.STATE_MARK + TransactionState.SUCCEEDED;
        message += transaction.toString();
        output.println(message);
    }
}
