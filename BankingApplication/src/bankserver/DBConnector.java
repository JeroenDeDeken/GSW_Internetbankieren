package bankserver;

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
            
            String sql, table;
            table = "Account";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `AccountID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `IBAN`      TEXT NOT NULL,\n" +
                        "   `Balance`   DECIMAL NOT NULL,\n" +
                        "   `Credit`    DECIMAL NOT NULL\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            table = "Transactions"; //Transaction is a reservered keyword
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `TransactionID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `debitIBAN`     TEXT NOT NULL,\n" +
                        "   `creditIBAN`    TEXT NOT NULL,\n" +
                        "   `Amount`        DECIMAL NOT NULL,\n" +
                        "   `Message`       TEXT,\n" +
                        "   `State`         INTEGER NOT NULL\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            table = "Customer";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `CustomerID`  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `Username`    TEXT NOT NULL UNIQUE,\n" +
                        "   `Password`    TEXT NOT NULL,\n" +
                        "   `Name`        TEXT NOT NULL,\n" +
                        "   `Residence`   TEXT\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            table = "Session";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `SessionID`   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "   `Timestamp`   DATE DEFAULT CURRENT_DATE,\n" +
                        "   `CustomerID`  INTEGER NOT NULL REFERENCES Customer(CustomerID)\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            table = "CustomerAccount";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `CustomerID`  INTEGER NOT NULL REFERENCES Customer(CustomerID),\n" +
                        "   `AccountID`   INTEGER NOT NULL REFERENCES Account(AccountID)\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            table = "AccountTransaction";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                        "   `AccountID`     INTEGER NOT NULL REFERENCES Account(AccountID),\n" +
                        "   `TransactionID` INTEGER NOT NULL REFERENCES Transactions(TransactionID)\n" +
                        ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    private static ResultSet executeSelect(String sql) throws SQLException {
        if (connection == null)
            if (!connect())
                return null;
        
        ResultSet result = null;
        try {
            Statement stmt = connection.createStatement();
            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (result != null) result.close();
        }
        
        return result;
    }
    
    /**
     * Logs a user in by creating a session row in the database.
     * @param username The username of the user to log in.
     * @param password The password of the user to log in.
     * @return The ID of the session created, if the user is not found it will return -1, when a (database) error happened returns null.
     */
    public static Integer loginUser(String username, String password) {        
        if (connection == null)
            if (!connect())
                return -1;
        
        ResultSet result = null;
        String sql;
        
        Integer customerID = null, sessionID = null;
        
        //Check if the user exists
        sql = "SELECT CustomerID FROM Customer WHERE Username = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            result = stmt.executeQuery(sql);
            
            if (result.next()) {
                customerID = result.getInt(1);
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
        
        if (result == null) return null; //Error happened
        if (customerID != null) return -1; //User not found
        result = null;
        
        //Create the user session
        sql = "INSERT INTO Session (CustomerID) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerID);
            sessionID = stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return sessionID;
    }
    
    /**
     * Registers a user with the given parameters.
     * @param username
     * @param password
     * @param residence
     * @return The customerID of the user registered, null on a exception and -1 when the user already exists.
     */
    public static Integer registerUser(String username, String password, String residence) {
        Integer customerID = null;
        boolean exists = false;
        ResultSet result = null;
        
        //Check if the user exists
        String sql = "SELECT COUNT(*) FROM Customer WHERE Username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            result = stmt.executeQuery(sql);
            
            if (result.next()) {
                exists = (result.getInt(1) > 0);
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
        
        if (result == null) return null; //Error happened
        if (exists) return -1; //User already exists
        result = null;
        
        //Create the customer
        sql = "INSERT INTO Customer (Username, Password, Residence) VALUES (?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, residence);
            customerID = stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return customerID;
    }
    
    /**
     * Check if a given accountNumber is connected to a customer
     * @param accountNumber
     * @return 
     */
    protected static boolean checkCustomerForAccount(String accountNumber) {
        boolean exists = false; //TODO update with new database set
        //TODO LET OP SQL INJECTIE!!!!!
//        String sql = "SELECT COUNT(*) FROM Accounts WHERE lower(AccountNr) = lower(" + accountNumber + ")";
//        try {
//            ResultSet result = executeSelect(sql);
//            if (result.next()) {
//                exists = (result.getInt(1) > 0);
//            }
//        } catch (SQLException ex) {
//            System.err.println("checkCustomerForAccount: " + ex.getMessage());
//        }        
//        
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
