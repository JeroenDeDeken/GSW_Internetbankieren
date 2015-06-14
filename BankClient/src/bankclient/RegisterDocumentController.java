/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import soapclient.RegisterStatus;

/**
 * FXML Controller class
 *
 * @author Roy
 */
public class RegisterDocumentController implements Initializable {

    @FXML private Label lblResult;
    @FXML private Button btnRegister;
    @FXML private TextField tfName, tfResidence, tfPassword1, tfPassword2;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @FXML
    private void handleRegisterAction(ActionEvent event) {
        try {
            btnRegister.setDisable(true);
            lblResult.setText("");
            
            String username, password, passwordRepeated, residence;
            
            username = tfName.getText().trim();
            residence = tfResidence.getText().trim();
            password = tfPassword1.getText();
            passwordRepeated = tfPassword2.getText();
            
            if (username.isEmpty() || password.isEmpty() || residence.isEmpty()) {
                setResult("Enter a username, password & residence first!");
                return;
            }
            
            if (!password.equals(passwordRepeated)) {
                setResult("The passwords provided do not match!");
                return;
            }
            
            if (username.length() < 4) {
                setResult("The username is too short, use at least 4 characters!");
                return;
            }
            
            if (password.length() < 4) {
                setResult("The password is too short, use at least 4 characters!");
                return;
            }
            
            password = Util.toSHA512(password);
            
            RegisterStatus status;
            try {
                status = BankClient.getInstance().getService().register(username, password, residence);
            } catch (Exception ex) {
                setResult("Something went wrong sending the request to the banking server." + System.lineSeparator() + "Please try again later.");
                return;
            }
            
            switch (status) {
                case SUCCESS: break;
                case MISSING_FIELDS: setResult("No username or password specified."); return;
                case SERVER_ERROR: setResult("Something went wrong, try again later."); return;
                case USERNAME_TO_SHORT: setResult("The given username is too short, use at least 4 characters."); return;
                case PASSWORD_TO_SHORT: setResult("The given password is too short, use at least 4 characters."); return;
                case USERNAME_ALREADY_EXISTS: setResult("The username is already in use, choose another one."); return;
                default: setResult("A unknown error happened, try again later."); return;
            }
            
            Globals.setUsername(username);
            BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNTS_FXML);
        } catch (NoSuchAlgorithmException ex) {
        }
        finally {
            setResult("");
        }
    }
    
    private void setResult(String result) {
        btnRegister.setDisable(false);
        lblResult.setText(result);
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.LOGIN_FXML);
    }
}
