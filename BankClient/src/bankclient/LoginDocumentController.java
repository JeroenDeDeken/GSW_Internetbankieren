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
import javax.xml.ws.Holder;
import soapclient.LoginStatus;

/**
 *
 * @author Roy
 */
public class LoginDocumentController implements Initializable {
    
    @FXML private Label lblResult;
    @FXML private TextField tfName, tfPassword;
    @FXML private Button btnLogin, btnRegister;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO retrieve announcements and give a messagebox
    }
    
    @FXML
    private void handleLoginAction(ActionEvent event) {
        btnLogin.setDisable(true);
        lblResult.setText("");
        
        try {
            String username, password;

            username = tfName.getText().trim();
            password = tfPassword.getText();

            if (username.isEmpty() || password.isEmpty()) {
                setResult("Enter a username & password first!");
                return;
            }
            
            password = Util.toSHA512(password);

            final Holder<Integer> sessionID = new Holder<>();

            LoginStatus status;
            try {
                status = BankClient.getInstance().getService().login(username, password, sessionID);
            } catch (Exception ex) {
                setResult("Something went wrong sending the request to the banking server." + System.lineSeparator() + "Please try again later.");
                return;
            }

            switch (status) {
                case SUCCESS: Globals.setUsername(username); Globals.setSessionID(sessionID.value); break;
                case MISSING_FIELDS: setResult("No username or password specified."); return;
                case SERVER_ERROR: setResult("Something went wrong, try again later."); return;
                case NOT_FOUND: setResult("Username & password combination not found."); return;
                default: setResult("A unknown error happened, try again later."); return;
            }

            BankClient.getInstance().showFXMLDocument(BankClient.ACCOUNTS_FXML);
        } catch (Exception ex) {
        } finally {
            setResult("");
        }
    }
    
    private void setResult(String result) {
        btnLogin.setDisable(false);
        lblResult.setText(result);
    }
    
    @FXML
    private void handleRegisterAction(ActionEvent event) {
        BankClient.getInstance().showFXMLDocument(BankClient.REGISTER_FXML);
    }
}