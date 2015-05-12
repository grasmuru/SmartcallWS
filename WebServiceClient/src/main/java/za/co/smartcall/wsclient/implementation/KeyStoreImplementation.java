package za.co.smartcall.wsclient.implementation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import za.co.smartcall.wsclient.DealerSetupException;
import za.co.smartcall.wsclient.KeyInterface;
import za.co.smartcall.wsclient.implementation.keys.CreateKeyPair;
import za.co.smartcall.wsclient.implementation.keys.ImportKey;

@Log4j
public class KeyStoreImplementation implements KeyInterface {
	
	public static final String keyStoreProperties = "clientKeystore.properties";
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static String APP_HOME = System.getProperty("user.dir");
	
	public static final String CONFIG_HOME = APP_HOME+FILE_SEPARATOR+"config"+FILE_SEPARATOR+"keys";
	
	public static final String URLPATH ="file:/"+ APP_HOME+FILE_SEPARATOR+"config"+FILE_SEPARATOR+"keys";
	
	public static final String smartcallCertificate = "smartcallservices.cer";
	
	public static final String clientKeyStore = "clientKeystore.jks";
	
	public static final String FILE_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.file";
	
	public static final String PASSWORD_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.keystore.password";
	
	public static final String ALIAS_PROPERTY_TAG = "org.apache.ws.security.crypto.merlin.keystore.alias";

	@Override
	public void createPrivateKeystore(String msisdn,String password) throws DealerSetupException{
		try {
			log.info("Create client keystore certificate");
			CreateKeyPair keyPair = new CreateKeyPair();
			keyPair.createKeyPair(msisdn, password);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error adding smartcall public key",e);
			throw new DealerSetupException(e);
		}
	}

	@Override
	public void addSmartcallPublicKey(String password) throws DealerSetupException{
		try {
			log.info("Adding smartcall certificate");
			ImportKey importKey = new ImportKey();
			importKey.addKey(smartcallCertificate, clientKeyStore, password);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error adding smartcall public key",e);
			throw new DealerSetupException(e);
		}
	}
	
	public String getKeystorePassword() throws IOException{
		InputStream keystoreStream = this.getClass().getClassLoader().getResourceAsStream(keyStoreProperties);
		Properties prop = new Properties();
		prop.load(keystoreStream);
		String password = prop.getProperty(PASSWORD_PROPERTY_TAG);
		return password;
	}
	
	public String getKeystoreFile() throws IOException{
		InputStream keystoreStream = this.getClass().getClassLoader().getResourceAsStream(keyStoreProperties);
		Properties prop = new Properties();
		prop.load(keystoreStream);
		String password = prop.getProperty(FILE_PROPERTY_TAG);
		return password;
	}
	
	
	 
	/**
	 * Create a file that has all the keystore properties and the user specific details 
	 */
	public void updateDealerDetailsInFile(String msisdn,String password) throws DealerSetupException{
		try {
			String keystorePropertiesFileLocation = CONFIG_HOME + FILE_SEPARATOR + keyStoreProperties;
			File file = new File(keystorePropertiesFileLocation);
			PropertiesConfiguration config = new PropertiesConfiguration(file);
			config.setProperty(PASSWORD_PROPERTY_TAG, password);
			config.setProperty(ALIAS_PROPERTY_TAG, msisdn);
			config.setProperty(FILE_PROPERTY_TAG, CONFIG_HOME+FILE_SEPARATOR+clientKeyStore);
			config.save();
			log.info("Saved keystore properties file as " + keystorePropertiesFileLocation);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			log.error("Error in dealer detail setup",e);
			throw new DealerSetupException(e.getMessage());
		}
	}
	
	public String getKeyLocation(){
		return CONFIG_HOME;
	
	}
	
	public void setHomeLocation(String directory){
		APP_HOME = directory;
	}
	
	public String getHomeLocation(){
		return APP_HOME;
	}	
	
	public String getConfigLocation(){
		return CONFIG_HOME;
	}	
	
}
