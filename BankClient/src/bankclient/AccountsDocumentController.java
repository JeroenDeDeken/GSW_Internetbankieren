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

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class AccountsDocumentController implements Initializable {

    @FXML private Label lblName;
    @FXML private ListView lbAccounts;
    
    private List<Account> mAccounts;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO set account name in lblName
        getAccounts();
        handleAccountsClick();
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
            try {
                Account account = BankClient.getInstance().getService().createAccount(BankClient.getSessionID());
                if (account == null) {
                     showError("Server Error", "Something went wrong on the server while creating the account.", "Please try again in a few minutes");
                    return;
                }

                if (mAccounts == null) {
                    mAccounts = new ArrayList<>();
                    lbAccounts.getItems().setAll(mAccounts);
                }
                mAccounts.add(account);
            }
            catch (Exception e) {
                showError("Network error", "Something went wrong trying to creating the account.", "Please check your internet connection.");
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
            mAccounts = BankClient.getInstance().getService().getAccounts(BankClient.getSessionID());
            if (mAccounts != null) {
                lbAccounts.getItems().setAll(mAccounts);
            }
            else {
                showError("Server Error", "Something went wrong on the server while retrieving the accounts.", "Please try again in a few minutes");
            }
        }
        catch (Exception ex) {
            showError("Network error", "Failed to retrieve accounts from the server.", "Please check your internet connection.");
        }
    }
    
    private void handleAccountsClick() {
        lbAccounts.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                Account account = (Account)lbAccounts.getSelectionModel().getSelectedItem();
                BankClient.setSelectedAccountID(account.getAccountID());
                BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNT_FXML);
            }
        });
    }
    
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
