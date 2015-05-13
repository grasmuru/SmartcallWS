package za.co.smartcall.wsclient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import za.co.smartcall._2010._12.message.DealerBalanceResponse;
import za.co.smartcall._2010._12.message.DealerRegisteredResponse;
import za.co.smartcall._2010._12.message.FundTransferRequest;
import za.co.smartcall._2010._12.message.FundTransferResponse;
import za.co.smartcall._2010._12.message.LastTransactionResponse;
import za.co.smartcall._2010._12.message.ProductRechargeRequest;
import za.co.smartcall._2010._12.message.ProductType;
import za.co.smartcall._2010._12.message.RechargeResponse;
import za.co.smartcall.wsclient.implementation.Bootstrapper;

/**
 * Unit test for the basic smartload Service.
 * File requesting and retrieval is not performed here.
 * All requests go through the smartload interface which needs to be setup 
 * first after the key setup (see CredentailSetup Test)
 * 
 */
public class SmartloadTest {
	
	private SmartloadInterface smartload;
	
	private static final String ENDPOINT_ADDRESS = "http://www.smartcallesb.co.za:8090/SmartcallServices/SmartloadService";
	private static final String WSDL = ENDPOINT_ADDRESS+"?wsdl";
	
	private String msisdn = "27827861225";
	
	@Before
	/**
	 * Initial setup of the basic smartcall service.
	 * @throws Exception
	 */
	public void setUp()  {
		try {
			Bootstrapper strapper = new Bootstrapper();
			smartload =  strapper.setupSmartload(WSDL,msisdn);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Error setting up interfaces " + e.getMessage());
			
		}

	}
	/**
	 * Is your dealer registered for smartcall
	 * Checks whether the user is available for transactioning on smartcall's side
	 * Could be blocked for whatever reason/ or a bad setup 
	 */
	@Test
	public void isDealerRegistered() {
		DealerRegisteredResponse response = smartload.isDealerRegistered(msisdn);
		System.out.println(ReflectionToStringBuilder.toString(response));
		Assert.assertTrue(response.isRegistered());
	}


	/**
	 * tests the basic balance return the amount and make sure that it is above zero
	 * Please make sure smartcall has enough funds in your account for testing,
	 * This is important for other subsequent tests as well
	 */
	@Test
	public void getDealerBalance() {
			DealerBalanceResponse response = smartload.getDealerBalance();
			System.out.println(ReflectionToStringBuilder.toString(response));
			Assert.assertNotNull(response);
			Assert.assertTrue("The contents for you balance were unexpected ",response.getBalance()> 0.10);
	}
	
	/**
	 * tests the act of transferring funds from one account to the other
	 * tests the balances thereafter
	 */
	@Test
	public void performFundsTransfer() {
		int AMOUNT_TO_TRANSFER = 10;
		String MSISDN_RECIPIENT = "27829909779";
		boolean SEND_RECIPIENT_A_INFORMATION_SMS = false; 
		FundTransferRequest request = new FundTransferRequest();
		request.setAmount(AMOUNT_TO_TRANSFER);
		request.setRecipientMsisdn(MSISDN_RECIPIENT);
		request.setSendSms(SEND_RECIPIENT_A_INFORMATION_SMS); // no sms's are sent on the test system
		FundTransferResponse response = smartload.performFundsTransfer(request);
		System.out.println(ReflectionToStringBuilder.toString(response));
		Assert.assertTrue("Account should be now R0" + AMOUNT_TO_TRANSFER,response.getNewDealerBalance()==0);
		FundTransferResponse response2 = smartload.performFundsTransfer(request);
		System.out.println(ReflectionToStringBuilder.toString(response));
		Assert.assertTrue("Second request should fail due to 5 minute ",response2.getError().getMessage()!=null);
	}
	
	/**
	 * get all  Networks then ProductTypes associated with the Network
	 * and then all products associated with the ProductType
	 */
	@Test(timeout=40000)
	public void getProducts(){
		List<za.co.smartcall._2010._12.message.Product> products = new ArrayList<za.co.smartcall._2010._12.message.Product>();
   	    List<za.co.smartcall._2010._12.common.Network> genNetworks = smartload.getNetworks();
   	    Assert.assertTrue("There should be at least 3 networks", genNetworks.size()>3);
        genNetworks.forEach(network->System.out.println(network.getDescription()));
   	    Hashtable<za.co.smartcall._2010._12.common.Network,List<za.co.smartcall._2010._12.message.ProductType>> genProductTypes = new  Hashtable<za.co.smartcall._2010._12.common.Network,List<za.co.smartcall._2010._12.message.ProductType>>();
		genNetworks.forEach(network->genProductTypes.put(network,smartload.getProductTypesForNetwork(network)));
		Assert.assertTrue("There should be at least 3 productTypes",genProductTypes.size()>3);
		for (za.co.smartcall._2010._12.common.Network network : genNetworks) { // must be a way to remove this
			for (ProductType productType :genProductTypes.get(network)) {
				List<za.co.smartcall._2010._12.message.Product> productsList = smartload.getProductForNetworkProductTypes(network, productType);
				products.addAll(productsList);
				}
			}
		Assert.assertTrue("There should be at least 20 products " + products.size(), products.size()>20);
		products.forEach(product->System.out.println(product.getDescription() + " :" + product.getId()));
	}
	
	/**
	 * Does a recharge to a phone number for product 
	 */
	@Test(timeout=5000)
	public void performRechargeWithClientReference() {
		
		try {
			String recipientOfProduct = "27827861225"; // for electricity this could be a meter no
			Long productId = new Long(24); // Telkom Mobile Airtime
			BigDecimal airtime = new BigDecimal(6); 
			
			ProductRechargeRequest rechargeRequest = new ProductRechargeRequest();
			rechargeRequest.setProductId(productId);
			rechargeRequest.setDeviceId(recipientOfProduct); // for electricity this could be a meter no
			rechargeRequest.setAmount(airtime);
			rechargeRequest.setPinless(true);
			rechargeRequest.setSendSms(true);
			rechargeRequest.setSmsRecipientMsisdn(recipientOfProduct); // where the sms must go
			Random generator = new Random(); 
			String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
			RechargeResponse response = smartload.performRechargeWithClientReference(rechargeRequest,uniqueClientReferenceNumber);
			System.out.println(ReflectionToStringBuilder.toString(response));
			Assert.assertNotNull(response.getRecharge().getOrderReferenceId()>0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Error requesting recharge ");
		}
	}
	
	
	@Test
    /**
     * Gets the last transaction associated with you dealer msisdn
     * irrespective.
     * Tests sends a request and checks if the item is returned
     */
	public void getLastTransaction() {
		String recipientOfProduct = "27827861225"; // for electricity this could be a meter no
		Long productId = new Long(24); // Telkom Mobile Airtime
		BigDecimal airtime = new BigDecimal(7); 
		
		ProductRechargeRequest rechargeRequest = new ProductRechargeRequest();
		rechargeRequest.setProductId(productId);
		rechargeRequest.setDeviceId(recipientOfProduct); // for electricity this could be a meter no
		rechargeRequest.setAmount(airtime);
		rechargeRequest.setPinless(true);
		rechargeRequest.setSendSms(true);
		rechargeRequest.setSmsRecipientMsisdn(recipientOfProduct); // where the sms must go
		Random generator = new Random(); 
		String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
		smartload.performRechargeWithClientReference(rechargeRequest,uniqueClientReferenceNumber);
		LastTransactionResponse lastTransaction = smartload.getLastTransaction();
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction));
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction.getTransaction()));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getRecipientMsisdn().equals(recipientOfProduct));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getReference()!=null);
	}
	


	

	/**
     * Gets the last transaction that was linked to the client generated reference number
     * Tests sends a request and checks if the item is returned
     * Please note you can have a duplicate transaction check forcing you to run the test in 5 minutes time or actually change 
     * recharge details
     */
	@Test
	public void getLastTransactionForClientReference() {
		String recipientOfProduct = "27827861225"; // for electricity this could be a meter no
		Long productId = new Long(24); // Telkom Mobile Airtime
		BigDecimal airtime = new BigDecimal(8); 
			
		ProductRechargeRequest rechargeRequest = new ProductRechargeRequest();
		rechargeRequest.setProductId(productId);
		rechargeRequest.setDeviceId(recipientOfProduct); // for electricity this could be a meter no
		rechargeRequest.setAmount(airtime);
		rechargeRequest.setPinless(true);
		rechargeRequest.setSendSms(true);
		rechargeRequest.setSmsRecipientMsisdn(recipientOfProduct); // where the sms must go
		Random generator = new Random(); 
		String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
		RechargeResponse response = smartload.performRechargeWithClientReference(rechargeRequest,uniqueClientReferenceNumber);
		System.out.println(ReflectionToStringBuilder.toString(response));
		if (response.getError()!= null){
			System.out.println(ReflectionToStringBuilder.toString(response.getError().getMessage()));
		}
		Assert.assertEquals("Recharge should be successful","SUCCESS", response.getResponseCode().SUCCESS.name());
		LastTransactionResponse lastTransaction = smartload.getLastTransactionForClientReference(uniqueClientReferenceNumber);
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction));
		Assert.assertNotNull("Should get a transaction back for " +uniqueClientReferenceNumber,lastTransaction.getTransaction());
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction.getTransaction()));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getRecipientMsisdn().equals(recipientOfProduct));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getReference()!=null);
	}
	
	/**
     * Gets the last transaction that was linked to the server generated reference number
     * Tests sends a request and checks if the item is returned
     */
	@Test
	public void getLastTransactionForReference() {
		String recipientOfProduct = "27827861225"; // for electricity this could be a meter no
		Long productId = new Long(24); // Telkom Mobile Airtime
		BigDecimal airtime = new BigDecimal(9); 
			
		ProductRechargeRequest rechargeRequest = new ProductRechargeRequest();
		rechargeRequest.setProductId(productId);
		rechargeRequest.setDeviceId(recipientOfProduct); // for electricity this could be a meter no
		rechargeRequest.setAmount(airtime);
		rechargeRequest.setPinless(true);
		rechargeRequest.setSendSms(true);
		rechargeRequest.setSmsRecipientMsisdn(recipientOfProduct); // where the sms must go
		Random generator = new Random(); 
		String uniqueClientReferenceNumber = Long.toString(generator.nextInt(10000) + 1);
		RechargeResponse response = smartload.performRechargeWithClientReference(rechargeRequest,uniqueClientReferenceNumber);
		long reference = response.getRecharge().getOrderReferenceId();
		LastTransactionResponse lastTransaction = smartload.getLastTransactionForReference(reference);
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction));
		System.out.println(ReflectionToStringBuilder.toString(lastTransaction.getTransaction()));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getRecipientMsisdn().equals(recipientOfProduct));
		Assert.assertTrue("Last transaction was incorrect ",lastTransaction.getTransaction().getReference()!=null);
	}	
	
}

