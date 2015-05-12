package za.co.smartcall.smartload.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import org.controlsfx.dialog.Dialogs;

import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;

@Log4j
public class UseExistingKeystoreController {

    private MainApp mainApp;
    
    @FXML
    private TextField dealerTextField;
        
    @FXML
    private Label keyLocationLabel;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public UseExistingKeystoreController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	keyLocationLabel.setText(KeyStoreImplementation.CONFIG_HOME);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        dealerTextField.setText(RefPreferences.getDealerMsisdn());
    }
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    private void handleReturn() {
        dialogStage.close();
    }
    
    @FXML
    private void linkToExistingKeystore() {
        try {
			mainApp.setDealerMsisdn(dealerTextField.getText());
			dialogStage.close();
		} catch (Exception e) {
			DialogFactory.createDialogException("Use exisiting key pair", "Error setting new key", e);
		}
    }
    

}	
	

