/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankserver;

import java.util.List;
import javafx.util.Pair;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
     * 
     * @param sessionID
     * @param fromAccount
     * @param toAccount
     * @param amount
     * @param description
     * @return 
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
    
    //TODO return result
    public Account createAccount(int sessionID) {
        //TODO
        //Determine the credit
        //Generate a new IBAN (IBAN code length 3) + (AccountID length 10))
        
        Account account = new Account(0, 0);
        
        //check input
        //create transaction
        //save to database
        
        DBConnector.createNewCustomerAccount(account);
        
        return account;
    }
    
    public List<Account> getAccounts(int sessionID) {
        //TODO check session, get user, return array
        return null;
    }
    
    public List<Transaction> getTransactionsForAccount(int sessionID, int accountID) {
        //TODO check session, get user, return array
        return null;
    }
}
