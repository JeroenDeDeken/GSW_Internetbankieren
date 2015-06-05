package bankserver;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Jeroen
 */
public class Transaction {
    
    private long transactionId;
    private String debitor;
    private String creditor;
    private double amount;
    private String message;
    private TransactionState state;
    
    /**
     * Create a new transaction without identifier
     * @param debitor
     * @param creditor
     * @param amount
     * @param message 
     */
    public Transaction(String debitor, String creditor, double amount, String message) {
        this.transactionId = UUID.randomUUID().getMostSignificantBits();
        this.debitor = debitor;
        this.creditor = creditor;
        this.amount = amount;
        this.message = message;
    }
    
    /**
     * Create a new transaction with a specified transactionID
     * @param transactionId
     * @param debitor
     * @param creditor
     * @param amount
     * @param message 
     * @param state 
     */
    public Transaction(long transactionId, String debitor, String creditor, double amount, String message, TransactionState state) {
        this.transactionId = transactionId;
        this.debitor = debitor;
        this.creditor = creditor;
        this.amount = amount;
        this.message = message;
        this.state = state;
    }

    /**
     * Gets the unique identifier of this transaction.
     * @return a UUID representing only this transaction
     */
    public long getTransactionId() {
        return transactionId;
    }
    
    /**
     * Gets the account where the money should be withdrawn from
     * @return a string representing the IBAN nr
     */
    public String getDebitor() {
        return debitor;
    }

    /**
     * Gets the account where the money should be added to
     * @return a string representing the IBAN nr
     */
    public String getCreditor() {
        return creditor;
    }

    /**
     * Gets the amount of money handled by this transaction
     * @return a double representing the amount of money which is transacted
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the message supplied to this transaction
     * @return a string containing the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the current transaction state
     * @return a @{TransactionState} enum value
     */
    public TransactionState getState() {
        return state;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) return false;
        
        Transaction otherTransaction = (Transaction) o;
        boolean retval = false;
        
        if (this.transactionId == otherTransaction.transactionId
                && (this.debitor == null ? otherTransaction.debitor == null : this.debitor.equals(otherTransaction.debitor))
                && (this.creditor == null ? otherTransaction.creditor == null : this.creditor.equals(otherTransaction.creditor))
                && this.amount == otherTransaction.amount
                && (this.message == null ? otherTransaction.message == null : this.message.equals(otherTransaction.message))
                && (this.state == null ? otherTransaction.state == null : this.state.equals(otherTransaction.state))) {
            retval = true;
        }
        
        return retval;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.transactionId ^ (this.transactionId >>> 32));
        hash = 59 * hash + Objects.hashCode(this.debitor);
        hash = 59 * hash + Objects.hashCode(this.creditor);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 59 * hash + Objects.hashCode(this.message);
        return hash;
    }
}
