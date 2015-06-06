package BookingCentral;

/**
 *
 * @author Jeroen
 */
public enum TransactionState {
    /**
     * the transaction is not yet processed
     */
    INITIAL(0),
    
    /**
     * the transaction is processed successfully
     */
    SUCCEEDED(1),
    
    /**
     * processing the transaction failed
     */
    FAILED(2),
    
    /**
     * The transaction is send to the bank but not yet acknowledged
     */
    SENDTOBANK(3);
    
    final int value;
    private TransactionState(int value) {
        this.value = value;
    }
}
