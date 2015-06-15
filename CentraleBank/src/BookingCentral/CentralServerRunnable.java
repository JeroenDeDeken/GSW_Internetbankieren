/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookingCentral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Jeroen
 */
public class CentralServerRunnable implements Runnable {
    
    private Socket socket = null;
    private TransactionHandler handler = null;
    public String bankName = null;
    public boolean alive;
    public PrintWriter out;
    
    public CentralServerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("thread started.");
        
        BufferedReader in = null;
        
        try {
            alive = true;
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	    handler = new TransactionHandler(out);
                   
	    while ((inputLine = in.readLine()) != null) {
		handler.processInput(inputLine);
                if (handler.bankName != null) {
                    bankName = handler.bankName;
                    System.out.println("bankname set to " + bankName);
                }
	    }
	} catch (IOException e) {
            
	}
        finally {
            try {
                if (out != null) out.close();
                if (out != null) in.close();
                if (socket != null) socket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            alive = false;
            System.out.println("Connection closed: " + bankName);
        }
    }

    /**
     * Send a transaction to the client so it can be processed.
     * @param transaction to send
     * @return true when successfully send to the client
     */
    public boolean sendTransaction(Transaction transaction) {
        return handler.sendToBank(transaction);
    }
    
    protected boolean sendTransactionState(Transaction transaction, TransactionState state) {
        return handler.sendTransactionState(transaction, state);
    }
}
