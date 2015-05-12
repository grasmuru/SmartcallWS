package za.co.smartcall.smartload.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.log4j.Log4j;
import za.co.smartcall._2010._12.common.ResponseCode;
import za.co.smartcall._2010._12.message.FundTransferRequest;
import za.co.smartcall._2010._12.message.FundTransferResponse;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.InputConstraints;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.SubmissionStatus;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class TransferView {

    private MainApp mainApp;
    
    @FXML
    private TextField recipientTextField;
    
    @FXML
    private TextField amountTextField;
    
    @FXML
    private Label dealerMsisdn;
    
    @FXML
    private Label dealerBalance;
    
    @FXML
    private Label lastTransaction;
    
    @FXML
    private CheckBox sendSmsBox;
    
    private boolean okClicked = false;
    
    public static final String DESCRIPTION = "Transfer";
    

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public TransferView() {
    	
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	InputConstraints.numbersOnly(amountTextField, 6);
    	InputConstraints.numbersOnly(recipientTextField, 11);
    	lastTransaction.setText("-");
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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
    private void perfromTransfer() {
    	BigDecimal transferAmount  = new BigDecimal(0);
    	String dealer = "";
        try {
        	String amount = amountTextField.getText();
        	transferAmount  = new BigDecimal(amount);
        	dealer = recipientTextField.getText();
        	if (amount.equals("")) throw new IllegalArgumentException("Must supply a transfer amount");
        	if (!InputConstraints.validateMsisdn(dealer)) throw new IllegalArgumentException("Dealer Id not in correct msisdn format");
        	boolean sendSms = sendSmsBox.isSelected();
        	FundTransferRequest fundTransferRequest = new FundTransferRequest();
        	fundTransferRequest.setAmount(Integer.parseInt(amount));
        	fundTransferRequest.setRecipientMsisdn(dealer);
        	fundTransferRequest.setSendSms(sendSms);
			FundTransferResponse transferResponse = mainApp.getSmartloadInterface().performFundsTransfer(fundTransferRequest);
		    SubmissionStatus submissionStatus = DataAccess.loadSubmissionStatus(transferResponse.getResponseCode().name());
			String status = transferResponse.getError()==null?transferResponse.getResponseCode().name():transferResponse.getError().getMessage();
		    saveTransaction(submissionStatus,status,transferAmount,dealer);
			if (transferResponse.getResponseCode().equals(ResponseCode.SUCCESS)){
			  NumberFormat currency = NumberFormat.getCurrencyInstance();
			   String message = "Successful Transfer of  "+ currency.format(transferAmount)+ " New balance "  + currency.format(transferResponse.getCurrentDealerBalance());
			   log.info(message + " new Dealer balance " + transferResponse.getNewDealerBalance() );
			   DialogFactory.createDialogInformation(DESCRIPTION, "Transfer successful", message);
			   recipientTextField.setText("");
			   amountTextField.setText("");
			} else {
			   DialogFactory.createDialogError("Transfer error", "Error performing transfer", transferResponse.getError().getMessage());
		   }
			mainApp.refresh();
        } catch (Exception e) {
        	DialogFactory.createDialogException("Transfer error", "Error performing transfer", e);
			SubmissionStatus submissionStatus = DataAccess.loadSubmissionStatus(ResponseCode.SYS_ERROR.name());
			saveTransaction(submissionStatus,e.getMessage(),transferAmount,dealer);
		}
    }
    
    
    private void saveTransaction(SubmissionStatus status,String error,BigDecimal amount,String dealerId){
    	Transaction transaction = new Transaction();
    	 NumberFormat currency = NumberFormat.getCurrencyInstance();
    	transaction.setAmount(currency.format(amount));
    	transaction.setDescription(DESCRIPTION);
    	transaction.setDeviceId(dealerId);
    	transaction.setSubmissionStatus(status);
    	transaction.setStatusDate(new Date());
    	transaction.setResponseMessage(error);
    	transaction.setLastFileName("");
    	transaction.setOrderRefNumber("");
    	transaction.setTransactionStatus("Submitted");
    	transaction.setDealer(mainApp.currentDealer);
    	DataAccess.saveOrUpdateTransactionToDB(transaction);
    }
    
    public void setup(){
    	dealerMsisdn.setText(RefPreferences.getDealerMsisdn());
		dealerBalance.setText(mainApp.getDealerBalance());
    	Transaction trans =  DataAccess.getlastTransaction(DESCRIPTION);
    	if (trans!=null)
    		lastTransaction.setText(trans.getBasicDescription());
    	
		
	
    }
    
}	
	

