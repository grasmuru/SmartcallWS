package za.co.smartcall.wsclient.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import za.co.smartcall._2010._12.common.Network;
import za.co.smartcall._2010._12.message.DealerBalanceResponse;
import za.co.smartcall._2010._12.message.DealerRegisteredResponse;
import za.co.smartcall._2010._12.message.FundTransferRequest;
import za.co.smartcall._2010._12.message.FundTransferResponse;
import za.co.smartcall._2010._12.message.LastTransactionResponse;
import za.co.smartcall._2010._12.message.Product;
import za.co.smartcall._2010._12.message.ProductRechargeRequest;
import za.co.smartcall._2010._12.message.ProductType;
import za.co.smartcall._2010._12.message.RechargeResponse;
import za.co.smartcall._2010._12.service.SmartloadService;
import za.co.smartcall.wsclient.SmartloadInterface;


public class Smartload implements SmartloadInterface {
	
	private SmartloadService port;
	
		
	
	public Smartload(SmartloadService port){
		this.port= port;
	//	
	}
	

	@Override
	public DealerBalanceResponse getDealerBalance() {
		DealerBalanceResponse response = port.getDealerBalance();
		return response;
		
	}

	@Override
	public LastTransactionResponse getLastTransaction() {
		LastTransactionResponse response = port.getLastTransaction();
		return response;
		
		
	}

	@Override
	@Deprecated // doesnt make sense rather call last transaction
	public LastTransactionResponse getLastTransactionForMsisdn(String queryMsisdn) {
		LastTransactionResponse response =port.getLastTransactionForMsisdn(queryMsisdn);
		return response;
		
	}

	@Override
	public LastTransactionResponse getLastTransactionForReference(long queryReference) {
		LastTransactionResponse response =port.getLastTransactionForOrderReference(queryReference);
		return response;
		
	}
	
	@Override
	public LastTransactionResponse getLastTransactionForClientReference(String clientReferenceNumber) {
		LastTransactionResponse response =port.getLastTransactionForClientReference(clientReferenceNumber);
	    return response; 
		
	}
	



	@Override
	public DealerRegisteredResponse isDealerRegistered(String dealerId) {
		DealerRegisteredResponse response = port.isDealerRegistered(dealerId);
	    return response;
	}

	@Override
	//send int and get double back?
	public FundTransferResponse performFundsTransfer(FundTransferRequest fundTransferRequest) {
		FundTransferResponse response = port.performFundsTransfer(fundTransferRequest);
		return response;
	
		
	}

	@Override
	public RechargeResponse performRechargeWithClientReference(ProductRechargeRequest productRechargeRequest,String clientReference) {
		RechargeResponse response =  port.performProductRechargeWithClientReference(productRechargeRequest, clientReference);
	    return response;
		
	}
	
	
	
	
   public List<Network> getNetworks(){
	   return port.getAllNetworks();
   }
  
   
   
   public List<ProductType> getProductTypesForNetwork(Network forNetwork){
	    Predicate<Network> filter = (network->network.getId()==forNetwork.getId());
	   Network selectedNetwork = getNetworks().stream().filter(filter).findFirst().get();
	   List<ProductType> networksProductTypes = selectedNetwork.getProductTypes();
	   return networksProductTypes;
   } 

   public List<Product> getProductForNetworkProductTypes(Network forNetwork,ProductType forProductType){
	   Predicate<Network> filter = (network->network.getId()==forNetwork.getId());
	   Network selectedNetwork = getNetworks().stream().filter(filter).findFirst().get();
	   List<ProductType> networksProductTypes = selectedNetwork.getProductTypes();
	   Predicate<ProductType> filter2 = (productType->productType.getId()==forProductType.getId());
	   if (networksProductTypes.stream().filter(filter2).count()==0) return new ArrayList<Product>();
	   ProductType selectedProductType = networksProductTypes.stream().filter(filter2).findFirst().get();
	   List<Product> networksProductTypesProducts = selectedProductType.getProducts();
	   return networksProductTypesProducts;
   }   
   


	
	
	
    

}
