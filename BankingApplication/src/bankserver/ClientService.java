/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankserver;

import javafx.util.Pair;
import javax.jws.WebService;

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
    
    public ClientService() {
        
    }
    
    /**
     * Register a user with the given parameters.
     * @param username The username of the user, must be unique.
     * @param password The password of the user, must be 6 characters long.
     * @param residence The residence of the user, may not be empty.
     * @return Register status code.
     */
    public registerStatus register(String username, String password, String residence) {
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
     * @return The status code of the login, on success it will contain also the session id on success.
     */
    public Pair<loginStatus, Integer> login(String username, String password) {
        username = username.trim();
        if (username.isEmpty() || password.isEmpty()) return new Pair<>(loginStatus.missingFields, -1);
        
        Integer sessionID = -1;
        
        try {
            Integer result = DBConnector.loginUser(username, password);
            if (result == null) {
                return new Pair<>(loginStatus.serverError, -1);
            } else if (result < 0) {
                return new Pair<>(loginStatus.notFound, -1);
            }
            return new Pair<>(loginStatus.success, result);
        } catch (Exception ex) {
            return new Pair<>(loginStatus.serverError, -1);
        }
    }
    
    /**
     * Login a user with the provided username & password.
     * @param sessionID The sessionID of the session to close.
     * @param username The username of the user to logout.
     */
    public void logout(int sessionID, String username) {
        
        //TODO destroy the session with the username
        
    }
    
    public void createTransaction(int sessionID, String fromAccount, String toAccount, double amount, String description) {
        //TODO
    }
    
    public void getAccounts(int sessionID) {
        //TODO check session, get user, return array
    }
    
    public void getTransactionsForAccount(int sessionID) {
        //TODO check session, get user, return array
    }
}
