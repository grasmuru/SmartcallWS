package za.co.smartcall.wsclient.implementation;


import za.co.smartcall._2010._12.message.BatchVoucherRequest;
import za.co.smartcall._2010._12.message.BatchVoucherResponse;
import za.co.smartcall._2010._12.message.RecreateVoucherBatchResponse;
import za.co.smartcall._2010._12.service.SmartBatchService;
import za.co.smartcall.wsclient.SmartloadBatchInterface;


public class SmartloadBatch implements SmartloadBatchInterface {
	
	private SmartBatchService port;
	
	
	
	public SmartloadBatch(SmartBatchService port){
		this.port= port;
	}
	

	@Override
	public BatchVoucherResponse orderVoucherBatch(BatchVoucherRequest batchVoucherRequest,String clientReference) {
		za.co.smartcall._2010._12.message.BatchVoucherRequest request = new za.co.smartcall._2010._12.message.BatchVoucherRequest();
		request.setProductId(batchVoucherRequest.getProductId());
		request.setQuantity(batchVoucherRequest.getQuantity());
		za.co.smartcall._2010._12.message.BatchVoucherResponse response = port.orderVoucherBatch(request, clientReference);
		return response;
		
	}


	@Override
	public RecreateVoucherBatchResponse recreateVoucherBatchFile(String clientReference) {
		za.co.smartcall._2010._12.message.RecreateVoucherBatchResponse response = port.recreateVoucherBatchFile(clientReference);
		return response;
		
	}
	

}
