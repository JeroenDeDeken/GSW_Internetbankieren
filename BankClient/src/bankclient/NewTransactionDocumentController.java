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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.xml.ws.Holder;
import soapclient.NewTransactionStatus;
import soapclient.Transaction;
import soapclient.TransactionState;
import subclasses.AccountExtended;

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
    @FXML private Button btnCreate;
    @FXML private Label lblResult;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAccounts();
    }
    
    @FXML
    private void handleCreateTransactionAction(ActionEvent event) {
        btnCreate.setDisable(true);
        lblResult.setText("");
        
        //TODO create new transaction
        //Check input
        //Check result of server
        
        try {
            String debitIBAN, creditIBAN, descripton;
            double amount;
            
            AccountExtended selectedDebit, selectedCredit;
            selectedDebit = (AccountExtended)ccbFromAccount.getSelectionModel().getSelectedItem();
            if (selectedDebit == null) {
                setResult("Please select a debit account first.");
                return;
            }
            debitIBAN = selectedDebit.getIban();
            
            Object selected = cbbToAccount.getSelectionModel().getSelectedItem();
            if (selected instanceof String) {
                if (!BankClient.isValidIBAN(selected.toString())) {
                    setResult("No valid amount above €0,00 entered.");
                    return;
                }
                creditIBAN = cbbToAccount.getSelectionModel().getSelectedItem().toString();
            }
            else if (selected instanceof AccountExtended) {
                selectedCredit = (AccountExtended)selected;
                creditIBAN = selectedCredit.getIban();
            }
            else {
                setResult("Failed to retrieve the debit IBAN.");
                return;
            }
            
            try {
                amount = Double.valueOf(tfAmount.getText());
            }
            catch (NumberFormatException ex) {
                setResult("No valid amount above €0,00 entered.");
                return;
            }
            
            if (amount <= 0) {
                setResult("Please enter a valid amount above €0,00.");
                return;
            }
            
            if (((selectedDebit.getBalance() + selectedDebit.getCredit()) - amount) < 0) {
                setResult("The selected debit account does not have enough credit. Only €" + selectedDebit.getBalance() + selectedDebit.getCredit());
                return;
            }
            
            descripton = taMessage.getText();
            
            NewTransactionStatus status;
            try {
                Holder<Transaction> newTransaction = new Holder<>();
                status = BankClient.getInstance().getService().createTransaction(Globals.getSessionID(), "debit", "credit", 50, "description", newTransaction);
            }
            catch (Exception ex) {
                setResult("Failed to retrieve transactions from the server." + System.lineSeparator() + "Please check your internet connection.");
                return;
            }
            
            switch (status) {
                case SUCCESS: break;
                case INVALID_IBAN: setResult("Invalid IBAN given, it shoud be for example 'XXX1234567890'."); return;
                case INVALID_AMOUNT: setResult("Invalid amount entered"); return;
                case SERVER_ERROR: setResult("Something went wrong, try again later."); return;
                default: setResult("A unknown error happened, try again later."); return;
            }
            BankClient.showAlert(AlertType.INFORMATION, "Transaction Complete", "The transaction was send out succesfully", "");
            BankClient.goBack();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            setResult("");
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
        List<AccountExtended> accounts = Globals.getCustomerAccounts();
        ccbFromAccount.getItems().setAll(accounts);
        cbbToAccount.getItems().setAll(accounts);
        //TODO select possibly
    }
    
    private void setResult(String result) {
        btnCreate.setDisable(false);
        lblResult.setText(result);
    }
}
