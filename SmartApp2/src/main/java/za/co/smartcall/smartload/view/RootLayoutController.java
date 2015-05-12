package za.co.smartcall.smartload.view;

import java.net.MalformedURLException;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;

@Log4j
public class RootLayoutController {

    // Reference to the main application.
    private MainApp mainApp;
    
	@FXML
    private Parent embeddedDealerView; //embeddedElement
    @FXML
    private DealerView embeddedDealerViewController; // $embeddedElement+Controller
    
    @FXML
    private Parent embeddedPerformRechargeView; //embeddedElement
    @FXML
    private PerformRechargeView embeddedPerformRechargeViewController; // $embeddedElement+Controller
    
    @FXML
    private Parent embeddedTransferView; //embeddedElement
    @FXML
    private TransferView embeddedTransferViewController; // $embeddedElement+Controller
    
    @FXML
    private Parent embeddedFileOrderView; //embeddedElement
    @FXML
    private FileOrderView embeddedFileOrderViewController; // $embeddedElement+Controller
    
    @FXML
    private Parent embeddedManageFileOrderView; //embeddedElement
    @FXML
    private ManageFileOrderView embeddedManageFileOrderViewController; // $embeddedElement+Controller
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public RootLayoutController() {
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
        embeddedDealerViewController.setMainApp(mainApp);
        embeddedPerformRechargeViewController.setMainApp(mainApp);
        embeddedTransferViewController.setMainApp(mainApp);
        embeddedFileOrderViewController.setMainApp(mainApp);
        embeddedManageFileOrderViewController.setMainApp(mainApp);
    }
    
    @FXML
    private void handleGenerateCertificate() {
    	mainApp.showSetupCertificate();
    }
    
    @FXML
    private void handleModifyUrl() {
    	mainApp.showModifyUrl();
    }
    
    
    @FXML
    private void handleExistingKeys() {
    	mainApp.showHandleExistingKeys();
    }
    
    @FXML
    private void handleSettingsChange() {
    	mainApp.showSettings();
    }
    
    @FXML
    public void refesh() {
    	log.info("Update settings");
    	if (mainApp.getSmartloadInterface()==null) {
    		 DialogFactory.createDialogWarning(
	         "Error connecting to smartcall",
	         "Internal System error!",
	         "Looks like there is a problem with the certificate. If you have not done so already, generate and submit your key");
    	} else {
    		try{
	    		mainApp.retrieveProducts();
	    		reset();
		    	} catch (Exception e) {
		    		DialogFactory.createDialogException("Error connecting to smartcall", "Internal System error!", e);
			  	}	
    	}
    }
    
    
    @FXML
    public void reset() {
        embeddedDealerViewController.refresh();
  		embeddedPerformRechargeViewController.setup();
  		embeddedManageFileOrderViewController.setup();
  		embeddedTransferViewController.setup();
  		embeddedFileOrderViewController.setup();
	    log.info("Finished Performing a client requested refresh");

    }
    
    @FXML
    public void refreshConnection() {
    	try {
			mainApp.initialiseWebService();
		} catch (MalformedURLException e) {
			DialogFactory.createDialogException("Error connecting to smartcall", "Internal System error!", e);
		}
    }
    
    
}	
	

