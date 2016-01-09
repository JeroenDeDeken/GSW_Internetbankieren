package BookingCentral;

import java.util.Objects;

/**
 *
 * @author Jeroen
 */
public class Transaction {
    
    public static final String SPLIT_STRING = "|"; //Possibly exchange
    public static final String ID_MARK = "<T>";
    public static final String CREDITOR_MARK = "<C>";
    public static final String DEBITOR_MARK = "<D>";
    public static final String AMOUNT_MARK = "<A>";
    public static final String MESSAGE_MARK = "<M>";
    public static final String STATE_MARK = "<S>";
    public static final String NAME_MARK = "<N>";
    
    private final long transactionId;
    private final String debitor;
    private final String creditor;
    private final double amount;
    private final String message;
    
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
    
    @Override
    public String toString() {
        String retval = new String();
        retval += SPLIT_STRING + ID_MARK + String.valueOf(transactionId);
        retval += SPLIT_STRING + CREDITOR_MARK + creditor;
        retval += SPLIT_STRING + DEBITOR_MARK + debitor;
        retval += SPLIT_STRING + AMOUNT_MARK + String.valueOf(amount);
        retval += SPLIT_STRING + MESSAGE_MARK + message;
        return retval;
    }
    
    /**
     * Returns if the objects are the same
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) {
            return false;
        }
        
        Transaction otherTransaction = (Transaction) o;
        boolean retval = false;
        
        if (this.transactionId == otherTransaction.transactionId
                && (this.debitor == null ? otherTransaction.debitor == null : this.debitor.equals(otherTransaction.debitor))
                && (this.creditor == null ? otherTransaction.creditor == null : this.creditor.equals(otherTransaction.creditor))
                && this.amount == otherTransaction.amount
                && (this.message == null ? otherTransaction.message == null : this.message.equals(otherTransaction.message))) {
            retval = true;
        }
        
        return retval;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.transactionId ^ (this.transactionId >>> 32));
        hash = 97 * hash + Objects.hashCode(this.debitor);
        hash = 97 * hash + Objects.hashCode(this.creditor);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.message);
        return hash;
    }
}
