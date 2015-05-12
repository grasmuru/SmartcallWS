package za.co.smartcall.smartload.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import org.controlsfx.dialog.Dialogs;

import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.Extractor;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.ParseCSV;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class FileDetailController {

    // Reference to the main application.
    private MainApp mainApp;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;
    
    private File fileInfo;
    
    
    @FXML
    private Label filePassword;
    
    @FXML
    private Label fileOrderReferenceNumber;
        
    @FXML
    private Label fileFilename;;
    
    @FXML
    private Label fileArrivalDate;
    
    @FXML
    private Label fileReference;
    
    @FXML
    private Label fileNetwork;
    
    @FXML
    private Label fileDescription;
    
    @FXML
    private Label fileAmount;
    
    @FXML
    private Label extractedLocation;
    
    @FXML
    private Button extractButton;
    
    @FXML
    private Button importButton;
    
    
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public FileDetailController() {
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
     }
    
    public void setFileInformation(File fileInfo) {
    	    this.fileInfo = fileInfo;
            fileFilename.setText(fileInfo.getFileName());
          
            fileArrivalDate.setText(fileInfo.getReceiveddate().toString());
            Transaction transaction = fileInfo.getTransaction();
             
            fileReference.setText(transaction.getDeviceId());
            fileAmount.setText(transaction.getAmount());
            fileDescription.setText(transaction.getDescription());
            fileNetwork.setText(transaction.getProduct().getProducttype().getNetwork().getNetwork());
            fileOrderReferenceNumber.setText(transaction.getOrderRefNumber());
            filePassword.setText(transaction.getPassword());
            if (fileInfo.isExtracted()){
            	extractButton.setDisable(true);
            	importButton.setDisable(false);
            	extractedLocation.setText(fileInfo.getExtractLocation());
            } else {
            	importButton.setDisable(true);
            	extractedLocation.setText("");
            }
            if (fileInfo.isImported()){
            	importButton.setDisable(true);
            }
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
    private void handleExtract() {
    	try {
			String fileNameAndLocation = RefPreferences.getDownloadLocation() + FILE_SEPARATOR +fileFilename.getText();
			String extractLocation = RefPreferences.getExtractLocation();
			Extractor extractor = new Extractor(extractLocation);
		    extractor.extractSingleFile(fileNameAndLocation, filePassword.getText());
		     fileInfo.setExtracted(true);
		     fileInfo.setExtractLocation(extractLocation);
		     DataAccess.saveFileToDB(fileInfo);
		     DialogFactory.createDialogInformation("File handling", "Call successful", "Extraction successful");
		     extractButton.setDisable(true);
		     importButton.setDisable(false);
		     mainApp.refresh();
		     extractedLocation.setText(extractLocation);
		} catch (Exception e) {
			DialogFactory.createDialogException("Extraction error", "Error extracting content from zip file", e);
		}
    }
    
    @FXML
    private void handleImport() {
    	try {
			String fileNameAndLocation = RefPreferences.getExtractLocation()+ FILE_SEPARATOR + fileFilename.getText();
		     new ParseCSV().parse(fileNameAndLocation,fileInfo.getId());
		     fileInfo.setImported(true);
		     DataAccess.saveFileToDB(fileInfo);
		     DialogFactory.createDialogInformation("File handling", "Call successful", "Import successful");
		     importButton.setDisable(true);
		     mainApp.refresh();
		} catch (Exception e) {
			DialogFactory.createDialogException("Import error", "Error importing content", e);
		}
    }
    
    @FXML
    private void checkContent() {
        dialogStage.close();
    }
    
    
}	
	

