/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.xml.ws.Holder;
import soapclient.NewTransactionStatus;
import soapclient.Transaction;
import soapclient.TransactionState;

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class NewTransactionDocumentController implements Initializable {

    @FXML private ChoiceBox ccbFromAccount;
    @FXML private ComboBox cbbToAccount;
    @FXML private TextField tfAmount;
    @FXML private TextArea taMessage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAccounts();
    }
    
    @FXML
    private void handleCreateTransactionAction(ActionEvent event) {
        //TODO create new transaction
        //Check input
        //Check result of server
        
        
        try {
            Holder<Transaction> newTransaction = new Holder<>();
            NewTransactionStatus status = BankClient.getInstance().getService().createTransaction(Globals.getSessionID(), "debit", "credit", 480567, "description", newTransaction);
            //TODO handle status etc
        }
        catch (Exception ex) {
            BankClient.showAlert(AlertType.ERROR, "Network error", "Failed to retrieve transactions from the server.", "Please check your internet connection.");
        }
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) {
        BankClient.goBack();
    }
    
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
    
    private void loadAccounts() {
        //TODO list the customers accounts in the ChoiceBox & combobox
    }
}
