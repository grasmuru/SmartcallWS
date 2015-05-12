package za.co.smartcall.wsclient;

import za.co.smartcall._2010._12.message.BatchVoucherRequest;
import za.co.smartcall._2010._12.message.BatchVoucherResponse;
import za.co.smartcall._2010._12.message.RecreateVoucherBatchResponse;
/**
 * Ordering of files of vouchers
 * @author rudig
 *
 */
public interface SmartloadBatchInterface {
	
	public BatchVoucherResponse orderVoucherBatch(BatchVoucherRequest batchVoucherRequest,String clientReference);

	public RecreateVoucherBatchResponse recreateVoucherBatchFile(String clientReference);
	
}