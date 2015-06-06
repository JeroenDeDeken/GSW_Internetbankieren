package bankserver;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jeroen
 */
@XmlRootElement
public enum TransactionState {
    /**
     * the initial state of a new transaction
     */
    INITIAL(0),
    
    /**
     * the transaction is waiting to be send to the banking central for processing
     */
    WAITING(1),
    
    /**
     * the transaction is send to the banking central to be processed there
     */
    SENDTOCENTRAL(2),
    
    /**
     * the transaction is processed successfully
     */
    SUCCEEDED(3),
    
    /**
     * processing the transaction failed
     */
    FAILED(4);
    
    final int value;
    private TransactionState(int value) {
        this.value = value;
    }
    
    public static TransactionState fromValue(int value) throws IllegalArgumentException {
         try{
              return TransactionState.values()[value];
         }catch( ArrayIndexOutOfBoundsException e ) {
              throw new IllegalArgumentException("Unknown enum value :"+ value);
         }
     }
}
