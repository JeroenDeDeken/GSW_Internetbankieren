/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subclasses;

import java.util.Objects;
import soapclient.Transaction;

/**
 * Provides overridable methods.
 * Possibly send checks back to the original object on the server.
 * @author Roy
 */
public class TransactionExtended extends Transaction {
    public TransactionExtended(Transaction transaction) {
        this.creditor = transaction.getCreditor();
        this.debitor = transaction.getDebitor();
        this.amount = transaction.getAmount();
        this.message = transaction.getMessage();
        this.state = transaction.getState();
    }
    
    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        
        Transaction otherTransaction = (Transaction) o;
        boolean retval = false;
        
        if (this.transactionId == otherTransaction.getTransactionId()
                && (this.debitor == null ? otherTransaction.getDebitor() == null : this.debitor.equals(otherTransaction.getDebitor()))
                && (this.creditor == null ? otherTransaction.getCreditor() == null : this.creditor.equals(otherTransaction.getCreditor()))
                && this.amount == otherTransaction.getAmount()
                && (this.message == null ? otherTransaction.getMessage() == null : this.message.equals(otherTransaction.getMessage()))
                && (this.state == null ? otherTransaction.getState() == null : this.state.equals(otherTransaction.getState()))) {
            retval = true;
        }
        
        return retval;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.transactionId ^ (this.transactionId >>> 32));
        hash = 59 * hash + Objects.hashCode(this.debitor);
        hash = 59 * hash + Objects.hashCode(this.creditor);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 59 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("Debitor: %s, Creditor: %s, Amount: %f, Description: %s", debitor, creditor, amount, message);
    }
}
