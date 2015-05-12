package za.co.smartcall.smartload.view;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefGenerator;
import za.co.smartcall.smartload.RefPreferences;
/**
 * Allow configurable settings to be set
 * @author rudig
 *
 */
@Log4j
public class SettingsController {

    private MainApp mainApp;
    
    @FXML
    private TextField fileTextField;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private TextField extractTextField;
    
    @FXML
    private TextField transIdTextField;
    
    @FXML
    private TextField downloadTextField;
    
    @FXML
    private TextField archiveTextField;
    
    @FXML
    private ChoiceBox<String> loggingSettings;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SettingsController() {
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
        this.mainApp = mainApp;
        fileTextField.setText(RefPreferences.getLoggingLocation());
        extractTextField.setText(RefPreferences.getExtractLocation());
        downloadTextField.setText(RefPreferences.getDownloadLocation());
        archiveTextField.setText(RefPreferences.getArchiveLocation());
        transIdTextField.setText(RefGenerator.getInstance().getCurrentCount());
        loggingSettings.getItems().add("DEBUG");
        loggingSettings.getItems().add("INFO");
        loggingSettings.getItems().add("ERROR");
        loggingSettings.setValue(RefPreferences.getLogLevel());
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
    private void modifySettings() {
        try {
        	RefPreferences.setLoggingLocation(fileTextField.getText());
        	RefPreferences.setExtractLocation(extractTextField.getText());
        	RefPreferences.setDownloadLocation(downloadTextField.getText());
        	RefPreferences.setArchiveLocation(archiveTextField.getText());
			String content = transIdTextField.getText();
			RefGenerator.getInstance().updateCount(content);
			String level = loggingSettings.getValue();
			mainApp.changeLogging(level);
			dialogStage.close();
		} catch (Exception e) {
			DialogFactory.createDialogException("Alter settings", "Error changing settings", e);
		}
    }
    

}	
	

