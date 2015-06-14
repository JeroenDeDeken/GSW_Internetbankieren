/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 *
 * @author Roy
 */
public class CentralConnection {

    // socket connection
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerHandler handler;
    private static String bankName;
    
    public CentralConnection(String bankName) {
        try {
            this.bankName = bankName;
            Socket socket = new Socket("localhost", 4444);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            
            String inputLine;
            handler = new ServerHandler(out);
            
            System.out.println("Connection: Send bankname " + bankName);
            handler.sendBankName(bankName);
            
            while ((inputLine = in.readLine()) != null) {
                handler.processInput(inputLine);
            }
            
            out.close();
            in.close();
            socket.close();
        }
        catch (ConnectException e) {
            System.out.println("Failed to connect to the central bank, error: " + e.getMessage());
        }
        catch (Exception e) {
            //Failed to c
            System.out.println("CentralConnection error: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    /**
     * When the credit account is no customer of this bank send the transaction to 
     * the banking central for processing
     * @param transaction 
     */
    public void sendTransactionToCentral(Transaction transaction) {
        boolean sendSucceeded = false;
        
        // send the transaction to the banking central
        try {
            handler.sendTransaction(transaction);
            sendSucceeded = true;
        }
        catch (Exception e) {
            System.out.println("error sending transaction to server");
        }
        
        if (sendSucceeded) {
            DBConnector.changeTransactionState(transaction, TransactionState.SENDTOCENTRAL);
        }
        else {
            DBConnector.changeTransactionState(transaction, TransactionState.WAITING);
        }
    }
}
