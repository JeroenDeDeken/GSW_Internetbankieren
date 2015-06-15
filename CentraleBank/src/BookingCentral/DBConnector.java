package BookingCentral;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            connection = DriverManager.getConnection("jdbc:sqlite:CentralServer.db");
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
     * When testing we delete the database after the tests to make a clean testrun every time
     */
    protected static void removeDatabase() {
        disconnect();
        
        Path path = FileSystems.getDefault().getPath("..\\CentraleBank", "CentralServer.db");
        System.out.println("Delete file " + path.toString());
        try {
            Files.deleteIfExists(path);
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    /**
     * Create the database when it is not existing
     */
    protected static void createDatabase() {
        if (connection == null) connect();
        
        try (Statement stmt = connection.createStatement()) {
            List<String> existingTables = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getTables(null, null, "%", null)) {
                while (resultSet.next()) {
                    existingTables.add(resultSet.getString(3));
                    System.out.println("Database table found: " + resultSet.getString(3));
                }
            }
            
            String sql, table;
            
            /**
             * Create table Transactions
             */
            table = "Transactions"; //Transaction is a reservered keyword
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `DbID`          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `TransactionID` LONG NOT NULL,\n" +
                        "   `debitIBAN`     TEXT NOT NULL,\n" +
                        "   `creditIBAN`    TEXT NOT NULL,\n" +
                        "   `Amount`        DECIMAL NOT NULL,\n" +
                        "   `Message`       TEXT,\n" +
                        "   `State`         INTEGER NOT NULL\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }
    
    /**
     * write a given transaction to the database for later processing
     * @param transaction
     * @return `true when successful
     */
    protected static boolean insertTransaction(Transaction transaction) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }
 
        String sql = "INSERT INTO Transactions (TransactionID, debitIBAN, creditIBAN, Amount, Message, State) VALUES (?,?,?,?,?,?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, transaction.getTransactionId());
            stmt.setString(2, transaction.getDebitor());
            stmt.setString(3, transaction.getCreditor());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setString(5, transaction.getMessage());
            stmt.setInt(6, TransactionState.INITIAL.value);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        
        return true;
    }
    
    /**
     * Change the transaction state of a given transaction
     * @param transaction
     * @param state 
     * @return true when successful
     */
    protected static boolean changeTransactionState(Transaction transaction, TransactionState state) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }
 
        String sql = "UPDATE Transactions SET State = ? WHERE TransactionID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, state.value);
            stmt.setLong(2, transaction.getTransactionId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        
        return true;
    }
    
    /**
     * get a list with all the transaction which still have the processing state
     * @return null when failed
     */
    protected static Set<Transaction> getUnprocessedTransactions() {
        if (connection == null) {
            if (!connect()) {
                return null;
            }
        }
 
        ResultSet result = null;
        Set<Transaction> returnValue = new HashSet<>();

        String sql = "SELECT TransactionID, debitIBAN, creditIBAN, Amount, Message FROM Transactions WHERE State = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, TransactionState.INITIAL.value);
            result = stmt.executeQuery();
            
            while (result.next()) {
                long transactionId = result.getLong("TransactionID");
                String debitIBAN = result.getString("debitIBAN");
                String creditIBAN = result.getString("creditIBAN");
                double amount = result.getDouble("Amount");
                String message = result.getString("Message");
                
                Transaction thisTransaction = new Transaction(transactionId, debitIBAN, creditIBAN, amount, message);
                returnValue.add(thisTransaction);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
                result = null;
            }
        }

        return returnValue;
    }
}
