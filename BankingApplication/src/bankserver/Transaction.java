package bankserver;

import java.util.Objects;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Jeroen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Transaction")
public class Transaction {
    
    public static final String SPLIT_STRING = "|"; //Possibly exchange
    public static final String ID_MARK = "<T>";
    public static final String CREDITOR_MARK = "<C>";
    public static final String DEBITOR_MARK = "<D>";
    public static final String AMOUNT_MARK = "<A>";
    public static final String MESSAGE_MARK = "<M>";
    public static final String STATE_MARK = "<S>";
    
    private long transactionId;
    private String debitor;
    private String creditor;
    private double amount;
    private String message;
    private TransactionState state;
    
    private Transaction() {
        
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
    @XmlAttribute
    public long getTransactionId() {
        return transactionId;
    }
    
    /**
     * Gets the account where the money should be withdrawn from
     * @return a string representing the IBAN nr
     */
    @XmlAttribute
    public String getDebitor() {
        return debitor;
    }

    /**
     * Gets the account where the money should be added to
     * @return a string representing the IBAN nr
     */
    @XmlAttribute
    public String getCreditor() {
        return creditor;
    }

    /**
     * Gets the amount of money handled by this transaction
     * @return a double representing the amount of money which is transacted
     */
    @XmlAttribute
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the message supplied to this transaction
     * @return a string containing the message
     */
    @XmlAttribute
    public String getMessage() {
        return message;
    }

    /**
     * Gets the current transaction state
     * @return a @{TransactionState} enum value
     */
    @XmlAttribute
    public TransactionState getState() {
        return state;
    }
    
    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        
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

//    @Override
//    public String toString() {
//        return String.format("Debitor: %s, Creditor: %s, Amount: %f, Description: %s", debitor, creditor, amount, message);
//    }
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
}
