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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Roy
 */
public class LoginDocumentController implements Initializable {
    
    @FXML private Label lblResult;
    @FXML private TextField tfBankAddress, tfName, tfPassword;
    @FXML private Button btnLogin, btnRegister;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO retrieve announcements and give a messagebox
    }
    
    @FXML
    private void handleLoginAction(ActionEvent event) {
        btnLogin.setDisable(true);
        
        //TODO do actual login checks
        BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNTS_FXML);
    }
    
    @FXML
    private void handleRegisterAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.REGISTER_FXML);
    }
}