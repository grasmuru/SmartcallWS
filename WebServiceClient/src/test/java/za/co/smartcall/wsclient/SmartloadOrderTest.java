package za.co.smartcall.wsclient;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import za.co.smartcall._2010._12.message.BatchVoucherRequest;
import za.co.smartcall._2010._12.message.BatchVoucherResponse;
import za.co.smartcall._2010._12.message.RecreateVoucherBatchResponse;
import za.co.smartcall._2010._12.message.VoucherBatchResponse;
import za.co.smartcall.wsclient.implementation.Bootstrapper;

/**
 * Unit test for simple App.
 */
public class SmartloadOrderTest {
	
	private SmartloadBatchInterface smartloadBatch;
	
	private SmartloadOrderServiceInterface smartloadBatchOrder;
	
	private static final String ENDPOINT_ADDRESS_BATCH = "http://www.smartcallesb.co.za:8091/SmartcallServices2/SmartBatchService";
	private static final String WSDL_BATCH = ENDPOINT_ADDRESS_BATCH+"?wsdl";
	
	private static final String ENDPOINT_ADDRESS_ORDER = "http://www.smartcallesb.co.za:8091/SmartcallServices2/SmartloadOrderService";
	private static final String WSDL_ORDER = ENDPOINT_ADDRESS_ORDER+"?wsdl";
	
	private String msisdn = "27725797942";
	
	private static final String TEST_FILE_LOCATION = System.getProperty("java.io.tmpdir");
	
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	@Before
	public void setUp() throws Exception {
		Bootstrapper strapper = new Bootstrapper();
		smartloadBatch =  strapper.setupSmartloadBatch(WSDL_BATCH,msisdn);
		smartloadBatchOrder = strapper.setupSmartloadOrderBatch(WSDL_ORDER, ENDPOINT_ADDRESS_ORDER, TEST_FILE_LOCATION);
	}

	/**
	 * tests the ordering of vouchers in file format
	 * The first step of ordering and getting a reference numebr and password for your test
	 */
	@Test(timeout=10000)
	@Ignore
	public void createVoucherRequest() {
		
		try {
			int numberOfVouchers = 2; // keep small so test reserve is not depleted too quickly
			int productId = 66; // these are Vodacom R5 vouchers
			
			BatchVoucherRequest batchVoucherRequest = new BatchVoucherRequest();
			batchVoucherRequest.setQuantity(numberOfVouchers);
			batchVoucherRequest.setProductId(productId);

			Random generator = new Random(); 
			String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
			
			BatchVoucherResponse response = smartloadBatch.orderVoucherBatch(batchVoucherRequest,uniqueClientReferenceNumber);
			Assert.assertTrue("Could not order a voucher file",response.getFilePassword()!=null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exceptions should be thrown");
		}
	} 
	/**
	 * tests the ordering of vouchers in file format and then the reordering.
	 * A new file is generated but with a new password.
	 */
	@Test(timeout=20000)
	@Ignore
	public void recreateVoucherRequest() {
		try {
			int numberOfVouchers = 2; // keep small so test reserve is not depleted too quickly
			int productId = 66; // these are Vodacom R5 vouchers
			BatchVoucherRequest batchVoucherRequest = new BatchVoucherRequest();
			batchVoucherRequest.setQuantity(numberOfVouchers);
			batchVoucherRequest.setProductId(productId);
			Random generator = new Random(); 
			String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
			BatchVoucherResponse response = smartloadBatch.orderVoucherBatch(batchVoucherRequest,uniqueClientReferenceNumber);
			Assert.assertTrue("Could not order a voucher file",response.getFilePassword()!=null);
			RecreateVoucherBatchResponse response2 = smartloadBatch.recreateVoucherBatchFile(uniqueClientReferenceNumber);
			Assert.assertTrue("Could not order a voucher file",response.getFilePassword()!=null);
			Assert.assertTrue("Passwords should not be the same",response.getFilePassword().equals(response2.getFilePassword()));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exceptions should be thrown");
		}
	}	
	
	/**
	 * tests the ordering of vouchers and then the retrieval of the file.
	 * The unzipping of the file is also attempted with the file
	 */
	@Test(timeout=70000)
	@Ignore
	public void orderAndRetrieveFile() {
		try {
			int numberOfVouchers = 1; // keep small so test reserve is not depleted too quickly
			int productId = 35; // these are Vodacom R5 vouchers
			BatchVoucherRequest batchVoucherRequest = new BatchVoucherRequest();
			batchVoucherRequest.setQuantity(numberOfVouchers);
			batchVoucherRequest.setProductId(productId);
			Random generator = new Random(); 
			String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
			BatchVoucherResponse response = smartloadBatch.orderVoucherBatch(batchVoucherRequest,uniqueClientReferenceNumber);
			System.out.println(response);
			Assert.assertTrue("There should be no error ",response.getError()==null);
			String password = response.getFilePassword();
			System.out.println(password);
			TimeUnit.SECONDS.sleep(60);
			VoucherBatchResponse batchresponse =  smartloadBatchOrder.getBatchOrder(uniqueClientReferenceNumber, response.getRecharge().getOrderReferenceId());
			String fileName = batchresponse.getFileName();
			Assert.assertNotNull(fileName);
			String fileLocation = TEST_FILE_LOCATION + FILE_SEPARATOR + fileName;
			System.out.println(fileLocation);
			ZipFile zipFile = new ZipFile(fileLocation);
			
			if (zipFile.isEncrypted()) {
				// if yes, then set the password for the zip file
				zipFile.setPassword(password);
			}
			
			// Get the list of file headers from the zip file
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			fileHeaderList.forEach(
			    fileHeader->	 {
				 try {
				    zipFile.extractFile(fileHeader, TEST_FILE_LOCATION);
			     } catch (ZipException ze){
				//do nothing
			     }
			     }
			    );
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exceptions should be thrown");
		}
	}	
	
	@Test(timeout=70000)
	@Ignore
	public void orderAndRetrieveFileDud() {
		try {

			VoucherBatchResponse batchresponse =  smartloadBatchOrder.getBatchOrder("116", 345654);
			System.out.println(batchresponse);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exceptions should be thrown");
		}
	}	
}

