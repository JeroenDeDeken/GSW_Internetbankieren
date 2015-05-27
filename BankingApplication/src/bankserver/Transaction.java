package bankserver;

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
     */
    public Transaction(long transactionId, String debitor, String creditor, double amount, String message) {
        this.transactionId = transactionId;
        this.debitor = debitor;
        this.creditor = creditor;
        this.amount = amount;
        this.message = message;
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
}
