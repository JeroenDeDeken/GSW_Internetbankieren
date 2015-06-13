package bankserver;

import java.io.PrintWriter;

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
        String[] strings = input.split("SPLIT");
        
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
        String message = "<S>" + TransactionState.SUCCEEDED;
        message += transaction.toString();
        output.println(message);
    }
}
