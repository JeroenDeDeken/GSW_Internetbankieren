/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subclasses;

import java.util.Objects;
import soapclient.Account;

/**
 * Provides overridable methods.
 * Possibly send checks back to the original object on the server.
 * @author Roy
 */
public class AccountExtended extends Account {    
    public AccountExtended(Account account) {
        this.accountID = account.getAccountID();
        this.iban = account.getIban();
        this.balance = account.getBalance();
        this.credit = account.getCredit();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.accountID;
        hash = 31 * hash + Objects.hashCode(this.iban);
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.balance) ^ (Double.doubleToLongBits(this.balance) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.credit) ^ (Double.doubleToLongBits(this.credit) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account other = (Account) obj;
        if (this.accountID != other.getAccountID()) {
            return false;
        }
        if (!Objects.equals(this.iban, other.getIban())) {
            return false;
        }
        if (Double.doubleToLongBits(this.balance) != Double.doubleToLongBits(other.getBalance())) {
            return false;
        }
        if (Double.doubleToLongBits(this.credit) != Double.doubleToLongBits(other.getCredit())) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("IBAN: %s, Balance: %f, Credit: %f", iban, balance, credit);
    }
}
