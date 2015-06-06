package BookingCentral;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Jeroen
 */
public class BankingCentral {

    private static Set<String> connectedBanks;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connectedBanks = new HashSet<>();
        
        // Setup the connection for the client banks to connect to.
        // The clients can add transactions to the central database
        // The central server can send transactions to the clients for processing
        
        // Set a timer to proces the waiting transactions every minute.
        Timer procesTransactionsTimer = new Timer();
        procesTransactionsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ProcessTransactions();
            }
        }, 1000,60000);
    }
    
    /**
     * Check the database and process unfinished transactions
     */
    private static void ProcessTransactions() {
        Iterable<Transaction> transactionList = DBConnector.getUnprocessedTransactions();
        
        for(Transaction transaction : transactionList) {
            String bankToSendTo = findBankByAccountNumber(transaction.getCreditor());
            boolean succeeded = sendTransactionToBank(bankToSendTo, transaction);
            if (succeeded) {
                DBConnector.changeTransactionState(transaction, TransactionState.SENDTOBANK);
            }
        }
    }
    
    /**
     * Write a transaction to the database for processing
     * @param transaction 
     * @return true when successfully added the transaction
     */
    public static boolean bookTransaction(Transaction transaction) {
        return DBConnector.insertTransaction(transaction);
    }
    
//    /**
//     * Check the database for the state of a transaction
//     * @param transaction
//     */
//    public static TransactionState findTransactionState(Transaction transaction) {
//        throw new NotImplementedException();
//    }
    
    /**
     * Find the bank which owns a given account number
     * @param accountNumber
     */
    private static String findBankByAccountNumber(String accountNumber) {
        String retval = "";
        for (String bank : connectedBanks) {
            if (accountNumber.contains(bank)) {
                retval = bank;
                break;
            }
        }
        return retval;
    }

    /**
     * Send the given transaction to a bank for further processing.
     * @param bankToSendTo
     * @param transaction 
     */
    private static boolean sendTransactionToBank(String bankToSendTo, Transaction transaction) {
        throw new NotImplementedException();
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
