package za.co.smartcall.wsclient.implementation;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import lombok.extern.log4j.Log4j;

import org.apache.cxf.ws.security.SecurityConstants;

import za.co.smartcall._2010._12.service.SmartBatchService;
import za.co.smartcall._2010._12.service.SmartBatchService_Service;
import za.co.smartcall._2010._12.service.SmartloadOrderService;
import za.co.smartcall._2010._12.service.SmartloadOrderService_Service;
import za.co.smartcall._2010._12.service.SmartloadService;
import za.co.smartcall._2010._12.service.SmartloadService_Service;
import za.co.smartcall.wsclient.SmartloadBatchInterface;
import za.co.smartcall.wsclient.SmartloadInterface;
import za.co.smartcall.wsclient.SmartloadOrderServiceInterface;

/**
 * Basic Initialisation of all three portions of the current smartcall webservice
 * All three interfaces involve the same step
 * Create port from generated stub code
 * Setup ws-security part through the setup wsse
 * return the implemntation of the interface to the user so that they only need to use this to call funstions
 * @author rudig
 *
 */
@Log4j
public class Bootstrapper {

	public SmartloadInterface smartload;
	
	public URL url;
	
	public Bootstrapper() throws MalformedURLException {
		String urlString = KeyStoreImplementation.URLPATH+ KeyStoreImplementation.FILE_SEPARATOR + KeyStoreImplementation.keyStoreProperties;
		url = new URL(urlString);
		log.info("Boottrapper created with " + urlString);
	}
	/**
	 * Return the smartload interface. All pinless, single voucher transactions queries and balance requests 
	 * are done through here
	 * @param wsdl the destination of smartcall's wsdl
	 * @param msisdn your account id for which you have setup your keys
	 * @return smartload interface where you can call all funstions 
	 * @throws MalformedURLException if your wsdl is dodge
	 */
	public SmartloadInterface setupSmartload(String wsdl,String msisdn) throws MalformedURLException  {
		SmartloadService port = getSmartloadProxy(wsdl);
		setupWsseForSmartload(port,msisdn,wsdl);
		return new Smartload(port);
	}
	/**
	 * Return the order batch interface. You can order your files and perfrom a reorder if required
	 * Reception of files is not done through this interface
	 * @param wsdl the destination of smartcall's wsdl
	 * @param msisdn your account id for which you have setup your keys
	 * @return smartload interface where you can call all funstions 
	 * @throws MalformedURLException if your wsdl is dodge
	 */
	public SmartloadBatchInterface setupSmartloadBatch(String wsdl,String msisdn) throws MalformedURLException  {
		SmartBatchService port = getSmartloadBatchProxy(wsdl);
		setupWsseForSmartBatch(port,msisdn,wsdl);
		return new SmartloadBatch(port);
	}
	/**
	 * Return the file after performing the order
	 * Receive the zipped encrypted file through this interface
	 * @param wsdl the destination of smartcall's wsdl
	 * @param msisdn your account id for which you have setup your keys
	 * @param where you want the file saved
	 * @return smartload interface where you can call all funstions 
	 * @throws MalformedURLException if your wsdl is dodge
	 */
	public SmartloadOrderServiceInterface setupSmartloadOrderBatch(String wsdl,String msisdn,String directory) throws MalformedURLException {
		SmartloadOrderService port = getSmartloadOrderProxy(wsdl);
		setupWsseForSmartOrderBatch(port,msisdn,wsdl);
		return new SmartloadOrderBatch(port,directory);
	}

	
    /*
     * get generated code after passing through wsdl
     */
	private SmartloadService getSmartloadProxy(String wsdl) throws MalformedURLException {
		QName serviceName = SmartloadService_Service.SERVICE;
		URL wsdlURL = new URL(wsdl);
		Service service = Service.create(wsdlURL, serviceName);
		SmartloadService port = (SmartloadService) service.getPort(SmartloadService.class);
		return port;
	}
	/*
     * get generated code after passing through wsdl
     */
	private SmartBatchService getSmartloadBatchProxy(String wsdl) throws MalformedURLException {
		QName serviceName = SmartBatchService_Service.SERVICE;
		URL wsdlURL = new URL(wsdl);
		Service service = Service.create(wsdlURL, serviceName);
		SmartBatchService port = (SmartBatchService) service.getPort(SmartBatchService.class);
		return port;
	}
	/*
     * get generated code after passing through wsdl
     */
	private SmartloadOrderService getSmartloadOrderProxy(String wsdl) throws MalformedURLException {
		QName serviceName = SmartloadOrderService_Service.SERVICE;
		URL wsdlURL = new URL(wsdl);
		Service service = Service.create(wsdlURL, serviceName);
		SmartloadOrderService port = (SmartloadOrderService) service.getPort(SmartloadOrderService.class);
		return port;
	}


	
	
	private void setupWsseForSmartload(SmartloadService in_port,String msisdn,String wsdl) throws MalformedURLException {
		((BindingProvider) in_port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsdl);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback(msisdn));
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_USERNAME, msisdn);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_USERNAME, "smartcallservices");
	}	
	
	

	private void setupWsseForSmartBatch(SmartBatchService in_port,String msisdn,String wsdl) {
		((BindingProvider) in_port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsdl);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback(msisdn));
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_USERNAME, msisdn);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_USERNAME, "smartcallservices");
	}	
	
	private void setupWsseForSmartOrderBatch(SmartloadOrderService in_port,String msisdn,String wsdl) {
		((BindingProvider) in_port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsdl);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback(msisdn));
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_PROPERTIES, url);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.SIGNATURE_USERNAME, msisdn);
		((BindingProvider) in_port).getRequestContext().put(SecurityConstants.ENCRYPT_USERNAME, "smartcallservices");
	}	
	
}
