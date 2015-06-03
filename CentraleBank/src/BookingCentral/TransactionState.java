package BookingCentral;

/**
 *
 * @author Jeroen
 */
public enum TransactionState {
    /**
     * the transaction is not yet processed
     */
    PROCESSING(0),
    
    /**
     * the transaction is processed successfully
     */
    SUCCEEDED(1),
    
    /**
     * processing the transaction failed
     */
    FAILED(2);
    
    final int value;
    private TransactionState(int value) {
        this.value = value;
    }
}
