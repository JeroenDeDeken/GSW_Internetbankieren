package BookingCentral;

import java.sql.Connection;

/**
 *
 * @author Jeroen
 */
public class DBConnector {

    private Connection connection;
    
    public DBConnector() {
        
    }
    
    /**
     * connect to the database
     */
    protected void connect() {
        
    }
    
    /**
     * disconnect from the database
     */
    protected void disconnect() {
        
    }
    
    /**
     * write a given transaction to the database for later processing
     * @param transaction
     */
    protected void logTransaction(Transaction transaction) {
        
    }
    
    /**
     * write logging to the database
     * @param severity
     * @param message 
     */
    protected void writeLogging(String severity, String message) {
        
    }
}
