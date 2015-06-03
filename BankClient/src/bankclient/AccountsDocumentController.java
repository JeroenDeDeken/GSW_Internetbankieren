/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import soapclient.Account;

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class AccountsDocumentController implements Initializable {

    @FXML private Label lblName;
    @FXML private ListView lbAccounts;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getAccounts();
    }
    
    @FXML
    private void handleViewAccountAction(ActionEvent event) {
        //TODO load account transactions
        BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNT_FXML);
    }
    
    @FXML
    private void handleCreateAccountAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("New account");
        alert.setHeaderText("Are you sure you want to create a new account?");
        alert.setContentText("A new account will be created, you won't be able to delete it.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Account account = BankClient.getInstance().getService().createAccount(BankClient.getSessionID());
            if (account == null) {
                //TODO show error result
                return;
            }
            
            //TODO add to list
        }
    }
    
    @FXML
    private void handleNewTransactionAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.NEW_TRANSACTION_FXML);
    }
    
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
    
    private void getAccounts() {
        List<Account> accounts = BankClient.getInstance().getService().getAccounts(BankClient.getSessionID());
        if (accounts == null) {
            //TODO show error result
            return;
        }

        for (Account account : accounts) {
            //TODO add to list
        }
    }
}
