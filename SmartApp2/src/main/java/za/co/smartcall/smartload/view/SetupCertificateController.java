package za.co.smartcall.smartload.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.wsclient.KeyInterface;
import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;
import za.co.smartcall.wsclient.implementation.keys.Util;
@Log4j
public class SetupCertificateController {

    private MainApp mainApp;
    
    @FXML
    private TextField dealerMsisdn;
    
    @FXML
    private TextField keyLocation;
    
    private KeyInterface keyInterface;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SetupCertificateController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
		keyInterface = new KeyStoreImplementation(); 
		keyLocation.setText(keyInterface.getKeyLocation());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        dealerMsisdn.setText(RefPreferences.getDealerMsisdn());
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
    private void setupDealer() {
        try {
			String msisdn = dealerMsisdn.getText();
			msisdn = Util.formatMsisdn(msisdn);
			String password = msisdn;
			log.info("Creating key pair for " + msisdn);
			keyInterface.updateDealerDetailsInFile(msisdn,password);
			keyInterface.createPrivateKeystore(msisdn, password);
			keyInterface.addSmartcallPublicKey(password);
			mainApp.setDealerMsisdn(msisdn);
			DialogFactory.createDialogInformation("Setup Complete", "Keys created", "Please send the .cer file to smartcall");
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Open Resource File");
	        configureFileChooser(fileChooser);
	        fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("Certificates", "*.cer")
	           
	            );
	        fileChooser.showOpenDialog(mainApp.getPrimaryStage());
	        
		} catch (Exception e) {
			DialogFactory.createDialogException("Setup Error", "Error performing key setup", e);
		}
    }
    
    private void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
            new File(keyInterface.getKeyLocation())
        ); 
    }
}	
	

