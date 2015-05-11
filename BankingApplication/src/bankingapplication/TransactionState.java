package bankingapplication;

/**
 *
 * @author Jeroen
 */
public enum TransactionState {
    /**
     * the initial state of a new transaction
     */
    INITIAL,
    
    /**
     * the transaction is waiting to be send to the banking central for processing
     */
    WAITING,
    
    /**
     * the transaction is send to the banking central to be processed there
     */
    SENDTOCENTRAL,
    
    /**
     * the transaction is processed successfully
     */
    SUCCEEDED,
    
    /**
     * processing the transaction failed
     */
    FAILED,
}
