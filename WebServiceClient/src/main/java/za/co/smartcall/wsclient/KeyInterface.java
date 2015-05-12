package za.co.smartcall.wsclient;

/**
 * Interfface related to the automation of the ws-securiry setup
 * @author rudig
 *
 */
public interface KeyInterface {

	public abstract void createPrivateKeystore(String msisdn,String password) throws DealerSetupException;
	
	public abstract void addSmartcallPublicKey(String password) throws DealerSetupException;
	
	public void updateDealerDetailsInFile(String msisdn,String password) throws DealerSetupException;
	
	public String getKeyLocation();
	
	public void setHomeLocation(String directory);
	
	public String getHomeLocation();
	
	public String getConfigLocation();
	
}
