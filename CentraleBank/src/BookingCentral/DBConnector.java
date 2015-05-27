package BookingCentral;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Jeroen
 */
public class DBConnector {

    private static Connection connection = null;
    
    /**
     * Connect to the database.
     * @return If the connection was successful.
     */
    protected static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:BankServer.db");
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Disconnect from the database.
     * @return If the disconnecting was successful.
     */
    protected static boolean disconnect() {
        try {
            if (connection != null) connection.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * write a given transaction to the database for later processing
     * @param transaction
     */
    protected static void logTransaction(Transaction transaction) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * Change the transaction state of a given transaction
     * @param transaction
     * @param state 
     */
    protected static void changeTransactionState(Transaction transaction, TransactionState state) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * write logging to the database
     * @param severity
     * @param message 
     */
    protected void writeLogging(String severity, String message) {
        
    }
}
