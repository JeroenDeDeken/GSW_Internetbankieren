/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import soapclient.ClientService;
import soapclient.ClientServiceService;

/**
 *
 * @author Roy
 */
public class BankClient extends Application {
    public final static String LOGIN_FXML = "LoginDocument.fxml";
    public final static String REGISTER_FXML = "RegisterDocument.fxml";
    public final static String ACCOUNTS_FXML = "AccountsDocument.fxml";
    public final static String ACCOUNT_FXML = "AccountDocument.fxml";
    public final static String NEW_TRANSACTION_FXML = "NewTransactionDocument.fxml";
    
    private final String PREF_BANK_URL = "pref_bank_url";
    
    private String lastScene = "";
    private Stage stage;
    private ClientService service;
    private static BankClient instance;
    
    private Integer sessionID = null, selectedAccountID = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        TextInputDialog dialog = new TextInputDialog(loadBankUrl());
        dialog.setTitle("Bank client application");
        dialog.setHeaderText("Please enter your banking address/url, if you are not sure what you need to fill in, please contact your bank.");
        dialog.setContentText("Banking url:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("URL entered: " + result.get());
            
            String url = result.get();
            String fullUrl = "http://" + url + ":8080/BankServer?wsdl";
            
            try {
                ClientServiceService css = new ClientServiceService(new URL(fullUrl));

                service = new ClientServiceService().getClientServicePort();
                instance = this;
                
                saveBankUrl(url);

                this.stage = stage;
                showFXMLDocument(LOGIN_FXML);
                this.stage.show();
            }
            catch (Exception ex) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Bank client application");
                alert.setHeaderText("Error");
                alert.setContentText("Failed to connect to the server '" + url + "'!" + System.lineSeparator() + "(" + fullUrl + ")");

                alert.showAndWait();

                start(stage);
            }
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Bank client application");
            alert.setHeaderText("Error");
            alert.setContentText("You have to enter a url/ip-address before you may continue!");

            alert.showAndWait();
        }
    }
    
    /**
     * Gets the {@link Preferences} for the application.
     * @return The {@link Preferences} object.
     */
    private Preferences getPreferences() {
        return Preferences.userNodeForPackage(BankClient.class);
    }
    
    /**
     * Saves the given string to the preferences.
     * @param bankUrl The string to save and load next time.
     */
    private void saveBankUrl(String bankUrl) {
        getPreferences().put(PREF_BANK_URL, bankUrl);
    }
    
    /**
     * Loads the last successful bankurl from the preferences.
     * @return 'localhost' by default.
     */
    private String loadBankUrl() {
        return getPreferences().get(PREF_BANK_URL, "localhost");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static BankClient getInstance() {
        return instance;
    }

    public ClientService getService() {
        return service;
    }

    public static Integer getSessionID() {
        return instance.sessionID;
    }

    public static void setSessionID(Integer sessionID) {
        instance.sessionID = sessionID;
    }

    public static Integer getSelectedAccountID() {
        return instance.selectedAccountID;
    }

    public static void setSelectedAccountID(Integer accountID) {
        instance.selectedAccountID = accountID;
    }

    /**
     * Goes to the previous FXML
     */
    public static void goBack() {
        instance.showFXMLDocument(instance.lastScene);
    }

    public Parent showFXMLDocument(String fxml) {
        try {
            lastScene = fxml;
            
            Parent page = (Parent)FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(page, 700, 450);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(page);
            }
            stage.sizeToScene();
            return page;
        } catch (IOException ex) {
            Logger.getLogger(BankClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}