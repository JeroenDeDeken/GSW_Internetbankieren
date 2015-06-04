/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankserver;

import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;

/**
 *
 * @author Roy
 */
@WebService
public class ClientService {
    private static ClientService cs;
    public static ClientService getInstance() {
        if (cs == null) cs = new ClientService();
        return cs;
    }
    
    public enum registerStatus {
        success,
        missingFields,
        usernameAlreadyExists,
        usernameToShort,
        passwordToShort,
        serverError,
    }
    
    public enum loginStatus {
        success,
        missingFields,
        notFound,
        serverError,
    }
    
    public enum newTransactionStatus {
        success,
        invalidIBAN,
        invalidAmount,
        serverError,
    }
    
    public ClientService() {
        
    }
    
    /**
     * Register a user with the given parameters.
     * @param username The username of the user, must be unique.
     * @param password The password of the user, must be 6 characters long.
     * @param residence The residence of the user, may not be empty.
     * @return Register status code.
     */
    public registerStatus register(@WebParam(name = "username") String username, @WebParam(name = "password") String password,@WebParam(name = "residense") String residence) {
        username = username.trim();
        if (username.isEmpty() || password.isEmpty() || residence.trim().isEmpty()) return registerStatus.missingFields;
        
        if (username.length() < 4) return registerStatus.usernameToShort;
        if (password.length() < 4) return registerStatus.passwordToShort;
        
        try {
            Integer result = DBConnector.loginUser(username, password);
            if (result == null) {
                return registerStatus.serverError;
            } else if (result < 0) {
                return registerStatus.usernameAlreadyExists;
            }
            return registerStatus.success;
        } catch (Exception ex) {
            return registerStatus.serverError;
        }
    }
    
    /**
     * Login a user with the provided username & password.
     * @param username
     * @param password
     * @param sessionID
     * @return The status code of the login, on success it will contain also the session id on success.
     */
    public loginStatus login(@WebParam(name = "username") String username, @WebParam(name = "password") String password ,
                               @WebParam(name = "sessionID", mode = WebParam.Mode.OUT) Holder<Integer> sessionID) {
        username = username.trim();
        if (username.isEmpty() || password.isEmpty()) return loginStatus.missingFields;
        
        try {
            Integer result = DBConnector.loginUser(username, password);
            if (result == null) {
                return loginStatus.serverError;
            } else if (result < 0) {
                return loginStatus.notFound;
            }
            sessionID.value = result;
            return loginStatus.success;
        } catch (Exception ex) {
            return loginStatus.serverError;
        }
    }
    
    /**
     * Logout a user with the provided session ID.
     * @param sessionID The sessionID of the session to close.
     */
    public void logout(int sessionID) {
        DBConnector.logoutUser(sessionID);
    }
    
    /**
     * Creates a new @{link Transaction} transaction with the given parameters.
     * @param sessionID The session ID where the user will be determined from.
     * @param fromAccount The IBAN of the credit account.
     * @param toAccount The IBAN of the debit account.
     * @param amount The amount to transact.
     * @param description The description of the transaction.
     * @return The status of the process.
     */
    public newTransactionStatus createTransaction(int sessionID, String fromAccount, String toAccount, double amount, String description) {
        
        if (amount <= 0) return newTransactionStatus.invalidAmount;
        if (!BankServer.getInstance().isValidIBAN(fromAccount) || !BankServer.getInstance().isValidIBAN(toAccount)) return newTransactionStatus.invalidIBAN;
        
        //TODO check if sessionID user contains fromAccount IBAN
        
        Transaction transaction = new Transaction(fromAccount, toAccount, amount, description);
        
        if (BankServer.getInstance().processTransaction(transaction) != TransactionState.FAILED) {
            return newTransactionStatus.serverError;
        }
        return newTransactionStatus.success;
    }
    
    /**
     * Creates a account for the customer bundled with the session.
     * @param sessionID The sessionID to create a account for.
     * @return The @{link Account account} created.
     */
    public Account createAccount(int sessionID) {
        //TODO Determine the credit
        
        Account account = new Account(0, 0);
        DBConnector.connectCustomerAccount(account.getAccountId(), DBConnector.getUserIDForSessionID(sessionID));
        return account;
    }
    
    /**
     * Returns the @{link Account accounts} owned by the customer.
     * @param sessionID The sessionID linked for the customerID.
     * @return 
     */
    public List<Account> getAccounts(int sessionID) {
        Integer id = DBConnector.getUserIDForSessionID(sessionID);
        return (id != null ? DBConnector.getAccountsForCustomerID(id) : null);
    }
    
    public List<Transaction> getTransactionsForAccount(int sessionID, int accountID) {
        //TODO check session, get user, return array
        return null;
    }
}
