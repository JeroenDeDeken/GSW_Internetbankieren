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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
        // TODO
    }
    
    @FXML
    private void handleCreateTransactionAction(ActionEvent event) {
        //TODO create new transaction
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) {
        //TODO back, go to the last account
    }
    
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
}
