package BookingCentral;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Jeroen
 */
public class BankingCentral {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    /**
     * Used to test the connection between bank and central
     * @return a enum code representing the state of the connection
     */
    private HeartbeatCode bankHeartBeat() {
        throw new NotImplementedException();
    }
    
    /**
     * Check the database and process unfinished transactions
     */
    private void ProcessTransactions() {
        throw new NotImplementedException();
    }
    
    /**
     * Write a transaction to the database for processing
     * @param transaction 
     */
    public void bookTransaction(Transaction transaction) {
        throw new NotImplementedException();
    }
    
    /**
     * Check the database for the state of a transaction
     * @param transaction
     */
    public TransactionState findTransactionState(Transaction transaction) {
        throw new NotImplementedException();
    }
    
    /**
     * Find the bank which owns a given account number
     * @param accountNumber
     */
    private void findBankByAccountNumber(String accountNumber) {
        throw new NotImplementedException();
    }
}
