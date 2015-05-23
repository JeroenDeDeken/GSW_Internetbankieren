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
    
    private String lastScene = "";
    private Stage stage;
    private ClientService service;
    private static BankClient instance;
    
    @Override
    public void start(Stage stage) throws Exception {
        TextInputDialog dialog = new TextInputDialog("localhost");
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
            
            start(stage);
        }
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