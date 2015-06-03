package bankserver;

/**
 *
 * @author Roy
 */
public class Account {
    private long accountId;
    private String IBAN;
    private double balance;
    private double credit;
    
    /**
     * Create a account object without identifier and save to database
     * @param balance
     * @param credit
     */
    public Account(double balance, double credit) {
        this(null, null, balance, credit);
    }
    
    
    /**
     * Create a account object with identifier
     * @param accountId
     * @param IBAN
     * @param balance
     * @param credit
     */
    public Account(Long accountId, String IBAN, double balance, double credit) {
        if (accountId == null && IBAN == null) {
            DBConnector.createNewCustomerAccount(this);
        }
        else {
            this.accountId = accountId;
            this.IBAN = IBAN;
        }
        this.balance = balance;
        this.credit = credit;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getIBAN() {
        return IBAN;
    }

    public double getBalance() {
        return balance;
    }

    public double getCredit() {
        return credit;
    }

    public void setAccountID(Integer ID) {
        this.accountId = ID;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }
}
