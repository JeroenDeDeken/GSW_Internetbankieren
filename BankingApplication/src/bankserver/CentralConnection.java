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
import java.net.SocketException;

/**
 *
 * @author Roy
 */
public class CentralConnection implements Runnable {

    // socket connection
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerHandler handler;
    private static String bankName;
    private Socket socket;
    private final String centralUrl;
    private final int centralPort;
    
    public CentralConnection(String bankName, String centralUrl, int centralPort) {
        CentralConnection.bankName = bankName;
        this.centralUrl = centralUrl;
        this.centralPort = centralPort;
    }
    
    @Override
    public void run() {
        System.out.println("thread started.");
        
        try {            
            socket = new Socket(centralUrl, centralPort);

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
        catch (SocketException e) {
            System.out.println("Lost connection to the central bank, error: " + e.getMessage());
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
     * @return if the sending succeeded
     */
    public boolean sendTransactionToCentral(Transaction transaction) {
        boolean sendSucceeded = false;
        
        // send the transaction to the banking central
        try {
            handler.sendTransaction(transaction);
            sendSucceeded = true;
        }
        catch (Exception e) {
            System.out.println("error sending transaction to server (" + e.getMessage() + ")");
        }
        
        if (sendSucceeded) {
            transaction.setState(TransactionState.SENDTOCENTRAL);
        } else {
            transaction.setState(TransactionState.WAITING);
        }
        
        return sendSucceeded;
    }
}
