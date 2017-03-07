package za.co.smartcall.smartload.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.wsclient.DealerSetupException;
import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;

@Log4j
public class UseExistingKeystoreController {

    private MainApp mainApp;
    
    @FXML
    private TextField dealerTextField;
    
    @FXML
    private TextField passwordTextField;
        
    @FXML
    private Label keyLocationLabel;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;
    
	public static final String FILE_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.file";
	
	public static final String PASSWORD_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.keystore.password";
	
	public static final String ALIAS_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.keystore.alias";
	
	public static final String keyStoreProperties = "clientKeystore.properties";
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static String APP_HOME = System.getProperty("user.dir");
	
	public static final String CONFIG_HOME = APP_HOME+FILE_SEPARATOR+"config"+FILE_SEPARATOR+"keys";
	
	public static final String clientKeyStore = "clientKeystore.jks";

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
			passwordTextField.getText();
			updateDealerDetailsInFile(dealerTextField.getText(), passwordTextField.getText());
			dialogStage.close();
		} catch (Exception e) {
			DialogFactory.createDialogException("Use exisiting key pair", "Error setting new key", e);
		}
    }
    
    /**
	 * Create a file that has all the keystore properties and the user specific details 
	 */
	public void updateDealerDetailsInFile(String msisdn,String password) throws DealerSetupException{
		try {
			String keystorePropertiesFileLocation = KeyStoreImplementation.CONFIG_HOME + FILE_SEPARATOR + keyStoreProperties;
			File file = new File(keystorePropertiesFileLocation);
			PropertiesConfiguration config = new PropertiesConfiguration(file);
			config.setProperty(PASSWORD_PROPERTY_TAG, password);
			config.setProperty(ALIAS_PROPERTY_TAG, msisdn);
			config.setProperty(FILE_PROPERTY_TAG, KeyStoreImplementation.CONFIG_HOME+FILE_SEPARATOR+clientKeyStore);
			config.save();
			log.info("Saved keystore properties file as " + keystorePropertiesFileLocation);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			log.error("Error in dealer detail setup",e);
			throw new DealerSetupException(e.getMessage());
		}
	}
    

}	
	

