package za.co.smartcall.wsclient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;

/**
 * Unit the tests the creation of a public/private key
 */
public class CredentialSetupTest {
	
	private KeyInterface keyInterface;
	
	private static final String TEST_USER = "27725797942";

	
	@Before
	public void setUp() throws Exception {
		keyInterface = new KeyStoreImplementation();
	}

    @Test
    /**
     * Unit the tests the creation of a public/private key
     * This can be used once to create a key pair for further tests.
     * No test is actually performed 
     */
    @Ignore
    public void performFullTest(){
    	try {
		    String msisdn = TEST_USER;
			String password = msisdn;
			keyInterface.updateDealerDetailsInFile(msisdn,password);
			keyInterface.createPrivateKeystore(msisdn, password);
			keyInterface.addSmartcallPublicKey(password);
		} catch (DealerSetupException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
    }
}

