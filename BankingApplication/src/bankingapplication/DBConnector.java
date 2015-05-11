package bankingapplication;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
     * connect to the database
     */
    protected static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:AEX.db");
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * disconnect from the database
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
     * Creates the database with the tables.
     */
    public static void createDatabase() {
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
            
            String sql;
            if (!existingTables.contains("Accounts")) {
                sql = "CREATE TABLE `Accounts` (\n" +
                        "   `ID`            INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `AccountNr`     TEXT NOT NULL,\n" +
                        "   `CustomerName`  TEXT NOT NULL,\n" +
                        "   `Balance`       INTEGER NOT NULL,\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table Koersen created successfully");
            }
            if (!existingTables.contains("Transactions")) {
                sql = "CREATE TABLE `Transactions` (\n" +
                        "   `ID`            INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `Debitor`       TEXT NOT NULL,\n" +
                        "   `Creditor`      TEXT NOT NULL,\n" +
                        "   `Amount`        INTEGER NOT NULL,\n" +
                        "   `Message`       TEXT NOT NULL,\n" +
                        "   `State`         TEXT NOT NULL,\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table Koersen created successfully");
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    private static ResultSet executeSelect(String sql) throws SQLException {
        if (connection == null) connect();
        
        ResultSet result = null;
        try {
            Statement stmt = connection.createStatement();
            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw new SQLException(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (result != null) result.close();
        }
        
        return result;
    }
    
    /**
     * Check if a given accountNumber is connected to a customer
     * @param accountNumber
     * @return 
     */
    protected static boolean checkCustomerForAccount(String accountNumber) {
        boolean exists = false;
        
        String sql = "SELECT COUNT(*) FROM Accounts WHERE lower(AccountNr) = lower(" + accountNumber + ")";
        try {
            ResultSet result = executeSelect(sql);
            if (result.next()) {
                exists = (result.getInt(1) > 0);
            }
        } catch (SQLException ex) {
            System.err.println("checkCustomerForAccount: " + ex.getMessage());
        }        
        
        return exists;
    }
    
    /**
     * Get the balance for a given account
     * @param accountNumber
     * @return 
     */
    protected static double getAccountBalance(String accountNumber) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * Store a new balance for the given account
     * @param accountNumber
     * @param balance 
     */
    protected static void setAccountBalance(String accountNumber, double balance) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * write the data to the database which is needed for remembering a new account
     */
    protected static void createNewCustomerAccount() {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * remove all account data for a customer
     * @param accountNumber
     */
    protected static void removeCustomerAccount(String accountNumber) {
        if (connection == null) connect();

        throw new NotImplementedException();
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
     * get a list with all the transaction which still have the waiting state
     * @return 
     */
    protected static Collection<Transaction> getUnprocessedTransactions() {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
    
    /**
     * write logging to the database
     * @param severity
     * @param message 
     */
    protected static void writeLogging(String severity, String message) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
}
