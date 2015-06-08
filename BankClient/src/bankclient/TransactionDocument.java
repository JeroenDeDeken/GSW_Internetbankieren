/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import soapclient.Transaction;

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class TransactionDocument implements Initializable {
    
    @FXML private TextField tfAmount, tfFromAccount, tfToAccount;
    @FXML private TextArea taMessage;
    @FXML private ComboBox lbTransactions;
    
    private List<Transaction> mTransactions;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getTransactions();
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) {
        BankClient.goBack();
    }
    
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
    
    private void getTransactions() {
        Integer selectedAccountID = BankClient.getSelectedAccountID();
        if (selectedAccountID != null && selectedAccountID >= 0) {
            try {
                mTransactions = BankClient.getInstance().getService().getTransactionsForAccount(BankClient.getSessionID(), selectedAccountID);
                if (mTransactions != null) {
                    //TODO asynchronous
                    lbTransactions.getItems().setAll(mTransactions);
                }
            }
            catch (Exception e) {
                lbTransactions.getItems().setAll("Failed to retrieve transactions!");
            }
        }
    }
}
