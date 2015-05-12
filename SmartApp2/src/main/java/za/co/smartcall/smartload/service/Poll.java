package za.co.smartcall.smartload.service;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javafx.application.Platform;
import lombok.extern.log4j.Log4j;
import za.co.smartcall._2010._12.common.ResponseCode;
import za.co.smartcall._2010._12.message.LastTransactionResponse;
import za.co.smartcall._2010._12.message.VoucherBatchResponse;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.hibernate.Transactions;
import za.co.smartcall.smartload.model.DataAccess;
import za.co.smartcall.wsclient.SmartloadInterface;
import za.co.smartcall.wsclient.SmartloadOrderServiceInterface;

@Log4j
public class Poll implements Runnable {
	
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private MainApp mainApp;
    
    
	public Poll(MainApp mainApp) {
		this.mainApp = mainApp;
	}


	@Override
	public void run() {
		Platform.runLater(() -> {
		try {
			log.debug("Try again");
			
			List<Transaction> orderRequests = DataAccess.getAllSuccessfulUnRetrievedFileOrders();
			
			log.debug("Outstanding Batch order transactions found " +  orderRequests.size());
			for (Transaction transaction : orderRequests) {
				String clientRef = transaction.getClientReference();
				log.info(clientRef + " " + transaction.getOrderRefNumber() + " " +mainApp.getSmartloadBatchOrderInterface().getDownloadDirectory());
				
				VoucherBatchResponse result = mainApp.getSmartloadBatchOrderInterface().getBatchOrder(clientRef, transaction.getOrderRef());
			    log.info("Result " + result.getResponseCode());
				if (result.getResponseCode().equals(ResponseCode.SUCCESS)){
					updateTransaction(transaction, result);
					mainApp.refresh();
				} else  {
					log.info("no success " + result.getResponseCode().name());
				}
			}
			if (orderRequests.size()==0) {
				log.debug("no batch orders pending");
			}
			List<Transaction> noStatusTransactions = DataAccess.getAllSuccessfulSubmittedNonFinalState();
			
			
			log.debug("NoStatus transactions found " +  noStatusTransactions.size());
			
			for (Transaction transaction : noStatusTransactions) {
				    String clientRef =  transaction.getClientReference();
		            LastTransactionResponse response = mainApp.getSmartloadInterface().getLastTransactionForClientReference(clientRef);
		        	if (response.getResponseCode().equals(ResponseCode.SUCCESS)){
						transaction.setTransactionStatus(response.getTransaction().getStatus());
						DataAccess.saveOrUpdateTransactionToDB(transaction);
						mainApp.refresh();
					} else  {
						log.debug("no success " + response.getResponseCode().name());
					}
			}
			if (noStatusTransactions.size()==0) {
				log.debug("no transaction updates pending");
			}
			
			
			
		} catch (Exception e) {
			log.error("Something wrong",e);
		}
		});
	}
	
	public void stop(){
		  Platform.exit();
	}
	
	 
	 private void updateTransaction(Transaction transaction,VoucherBatchResponse result){
		    
		   	transaction.setRetrieved(true);
		   	transaction.setStatusDate(new Date());
		   	DataAccess.saveOrUpdateTransactionToDB(transaction);
		   	
	    	File info = new File();
	    	info.setReceiveddate(new Date());
	    	info.setFileName(result.getFileName());
	    	info.setExtracted(false);
	    	info.setContent(transaction.getDescription());
	    	info.setDownloadLocation(mainApp.getSmartloadBatchOrderInterface().getDownloadDirectory());
	    	info.setTransaction(transaction);
	    	info.setExtractLocation("");
	    	DataAccess.saveFileToDB(info);
	    }   
	 

	   

}
