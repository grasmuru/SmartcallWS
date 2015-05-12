package za.co.smartcall.wsclient;

import java.io.IOException;

import za.co.smartcall._2010._12.message.VoucherBatchResponse;

/**
 * Retrieval of files from previous orders
 * @author rudig
 *
 */
public interface SmartloadOrderServiceInterface {
	
	public abstract VoucherBatchResponse getBatchOrder(String clientReference,long orderReference) throws IOException;
	
	public String getDownloadDirectory();
}