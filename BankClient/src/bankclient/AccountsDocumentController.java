/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import soapclient.Account;
import subclasses.AccountExtended;

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
        lblName.setText(Globals.getUsername());
        getAccounts();
        handleAccountsClick();
    }
    
    @FXML
    private void handleViewAccountAction(ActionEvent event) {
        viewAccount();
    }
    
    @FXML
    private void handleCreateAccountAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("New account");
        alert.setHeaderText("Are you sure you want to create a new account?");
        alert.setContentText("A new account will be created, you won't be able to delete it.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                Account account = BankClient.getInstance().getService().createAccount(Globals.getSessionID());
                if (account == null) {
                     BankClient.showAlert(AlertType.ERROR, "Server Error", "Something went wrong on the server while creating the account.", "Please try again in a few minutes");
                    return;
                }

                if (Globals.getCustomerAccounts() == null) {
                    Globals.setCustomerAccounts(new ArrayList<>());
                }
                Globals.getCustomerAccounts().add(new AccountExtended(account));
                lbAccounts.getItems().setAll(Globals.getCustomerAccounts());
            }
            catch (Exception e) {
                BankClient.showAlert(AlertType.ERROR, "Network error", "Something went wrong trying to creating the account.", "Please check your internet connection.");
            }
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
        try {
            List<Account> accounts = BankClient.getInstance().getService().getAccounts(Globals.getSessionID());
            if (accounts != null) {
                Globals.setCustomerAccounts(new ArrayList<>());
                for (Account account : accounts) {
                    Globals.getCustomerAccounts().add(new AccountExtended(account));
                }
                lbAccounts.getItems().setAll(Globals.getCustomerAccounts());
            }
            else {
                BankClient.showAlert(AlertType.ERROR, "Server Error", "Something went wrong on the server while retrieving the accounts.", "Please try again in a few minutes");
            }
        }
        catch (Exception ex) {
            BankClient.showAlert(AlertType.ERROR, "Network error", "Failed to retrieve accounts from the server.", "Please check your internet connection.");
        }
    }
    
    private void handleAccountsClick() {
        lbAccounts.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                viewAccount();
            }
        });
    }
    
    private void viewAccount() {
        AccountExtended account = (AccountExtended)lbAccounts.getSelectionModel().getSelectedItem();
        Globals.setSelectedAccount(account);
        if (account == null) return;
        BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNT_FXML);
    }
}
