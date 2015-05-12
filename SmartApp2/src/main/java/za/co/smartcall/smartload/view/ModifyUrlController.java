package za.co.smartcall.smartload.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;

@Log4j
public class ModifyUrlController {

    
    @FXML
    private TextField urlTextField;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ModifyUrlController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        urlTextField.setText(RefPreferences.getWebServiceUrl());
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
    private void modifyUrl() {
        try {
			RefPreferences.setWebServiceUrl(urlTextField.getText());
			dialogStage.close();
		} catch (Exception e) {
			DialogFactory.createDialogException("Changing url", "Url error", e);
		}
    }
    

}	
	

