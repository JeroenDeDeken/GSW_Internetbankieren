package bankserver;

/**
 *
 * @author Jeroen
 */
public class Transaction {
    
    private String debitor;
    private String creditor;
    private double amount;
    private String message;
    
    public Transaction(String debitor, String creditor, double amount, String message) {
        this.debitor = debitor;
        this.creditor = creditor;
        this.amount = amount;
        this.message = message;
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
     * Gets the message suplied to this transaction
     * @return a string containing the message
     */
    public String getMessage() {
        return message;
    }
}
