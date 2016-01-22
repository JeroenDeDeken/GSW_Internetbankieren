/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankServer;

import bankserver.Account;
import org.junit.Test;

/**
 *
 * @author Roy
 */
public class AccountTest {
    
    /**
     * Test the fail of a constructor of accountID with a negative credit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccount() {
        Account account = new Account(10, -10);
    }
}
