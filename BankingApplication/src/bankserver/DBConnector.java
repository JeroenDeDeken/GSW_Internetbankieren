package bankserver;

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
    public static boolean connect() {
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
    public static boolean disconnect() {
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
    public static void removeDatabase() {
        disconnect();
        //TODO cleanup, do not use a static path
        Path path = FileSystems.getDefault().getPath("D:\\Git\\GSW_Internetbankieren\\BankingApplication", "BankServer.db");
        System.out.println("Delete file " + path.toString());
        try {
            Files.deleteIfExists(path);
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
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
            
            /**
             * Create table Account
             */
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
            
            /**
             * Create table Customer
             */
            table = "Customer";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                      "   `CustomerID`  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                      "   `Username`    TEXT NOT NULL UNIQUE,\n" +
                      "   `Password`    TEXT NOT NULL,\n" +
//                      "   `Name`        TEXT NOT NULL,\n" +
                      "   `Residence`   TEXT\n" +
                      ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            
            /**
             * Create table Session
             */
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
            
            /**
             * Create table CustomerAccount
             */
            table = "CustomerAccount";
            if (!existingTables.contains(table)) {
                sql = "CREATE TABLE `" + table + "` (\n" +
                      "   `CustomerID`  INTEGER NOT NULL REFERENCES Customer(CustomerID),\n" +
                      "   `AccountID`   INTEGER NOT NULL REFERENCES Account(AccountID)\n" +
                      ");";
                stmt.executeUpdate(sql);
                System.out.println("Table " + table + " created successfully");
            }
            
            /**
             * Create table AccountTransaction
             */
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
                return null;
        
        ResultSet result = null;
        String sql;
        
        Integer customerID = null, sessionID = null;
        
        //Check if the user exists
        sql = "SELECT CustomerID FROM Customer WHERE Username = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            result = stmt.executeQuery();
            
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
            }
        }
        
        if (result == null) return null; //Error happened
        if (customerID == null) return -1; //User not found
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
     * Logs out the user logged in with the sessionID.
     * @param sessionID The ID of the session the user is logged in with.
     * @return If the deletion was a success.
     */
    public static boolean logoutUser(int sessionID) {
        String sql = "DELETE FROM Session WHERE SessionID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionID);
            
            stmt.executeQuery();
            
            return true;
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
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
            result = stmt.executeQuery();
            
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
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                customerID = rs.getInt(1);
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
            }
        }
        
        return customerID;
    }
    
    /**
     * Check if a given accountNumber is connected to a customer
     * @param accountNumber
     * @return 
     */
    public static boolean checkCustomerForAccount(String accountNumber) {
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
     * @param accountIBANNumber
     * @return 
     */
    public static double getAccountBalance(String accountIBANNumber) {
        if (connection == null) {
            if (!connect()) {
                return -99999999;
            }
        }

        ResultSet result = null;
        double returnValue = -88888888;
        String sql = "SELECT Balance FROM Account WHERE IBAN = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountIBANNumber);
            result = stmt.executeQuery();
            
            if (result.next()) {
                returnValue = result.getDouble(1);
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
            }
        }

        return returnValue;
    }
    
    /**
     * Store a new balance for the given account
     * @param accountIBANNumber
     * @param balance
     * @return true when successful
     */
    public static boolean setAccountBalance(String accountIBANNumber, double balance) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String sql = "UPDATE Account SET Balance = ? WHERE IBAN = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setString(2, accountIBANNumber);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }
    
//    /**
//     * write the data to the database which is needed for remembering a new account
//     * @param IBANNumber
//     * @return 
//     */
//    public static boolean createNewCustomerAccount(String IBANNumber) {
//        if (connection == null) {
//            if (!connect()) {
//                return false;
//            }
//        }
//
//        String sql = "INSERT INTO Account (IBAN, Balance, Credit) VALUES (?, 0, 0);";
//        
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, IBANNumber);
//            stmt.executeUpdate();
//            stmt.close();
//            
//        } catch (SQLException e) {
//            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
//            return false;
//        }
//
//        return true;
//    }
    
    /**
     * Write the data to the database which is needed for remembering a new account.
     * @param account The Account to create.
     * @return If the creation was successful.
     */
    public static boolean createNewCustomerAccount(Account account) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }
            
        int accountID = -1;
        
        String sql = "INSERT INTO Account (IBAN, Balance, Credit) VALUES (?,?,?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "generating...");
            stmt.setDouble(2, account.getBalance());
            stmt.setDouble(3, account.getCredit());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                accountID = rs.getInt(1);
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        
        account.setAccountID(accountID);
        account.setIBAN(String.format("%s%010d", BankServer.BANKING_CODE, accountID));
        
        sql = "UPDATE Account SET IBAN = ? WHERE AccountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getIBAN());
            stmt.setInt(2, accountID);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    
    public static boolean connectCustomerAccount(int accountID, int customerID) {
        String sql = "INSERT INTO CustomerAccount (CustomerID, AccountID) VALUES (?, ?);";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, accountID);
            stmt.execute();
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        
        return false;
    }

    /**
     * Get the customer ID for the given session ID.
     * @param sessionID
     * @return null when the session is not found.
     */
    public static Integer getUserIDForSessionID(int sessionID) {
        Integer customerID = null;
        ResultSet result;
        String sql = "SELECT CustomerID FROM Session WHERE SessionID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionID);
            result = stmt.executeQuery();
            
            if (result.next()) {
                customerID = result.getInt(1);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return customerID;
    }
    
    /**
     * Returns a @{link List} of @{link Account accounts} of the given customer.
     * @param customerID The ID of the customer.
     * @return null on error.
     */
    public static List<Account> getAccountsForCustomerID(int customerID) {
        List<Account> accounts = null;
        ResultSet result;
        String sql = "SELECT AccountID, IBAN, Balance, Credit FROM Account WHERE AccountID = (SELECT AccountID FROM CustomerAccount WHERE CustomerID = ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            result = stmt.executeQuery();
            
            accounts = new ArrayList<>();
            while (result.next()) {
                accounts.add(new Account(result.getInt(1), result.getString(2), result.getDouble(3), result.getDouble(4)));
            }
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return accounts;
    }
    
    /**
     * Write a given transaction to the database for later processing.
     * @param transaction
     * @return true when successful
     */
    public static boolean insertTransaction(Transaction transaction) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }
 
        String sql = "INSERT INTO Transactions (TransactionID, DebitIBAN, CreditIBAN, Amount, Message, State) VALUES (?,?,?,?,?,?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
     * Change the transaction state of a given transaction.
     * @param transaction
     * @param state
     * @return true when successful
     */
    public static boolean changeTransactionState(Transaction transaction, TransactionState state) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }
 
        String sql = "UPDATE Transactions SET State = ? WHERE TransactionID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
     * Get a list with all the transaction which still have the waiting state.
     * @return null when failed
     */
    public static Set<Transaction> getUnprocessedTransactions() {
        if (connection == null) {
            if (!connect()) {
                return null;
            }
        }
 
        ResultSet result = null;
        Set<Transaction> returnValue = new HashSet<>();

        String sql = "SELECT TransactionID, DebitIBAN, CreditIBAN, Amount, Message FROM Transactions WHERE State = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, TransactionState.WAITING.value);
            result = stmt.executeQuery();
            
            while (result.next()) {
                long transactionId = result.getLong("TransactionID");
                String debitIBAN = result.getString("DebitIBAN");
                String creditIBAN = result.getString("CreditIBAN");
                double amount = result.getDouble("Amount");
                String message = result.getString("Message");
                
                Transaction thisTransaction = new Transaction(transactionId, debitIBAN, creditIBAN, amount, message, TransactionState.WAITING);
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
    
    /**
     * Returns a @{link List} of @{link Transaction transactions} of the given @{Link Account accountID}.
     * @param accountID The ID of the account.
     * @return null on error.
     */
    public static List<Transaction> getTransactionsForAccountID(int accountID) {
        List<Transaction> transactions = null;
        ResultSet result;
        
        String sql = "SELECT TransactionID, debitIBAN, creditIBAN, Amount, Message, State FROM Transactions WHERE TransactionID = (SELECT TransactionID FROM AccountTransaction WHERE AccountID = ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            result = stmt.executeQuery();
            
            transactions = new ArrayList<>();
            while (result.next()) {
                transactions.add(new Transaction(result.getLong(1), result.getString(2), result.getString(3), result.getDouble(4), result.getString(5), TransactionState.fromValue(result.getInt(6))));
            }
        } catch (SQLException e) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return transactions;
    }
    
    /**
     * Get the credit for a given account
     * @param accountIBANNumber
     * @return 
     */
    public static double getAccountCredit(String accountIBANNumber) {
        if (connection == null) {
            if (!connect()) {
                return -99999999;
            }
        }

        ResultSet result = null;
        double returnValue = -88888888;
        String sql = "SELECT Credit FROM Account WHERE IBAN = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountIBANNumber);
            result = stmt.executeQuery();
            
            if (result.next()) {
                returnValue = result.getDouble(1);
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
            }
        }

        return returnValue;
    }
    
    /**
     * write logging to the database
     * @param severity
     * @param message 
     */
    public static void writeLogging(String severity, String message) {
        if (connection == null) connect();

        throw new NotImplementedException();
    }
}
