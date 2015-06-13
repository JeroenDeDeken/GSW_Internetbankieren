/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.util.List;
import subclasses.AccountExtended;
import subclasses.TransactionExtended;

/**
 *
 * @author Roy
 */
public class Globals {
    private static Integer mSessionID;
    private static String mUsername;
    
    private static List<AccountExtended> mCustomerAccounts;
    private static AccountExtended mSelectedAccount;
    
    private static List<TransactionExtended> mAccountTransactions;
    private static TransactionExtended mSelectedTransaction;

    public static Integer getSessionID() {
        return mSessionID;
    }

    public static void setSessionID(Integer sessionID) {
        mSessionID = sessionID;
    }

    public static String getUsername() {
        return mUsername;
    }

    public static void setUsername(String username) {
        mUsername = username;
    }

    public static List<AccountExtended> getCustomerAccounts() {
        return mCustomerAccounts;
    }

    public static void setCustomerAccounts(List<AccountExtended> customerAccounts) {
        mCustomerAccounts = customerAccounts;
    }

    public static AccountExtended getSelectedAccount() {
        return mSelectedAccount;
    }

    public static void setSelectedAccount(AccountExtended selectedAccount) {
        mSelectedAccount = selectedAccount;
    }

    public static List<TransactionExtended> getAccountTransactions() {
        return mAccountTransactions;
    }

    public static void setAccountTransactions(List<TransactionExtended> accountTransactions) {
        mAccountTransactions = accountTransactions;
    }

    public static TransactionExtended getSelectedTransaction() {
        return mSelectedTransaction;
    }

    public static void setSelectedTransaction(TransactionExtended selectedTransaction) {
        mSelectedTransaction = selectedTransaction;
    }
    
}
