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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import soapclient.Transaction;
import subclasses.AccountExtended;
import subclasses.TransactionExtended;

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class AccountDocumentController implements Initializable {

    @FXML private Label lblAccount, lblCredit, lblCreditValue, lblBalance, lblBalanceValue;
    @FXML private ListView lbTransactions;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AccountExtended account = Globals.getSelectedAccount();
        lblAccount.setText(account.getIban());
        lblCreditValue.setText(String.format("%.2f", account.getCredit()));
        lblBalanceValue.setText(String.format("%.2f", account.getBalance()));
        
        handleTransactionClick();
        getTransactions();
    }
    
    @FXML
    private void handleNewTransactionAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.NEW_TRANSACTION_FXML);
    }
    
    @FXML
    private void handleViewTransactionAction(ActionEvent event) {
        viewTransaction();
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNTS_FXML);
    }
    
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
    
    private void getTransactions() {
        try {
            List<Transaction> transactions = BankClient.getInstance().getService().getTransactionsForAccount(Globals.getSessionID(), Globals.getSelectedAccount().getAccountID());
            if (transactions != null) {
                for (Transaction transaction : transactions) {
                    Globals.getAccountTransactions().add(new TransactionExtended(transaction));
                }
                lbTransactions.getItems().setAll(Globals.getAccountTransactions());
            }
            else {
                BankClient.showAlert(Alert.AlertType.ERROR, "Server Error", "Something went wrong on the server while retrieving the transactions.", "Please try again in a few minutes");
            }
        }
        catch (Exception ex) {
            BankClient.showAlert(Alert.AlertType.ERROR, "Network error", "Failed to retrieve transactions from the server.", "Please check your internet connection.");
        }
    }
    
    private void handleTransactionClick() {
        lbTransactions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                viewTransaction();
            }
        });
    }
    
    private void viewTransaction() {
        TransactionExtended transaction = (TransactionExtended)lbTransactions.getSelectionModel().getSelectedItem();
        Globals.setSelectedTransaction(transaction);
        if (transaction == null) return;
        BankClient.getInstance().showFXMLDocument(BankClient.TRANSACTION_FXML);
    }
}
