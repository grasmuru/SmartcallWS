package za.co.smartcall.smartload.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.log4j.Log4j;
import za.co.smartcall._2010._12.message.ProductRechargeRequest;
import za.co.smartcall._2010._12.message.RechargeResponse;
import za.co.smartcall.smartload.CommonUtils;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.InputConstraints;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefGenerator;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.Network;
import za.co.smartcall.smartload.hibernate.Product;
import za.co.smartcall.smartload.hibernate.ProductTypes;
import za.co.smartcall.smartload.hibernate.Products;
import za.co.smartcall.smartload.hibernate.Producttype;
import za.co.smartcall.smartload.hibernate.SubmissionStatus;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class PerformRechargeView {

    // Reference to the main application.
    private MainApp mainApp;
    
    @FXML
    private ComboBox<String> networkSelection;
    
    @FXML
    private ComboBox<String> productTypeSelection;
    
    @FXML
    private ComboBox<String> productSelection;
    
    @FXML
    private CheckBox pinless;
    
    @FXML
    private CheckBox sendSms;
    
    @FXML
    private TextField productAmount;
    
    @FXML
    private TextField recipientMsisdn;
    
    @FXML
    private TextField rechargeDeviceId;
    
        
    private Product selectedProduct;
    
    private Producttype selectedProductType;
    
    private Network selectedNetwork;
    
    @FXML
    private Label dealerMsisdn;
    
    @FXML
    private Label lastTransaction;
        
    @FXML
    private Label dealerBalance;
    
    public static final String DESCRIPTION = "Sell";
    

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PerformRechargeView() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	 pinless.setDisable(true);
    	 pinless.setSelected(false);
    	 InputConstraints.numbersOnly(productAmount, 6);
    	 InputConstraints.numbersOnly(recipientMsisdn, 10);
    	 lastTransaction.setText("-");
    	
      }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public void setup(){
    	 
    	
			dealerMsisdn.setText(RefPreferences.getDealerMsisdn());
			dealerBalance.setText(mainApp.getDealerBalance());
	

	       	Transaction trans = DataAccess.getlastTransaction(DESCRIPTION);
	    	if (trans != null)
	    		lastTransaction.setText(trans.getBasicDescription());
			
	    	networkSelection.getItems().clear();			
			List<Network> networks = DataAccess.loadNetworksFromDB().getNetworks();
			networks.sort((p1, p2) -> Integer.valueOf(p1.getId()).compareTo(Integer.valueOf(p2.getId())));
			networks.forEach(network->networkSelection.getItems().add(network.getNetwork()));
			networkSelection.setValue(networks.get(0).getNetwork());
			
	        rechargeDeviceId.setDisable(true);
    }
    
    @FXML
    private void handleSendSms(){
    	if (sendSms.isSelected()) {
    		rechargeDeviceId.setDisable(true);
    	}
    	rechargeDeviceId.setDisable(false);
    }
    

    @FXML
    private void handleNetworkSelection() {
    	
    	productTypeSelection.getItems().clear();
    	productSelection.getItems().clear();
    	 pinless.setSelected(false);
    	
        productTypeSelection.setDisable(false);
        String networkChoice = networkSelection.getValue();
        if (networkChoice==null) return;
       
    	selectedNetwork = DataAccess.getNetworkWithName(networkChoice);
        
    	List<Producttype> possibleProducttypes = DataAccess.getProductTypesForNetwork(selectedNetwork);
    	possibleProducttypes.forEach(productType->productTypeSelection.getItems().add(productType.getDescription()));
        String defaultValue = possibleProducttypes.get(0).getDescription();
        productTypeSelection.setValue(defaultValue);
    }
    
    @FXML
    private void handleProductTypeSelection() {
    	 productSelection.setDisable(false);
    	 String productTypeChoice = productTypeSelection.getValue();
         if (productTypeChoice==null) return;  
         productSelection.getItems().clear();
    	     	 
    	 selectedProductType = DataAccess.getProductTypeFromNameAndNetwork(productTypeChoice, selectedNetwork);
         List<Product> possibleProducts = DataAccess.getProductsForProductType(selectedProductType);
         possibleProducts.forEach(product->productSelection.getItems().add(product.getDescription()));
    	 if (selectedProductType.isFixed()){
    		 productSelection.setDisable(false);
    		 productAmount.setDisable(true);
    		 String defaultValue = possibleProducts.get(0).getDescription();
    	     productSelection.setValue(defaultValue);
       	 } else {
    		 selectedProduct = possibleProducts.get(0);
    	     productAmount.setDisable(false);
    		 productSelection.setDisable(true);
    	 }
    }
    
    @FXML
    private void handleProductSelection() {
    	
    	 String networkChoice = networkSelection.getValue();
    	 String productTypeChoice = productTypeSelection.getValue();
    	 String productChoice = productSelection.getValue();
    	 
    	 if (productTypeChoice==null) return;  
    	 if (productChoice==null) return; 
    	 
         selectedNetwork = DataAccess.getNetworkWithName(networkChoice);
    	 selectedProductType =  DataAccess.getProductTypeFromNameAndNetwork(productTypeChoice, selectedNetwork);
    	 selectedProduct = DataAccess.getProductFromNameAndNetworkAndProductType(productChoice,selectedNetwork,selectedProductType);
		 pinless.setSelected(!selectedProduct.isPinIndicator());
    }
    
    
    @FXML
    private void perfromRecharge() {
    	RechargeResponse recharge = null;;
        try {
        	ProductRechargeRequest rechargeRequest = new ProductRechargeRequest();
        	if (!productAmount.getText().equals(""))
        		rechargeRequest.setAmount(new BigDecimal(productAmount.getText()));
        	rechargeRequest.setPinless(!selectedProduct.isPinIndicator());
        	rechargeRequest.setDeviceId(rechargeDeviceId.getText());
        	if (recipientMsisdn.getText().startsWith("0"))
        		rechargeRequest.setDeviceId(recipientMsisdn.getText().replaceFirst("0", "27"));
        	rechargeRequest.setSendSms(sendSms.isSelected()); 
        	if (selectedProduct != null)
        		rechargeRequest.setProductId(new Long(selectedProduct.getId()));
           	String ref = RefGenerator.getInstance().getCount();
			recharge = mainApp.getSmartloadInterface().performRechargeWithClientReference(rechargeRequest,ref);
			returnRechargeResult(recharge);
			saveTransaction(recharge, rechargeRequest,ref);
		} catch (Exception e) {
			DialogFactory.createDialogException("Submission error", recharge.getError()==null?recharge.getResponseCode().value():recharge.getError().getMessage(), e);
		}
        mainApp.refresh();
    }
    

    private void saveTransaction(RechargeResponse rechargeResponse,ProductRechargeRequest rechargeRequest,String ref){
    	String responseCode = rechargeResponse.getResponseCode().name(); 
    	String error="";
    	if (rechargeResponse.getError()!=null)
    		 error = rechargeResponse.getError().getMessage();
    	Transaction transaction = new Transaction();
    	if (selectedProductType.isFixed()){
    	   	transaction.setAmount(selectedProduct.getDescription());
    	} else {
    		 NumberFormat currency = NumberFormat.getCurrencyInstance();
    		 if (rechargeRequest.getAmount()==null) rechargeRequest.setAmount(new BigDecimal(0));
    		 transaction.setAmount(currency.format(rechargeRequest.getAmount()));
    	}
    	transaction.setDescription(DESCRIPTION + " "+ selectedProductType.getDescription());
    	transaction.setDeviceId(rechargeRequest.getDeviceId());
    	 SubmissionStatus submissionStatus = DataAccess.loadSubmissionStatus(responseCode);
    	transaction.setSubmissionStatus(submissionStatus);
    	transaction.setStatusDate(new Date());
    	transaction.setResponseMessage(error);
    	transaction.setClientReference(ref);
    	transaction.setFile(false);
    	if (rechargeResponse.getResponseCode().value().equals("SUCCESS"))
    		transaction.setTransactionStatus("Submitted");
    	else 
    		transaction.setTransactionStatus(rechargeResponse.getResponseCode().value());
    	transaction.setPinless(rechargeRequest.isPinless());
    	transaction.setDeviceId(rechargeRequest.getDeviceId());
       	transaction.setProduct(DataAccess.findProduct(rechargeRequest.getProductId()));
    	if (rechargeResponse.getRecharge()!=null){
    		transaction.setOrderRef(rechargeResponse.getRecharge().getOrderReferenceId());
	    	transaction.setVoucherPin(rechargeResponse.getRecharge().getVoucherPin());
	    	transaction.setBoxnumber(rechargeResponse.getRecharge().getBoxNumber());
	    	transaction.setBatchnumber(rechargeResponse.getRecharge().getBatchNumber());
	    	transaction.setTicketNumber(rechargeResponse.getRecharge().getTicketNumber());
    	} else transaction.setOrderRefNumber(ref);
      	transaction.setLastFileName("");
    	transaction.setDealer(mainApp.currentDealer);
      	DataAccess.saveOrUpdateTransactionToDB(transaction);
    }   
    
    public void returnRechargeResult(RechargeResponse message){
    	String display = "";
    	if (message.getResponseCode()!=null){
    		display = message.getResponseCode().name();
    	}
    	if (message.getError()!=null){
    		display = display  + " " + message.getError().getMessage();
    	}
    	DialogFactory.createDialogInformation("Recharge", "Recharge successful", display);
   }
    

    
    
}	
	

