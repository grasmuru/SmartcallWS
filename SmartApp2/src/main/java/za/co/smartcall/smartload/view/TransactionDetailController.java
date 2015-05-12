package za.co.smartcall.smartload.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.function.Predicate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import org.controlsfx.dialog.Dialogs;

import za.co.smartcall._2010._12.common.ResponseCode;
import za.co.smartcall._2010._12.message.LastTransactionResponse;
import za.co.smartcall._2010._12.message.ProductRechargeRequest;
import za.co.smartcall._2010._12.message.RechargeResponse;
import za.co.smartcall._2010._12.message.RecreateVoucherBatchResponse;
import za.co.smartcall.smartload.CommonUtils;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.hibernate.SubmissionStatus;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.hibernate.Transactions;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class TransactionDetailController {


    private MainApp mainApp;
    
    private Stage dialogStage;
    
    private Transaction transaction;
    
    @FXML
    private Label transactionDescription;
    
    @FXML
    private Label transactionAmount;
        
    @FXML
    private Label transactionNetwork;
    
    @FXML
    private Label transactionRecipient;
    
    @FXML
    private Label transactionReference;
    
    @FXML
    private Label transactionStatus;
    
    @FXML
    private Label transactionDate;
    
    @FXML
    private Label transactionCost;
    
    @FXML
    private Label transactionDiscount;
    
    @FXML
    private Label transactionMessage;
    
    @FXML
    private Label fileName;
    
    @FXML
    private Label password;
    
    @FXML
    private Label orderReferenceNumber;
    
    @FXML
    private Label submissionStatus;
    
    @FXML
    private Label smsRecipient;
    
    @FXML
    private Label voucherPin;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public TransactionDetailController() {
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
    
    public void setTransaction(Transaction transaction) {
        transactionDescription.setText(transaction.getDescription());
        transactionAmount.setText(transaction.getAmount());
        if (transaction.getProduct() !=null ) // for transfers
        	transactionNetwork.setText(transaction.getProduct().getProducttype().getNetwork().getNetwork());
        else transactionNetwork.setText("-");
        transactionRecipient.setText(transaction.getDeviceId());
        transactionReference.setText(transaction.getClientReference());
        transactionStatus.setText(transaction.getTransactionStatus());
        transactionDate.setText(transaction.getStatusDate().toString());
        if (transaction.getProduct() ==null ){
        	transactionCost.setText(transaction.getAmount());
        }
        else if (transaction.getProduct().getProducttype().isFixed()){
        	transactionCost.setText(CommonUtils.currencyFormat(transaction.getProduct().getRetailValue()));
        }
        else {
        	transactionCost.setText(CommonUtils.discountedPrice(transaction.getAmount(), transaction.getProduct().getDiscount()));
        }
        if (transaction.getProduct() !=null )
        	transactionDiscount.setText(transaction.getProduct().getDiscount().toPlainString() + "%");
        transactionMessage.setText(transaction.getResponseMessage());
        submissionStatus.setText(transaction.getSubmissionStatus().getSubStatus());
        voucherPin.setText(transaction.getVoucherPin());
        smsRecipient.setText(transaction.getSmsRecipientMsisdn());
        String file;
		try {
			file = transaction.getFiles().stream().findFirst().get().getFileName();
		} catch (Exception e) {
			log.error(e);
			file = "";
		}
        
        
        fileName.setText(file);
        password.setText(transaction.getPassword());
        orderReferenceNumber.setText(transaction.getOrderRefNumber());
        this.transaction = transaction;
        
     }
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    

    
    @FXML
    private void handleReturn() {
    	DealerView.individualShowing = false;
        dialogStage.close();
    }
    
    @FXML
    private void handleQuery() {
            try {
				String orderRef =  transaction.getClientReference();
				LastTransactionResponse response = mainApp.getSmartloadInterface().getLastTransactionForClientReference(orderRef);
				String tStatus = response.getTransaction().getStatus();
				transaction.setTransactionStatus(tStatus);
				DataAccess.saveOrUpdateTransactionToDB(transaction);
				transactionStatus.setText(tStatus);
				DialogFactory.createDialogInformation("Query", "Get status update", "The latest status of the transaction has been retrieved");
			} catch (Exception e) {
				DialogFactory.createDialogException("Error performing query", "Query error", e);
			}
    }
    
   
    @FXML
    private void handleResend() throws ParseException {
        try {
        	String clientRef = transaction.getClientReference();
        	if (transaction.getSubmissionStatus().equals("SUCCESS")){
        		 DialogFactory.createDialogInformation("Resend", "Resend blocked", "Sucessful transactions cannot be resent");
        		 return;
        	}
			if (transaction.isFile()){
				RecreateVoucherBatchResponse response = mainApp.getSmartloadBatchInterface().recreateVoucherBatchFile(clientRef);
			    if (response.getResponseCode().equals(ResponseCode.APP_ERROR)){
				   transaction.setResponseMessage(response.getError().getMessage());
			    } 
			    else {
			      transaction.setPassword(response.getFilePassword());
			   
				}
			    updateTransaction(transaction);
			}
			 else {
				ProductRechargeRequest productRechargeRequest = new ProductRechargeRequest();
				NumberFormat currency = NumberFormat.getCurrencyInstance();
				if (!transaction.getProduct().getProducttype().isFixed())
					productRechargeRequest.setAmount(new BigDecimal(currency.parse(transaction.getAmount()).toString()));
				productRechargeRequest.setDeviceId(transaction.getDeviceId());
				productRechargeRequest.setPinless(transaction.isPinless());
				productRechargeRequest.setProductId((long)transaction.getProduct().getId());
				
				if (transaction.getDeviceId()!=null){
					productRechargeRequest.setSendSms(true);
					productRechargeRequest.setSmsRecipientMsisdn(transaction.getSmsRecipientMsisdn());
				} else {
					productRechargeRequest.setSendSms(false);
				}
				RechargeResponse rechargeResponse = mainApp.getSmartloadInterface().performRechargeWithClientReference(productRechargeRequest, clientRef);
				String responseCode = rechargeResponse.getResponseCode().name(); 
		    	String error="";
		    	if (rechargeResponse.getError()!=null)
		    		 error = rechargeResponse.getError().getMessage();
		    	 SubmissionStatus submissionStatus = DataAccess.loadSubmissionStatus(responseCode);
		     	transaction.setSubmissionStatus(submissionStatus);
		     	transaction.setResponseMessage(error);
		     	updateTransaction(transaction);			
		     	
			}
		} catch (Exception e) {
			DialogFactory.createDialogException("Error performing resend", "Resend error", e);
		}
    }
    
    private void updateTransaction(Transaction transaction){
    	 int resend = transaction.getResend();
	     resend++;
	     transaction.setResend(resend);
	     transaction.setStatusDate(new Date());
		 transaction.setTransactionStatus("Resend");
    	 DataAccess.saveOrUpdateTransactionToDB(transaction);
    	 DialogFactory.createDialogInformation("Resend", "Item resent", "The item was resent witht the same details");
    }
    
}	
	

