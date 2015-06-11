package BookingCentral;

import java.io.*;
import java.net.ServerSocket;
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

    private static Set<CentralServerRunnable> connectedBanks;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Set a timer to proces the waiting transactions every minute.
        Timer procesTransactionsTimer = new Timer();
        procesTransactionsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ProcessTransactions();
            }
        }, 1000,60000);
        
        connectedBanks = new HashSet<>();
        
        // Setup the connection for the client banks to connect to.
        // The clients can add transactions to the central database
        // The central server can send transactions to the clients for processing
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        while (listening) {
            // create new Thread which runs Multiserverrunnable
            CentralServerRunnable bank = new CentralServerRunnable(serverSocket.accept()); 
            Thread t = new Thread(bank);
            connectedBanks.add(bank);
            // start Thread
            t.start();
        }
        serverSocket.close();
    }
    
    /**
     * Check the database and process unfinished transactions
     */
    private static void ProcessTransactions() {
        Iterable<Transaction> transactionList = DBConnector.getUnprocessedTransactions();
        
        for(Transaction transaction : transactionList) {
            CentralServerRunnable bankToSendTo = findBankByAccountNumber(transaction.getCreditor());
            if (bankToSendTo != null) {
                if (bankToSendTo.alive) {
                    boolean succeeded = bankToSendTo.sendTransaction(transaction);
                    if (succeeded) {
                        DBConnector.changeTransactionState(transaction, TransactionState.SENDTOBANK);
                    }
                }
                else {
                    connectedBanks.remove(bankToSendTo);
                }

            }
        }
    }
    
    /**
     * Find the bank which owns a given account number
     * @param accountNumber
     */
    private static CentralServerRunnable findBankByAccountNumber(String accountNumber) {
        CentralServerRunnable retval = null;
        for (CentralServerRunnable bank : connectedBanks) {
            if (accountNumber.contains(bank.bankName)) {
                retval = bank;
                break;
            }
        }
        return retval;
    }
}
