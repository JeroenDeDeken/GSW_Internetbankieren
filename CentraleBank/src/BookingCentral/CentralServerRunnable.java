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
        
        try {
            alive = true;
	    out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	    handler = new TransactionHandler(out);
                   
	    while ((inputLine = in.readLine()) != null) {
		handler.processInput(inputLine);
	    }
	    out.close();
	    in.close();
	    socket.close();
            alive = false;

	} catch (IOException e) {
	    e.printStackTrace();
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
}
