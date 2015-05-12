package za.co.smartcall.wsclient.implementation;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.extern.log4j.Log4j;
import za.co.smartcall._2010._12.message.VoucherBatchResponse;
import za.co.smartcall._2010._12.service.SmartloadOrderService;
import za.co.smartcall.wsclient.SmartloadOrderServiceInterface;

@Log4j
public class SmartloadOrderBatch implements SmartloadOrderServiceInterface {
	
	private SmartloadOrderService port;
	
	private String directory;
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public SmartloadOrderBatch(SmartloadOrderService port,String directory){ 
		this.port= port;
		this.directory = directory;
	}
	

	public VoucherBatchResponse getBatchOrder(String clientReference,long orderReference) throws IOException{
		VoucherBatchResponse voucherBatchResponse = port.getVoucherBatchOrder(clientReference, orderReference);
		String fileName = voucherBatchResponse.getFileName();
		File f = new File(directory);
		f.mkdirs();
		try (final OutputStream out = new FileOutputStream(directory + FILE_SEPARATOR + fileName)){
			voucherBatchResponse.getFile().writeTo(out);
		} 
		return voucherBatchResponse;
	
	}
	
	public String getDownloadDirectory(){
		return directory;
	}
	
	
  

}
