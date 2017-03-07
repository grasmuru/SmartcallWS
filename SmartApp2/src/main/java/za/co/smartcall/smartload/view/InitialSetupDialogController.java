package za.co.smartcall.smartload.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.wsclient.implementation.keys.Util;
/**
 * If the app identifies that this the first time that the user
 * has used this app it asks the user for details
 * @author rudig
 *
 */
@Log4j
public class InitialSetupDialogController {

    // Reference to the main application.
    private MainApp mainApp;
    
    @FXML
    private TextField dealerMsisdn;
    
    @FXML
    private TextField loggingLocation;
    
    @FXML
    private TextField downloadLocation;
    
    @FXML
    private TextField extractLocation;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public InitialSetupDialogController() {
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
        dealerMsisdn.setText("");
        loggingLocation.setText(RefPreferences.getLoggingLocation());
        downloadLocation.setText(RefPreferences.getDownloadLocation());
        extractLocation.setText(RefPreferences.getExtractLocation());
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
    public void setupDealer() {
        try {
			String msisdn = dealerMsisdn.getText();
			msisdn = Util.formatMsisdn(msisdn);
			mainApp.setDealerMsisdn(msisdn);
			String logLocation = RefPreferences.getLoggingLocation();
			if (!logLocation.equals(loggingLocation.getText())){
				RefPreferences.setLoggingLocation(loggingLocation.getText());
				MainApp.setupLogging(logLocation,RefPreferences.getLogLevel());
			}
			if (!RefPreferences.getExtractLocation().equals(extractLocation.getText())){
				RefPreferences.setExtractLocation(extractLocation.getText());
			}
			if (!RefPreferences.getDownloadLocation().equals(downloadLocation.getText())){
				RefPreferences.setDownloadLocation(downloadLocation.getText());
			}
			dialogStage.close();
		} catch (Exception e) {
			DialogFactory.createDialogException("Setup Error", "Error entering basic setup Data", e);
		}
    }
    
}	
	

