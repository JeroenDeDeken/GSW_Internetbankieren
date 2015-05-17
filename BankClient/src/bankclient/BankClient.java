/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankclient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private static BankClient instance;
    
    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        
        this.stage = stage;
        showFXMLDocument(LOGIN_FXML);
        this.stage.show();
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