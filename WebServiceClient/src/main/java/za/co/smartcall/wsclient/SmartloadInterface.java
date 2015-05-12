package za.co.smartcall.wsclient;

import java.util.List;

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


/**
 * Interface for smartload functionality related to balance check,transfer,trnsaction query and ordering of pinless 
 * and single voucher requests
 * @author rudig
 *
 */
public interface SmartloadInterface {
	
	public abstract DealerBalanceResponse getDealerBalance();

	public abstract LastTransactionResponse getLastTransaction();

	@Deprecated
	public abstract LastTransactionResponse getLastTransactionForMsisdn(String msisdn);

	public abstract LastTransactionResponse getLastTransactionForReference(long queryReference);
	
	public abstract LastTransactionResponse getLastTransactionForClientReference(String clientReferenceNumber);

	public abstract DealerRegisteredResponse isDealerRegistered(String dealerId);

	public abstract FundTransferResponse performFundsTransfer(FundTransferRequest fundTransferRequest);

	public RechargeResponse performRechargeWithClientReference(ProductRechargeRequest productRechargeRequest,String clientReference);

	public abstract List<Network> getNetworks();
	
    public List<ProductType> getProductTypesForNetwork(Network forNetwork);

	public List<Product> getProductForNetworkProductTypes(Network forNetwork,ProductType forProductType);
	   
	
}