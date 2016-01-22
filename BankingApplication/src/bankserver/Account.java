package bankserver;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Roy
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Account")
public class Account {
    private int accountID;
    private String IBAN;
    private double balance;
    private double credit;
    
    private Account() {
        
    }
    
    /**
     * Create a account object without identifier and save to database
     * Creates a new IBAN with 
     * @param balance
     * @param credit
     */
    public Account(double balance, double credit) {
        this(null, null, balance, credit);
    }
    
    
    /**
     * Create a account object with identifier, only used to load existing
     * @param accountID
     * @param IBAN
     * @param balance
     * @param credit
     */
    public Account(Integer accountID, String IBAN, double balance, double credit) {
        if (credit < 0) {
            throw new IllegalArgumentException("Credit may not be negative");
        }
        
        this.balance = balance;
        this.credit = credit;
        if (accountID == null && IBAN == null) {
            DBConnector.createNewCustomerAccount(this);
        }
        else {
            this.accountID = accountID;
            this.IBAN = IBAN;
        }
    }

    @XmlAttribute
    public int getAccountID() {
        return accountID;
    }

    @XmlAttribute
    public String getIBAN() {
        return IBAN;
    }

    @XmlAttribute
    public double getBalance() {
        return balance;
    }

    @XmlAttribute
    public double getCredit() {
        return credit;
    }

    void setAccountID(Integer ID) {
        this.accountID = ID;
    }

    void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.accountID;
        hash = 31 * hash + Objects.hashCode(this.IBAN);
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.balance) ^ (Double.doubleToLongBits(this.balance) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.credit) ^ (Double.doubleToLongBits(this.credit) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }
        
        final Account other = (Account) obj;
        if (this.accountID != other.accountID) {
            return false;
        }
        if (!Objects.equals(this.IBAN, other.IBAN)) {
            return false;
        }
        if (Double.doubleToLongBits(this.balance) != Double.doubleToLongBits(other.balance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.credit) != Double.doubleToLongBits(other.credit)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("IBAN: %s, Balance: %f, Credit: %f", IBAN, balance, credit);
    }
}
