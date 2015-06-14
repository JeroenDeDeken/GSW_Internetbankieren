/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Roy
 */
public class Util {
    
    /**
     * Hashes the given String to SHA-512.
     * @param str The string to hash.
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String toSHA512(String str) throws NoSuchAlgorithmException {
        MessageDigest sh = MessageDigest.getInstance("SHA-512");
        sh.update(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : sh.digest()) sb.append(Integer.toHexString(0xff & b));
        return sb.toString();
    }
    
    /**
     * Performs checks if the given string is a valid IBAN code.
     * A valid IBAN consists of the first 3 a letter, and the next 10 of a number
     * @param IBAN The string to check
     * @return true when the given string is valid
     */
    public static boolean isValidIBAN(String IBAN) {
        if (IBAN == null) return false;
        if (IBAN.length() != 13) return false;
        if (!IBAN.substring(0, 3).matches("[a-zA-Z]+")) return false;
        if (!IBAN.substring(3, 13).matches("[0-9]+")) return false;
            
        return true;
    }
}
