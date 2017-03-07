package za.co.smartcall.smartload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import za.co.smartcall._2010._12.message.DealerBalanceResponse;
import za.co.smartcall._2010._12.message.ProductType;
import za.co.smartcall.smartload.hibernate.Dealer;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Networks;
import za.co.smartcall.smartload.hibernate.ProductTypes;
import za.co.smartcall.smartload.hibernate.Products;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.model.DataAccess;
import za.co.smartcall.smartload.service.PollForFiles;
import za.co.smartcall.smartload.view.FileDetailController;
import za.co.smartcall.smartload.view.InitialSetupDialogController;
import za.co.smartcall.smartload.view.ModifyUrlController;
import za.co.smartcall.smartload.view.RootLayoutController;
import za.co.smartcall.smartload.view.SettingsController;
import za.co.smartcall.smartload.view.SetupCertificateController;
import za.co.smartcall.smartload.view.TransactionDetailController;
import za.co.smartcall.smartload.view.UseExistingKeystoreController;
import za.co.smartcall.wsclient.KeyInterface;
import za.co.smartcall.wsclient.SmartloadBatchInterface;
import za.co.smartcall.wsclient.SmartloadInterface;
import za.co.smartcall.wsclient.SmartloadOrderServiceInterface;
import za.co.smartcall.wsclient.implementation.Bootstrapper;
import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;

@Log4j
public class MainApp extends Application {
	
	private Stage primaryStage;
	
    private BorderPane rootLayout;
    
    private RootLayoutController rootController;
    
    @Getter @Setter private SmartloadInterface smartloadInterface;
    
    @Getter @Setter private SmartloadBatchInterface smartloadBatchInterface;
    
    @Getter @Setter private SmartloadOrderServiceInterface smartloadBatchOrderInterface;
    
    @Getter @Setter private KeyInterface keyInterface;
    
    protected static String ENDPOINT_ADDRESS_BASE = "http://www.smartcallesb.co.za:8090/SmartcallServices2/"; 
    
//	protected static String ENDPOINT_ADDRESS = ENDPOINT_ADDRESS_BASE+"SmartloadService";
//	protected static String WSDL = ENDPOINT_ADDRESS+"?wsdl";
//	
//	protected static String ENDPOINT_ADDRESS_ORDER = ENDPOINT_ADDRESS_BASE+"SmartBatchService";
//	protected static String WSDL_ORDER = ENDPOINT_ADDRESS_ORDER+"?wsdl";
//	
//	protected static String ENDPOINT_ADDRESS_ORDER_RESP = ENDPOINT_ADDRESS_BASE+"SmartloadOrderService";
//	protected static String WSDL_ORDER_RESP = ENDPOINT_ADDRESS_ORDER_RESP+"?wsdl";

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static String APP_HOME = System.getProperty("user.dir");
    
   
  
    
    public String basicPath = "";
    
    
    public Dealer currentDealer;
    
   

	
	private PollForFiles pollForFiles;
	
	private static FileAppender fa;
	
	
	
    
       
    Service<Void> service = new Service<Void>() {
	    @Override
	    protected Task<Void> createTask() {
	        return new Task<Void>() {
	            @Override
	            protected Void call()
	                    throws InterruptedException {
	            	log.info("Starting task");
	            	Thread.sleep(10000);
	            	log.info("Starting task ..");
	                updateMessage("Starting setup . . .");
	                updateProgress(0, 10);
	                try {
						initialise();
						updateMessage("Initialising WebService . . .");
						log.info("Initialised webservice");
						updateProgress(1, 10);
					   	updateProgress(2, 10);
						updateMessage("Retrieving products. . .");
					    retrieveProducts();
					    log.info("Retrieved products. . .");
					 	updateProgress(8, 10);

	                } catch (Exception e) {
						 updateMessage("Error " + e.getMessage());
						 return null;
					}
	                updateMessage("Completed.");
	                return null;
	            }
	        };
	    }
	};
	
	public static void main(String[] args) {
		launch(args);
	}
	
	   /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
  
	@Override
	public void start(Stage primaryStage) {
		  try {
			    this.primaryStage = primaryStage;
			    this.primaryStage.setTitle("SmartloadApp");
			    initRootLayout();
			     log.info("Dealer starting " + RefPreferences.getDealerMsisdn());
			     if (RefPreferences.getDealerMsisdn()==null) {
			    	showSetupInitial();
			    	
			    }
			 //   DialogFactory.createDialogProgress(primaryStage,"Progress Dialog","Loading configuration",service);
		        service.start();
        		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			            @Override
			            public void handle(WorkerStateEvent t) {
				        	  try {
				        		 retrieveProducts();
				        		 rootController.refesh();
						      } catch (Exception e) {
								log.error("refresh",e);
						      }	   
			            }
        		});
        		currentDealer = DataAccess.getDealer(RefPreferences.getDealerMsisdn());
        		if (currentDealer==null){
        			currentDealer = new Dealer(RefPreferences.getDealerMsisdn(),"");
        			DataAccess.saveDealerToDB(currentDealer);
        		}
		} catch (Exception e) {
			DialogFactory.createDialogException("Error", "Setup Error", e);   
		}
	}
	


	/**
	 * Constructor
	 * @throws IOException 
	 * @throws ConfigurationException 
	 */
	public MainApp() throws IOException, ConfigurationException {
		
		 basicPath = APP_HOME;
		
		 RefPreferences.DEFAULT_DOWNLOAD_LOCATION = basicPath+FILE_SEPARATOR + "files";
		 RefPreferences.DEFAULT_EXTRACT_LOCATION = basicPath+FILE_SEPARATOR + "extract";
		 RefPreferences.DEFAULT_LOGGING_LOCATION = basicPath+FILE_SEPARATOR + "logging";
		 RefPreferences.DEFAULT_ARCHIVE_LOCATION = basicPath+FILE_SEPARATOR + "archive";
		
	 	setupLogging(RefPreferences.getLoggingLocation(),RefPreferences.getLogLevel());
		
		  
		 
	}

    public static void setupLogging(String extractLocation,String logLevel){
    	java.io.File f = new java.io.File(extractLocation);
    	f.mkdirs();
    	if (fa!=null) fa.close();
    	  fa = new FileAppender();
    	  fa.setName("FileLogger");
		  fa.setFile(extractLocation+FILE_SEPARATOR+"info.log");
		  fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		  fa.setThreshold(Level.toLevel(logLevel));
		  fa.setAppend(true);
		  fa.activateOptions();
		  Logger.getRootLogger().addAppender(fa);
		  log.info("Setup the logging");
		
    }
	
	public void changeLogging(String logLevel){
		 fa.setThreshold(Level.toLevel(logLevel));
	}
	
	
	private void initialise() throws Exception{
		try {
			initialiseWebService();
			pollForFiles = new PollForFiles();
			pollForFiles.schedule(this);
		} catch (Exception e) {
			log.error("Error initialsing webservice" ,e);
			throw e;
		} 
	}
	
	public void initialiseWebService() throws MalformedURLException{
		Bootstrapper strapper = new Bootstrapper();
		keyInterface = new KeyStoreImplementation();
		keyInterface.setHomeLocation(APP_HOME);
		
		String currentBase = RefPreferences.getWebServiceUrl(); 
		    
		String ENDPOINT_ADDRESS = currentBase+"SmartloadService";
		String WSDL = ENDPOINT_ADDRESS+"?wsdl";
			
		String ENDPOINT_ADDRESS_ORDER = ENDPOINT_ADDRESS_BASE+"SmartBatchService";
		String WSDL_ORDER = ENDPOINT_ADDRESS_ORDER+"?wsdl";
			
		String ENDPOINT_ADDRESS_ORDER_RESP = ENDPOINT_ADDRESS_BASE+"SmartloadOrderService";
		String WSDL_ORDER_RESP = ENDPOINT_ADDRESS_ORDER_RESP+"?wsdl";
		
		
		smartloadBatchOrderInterface = strapper.setupSmartloadOrderBatch(WSDL_ORDER_RESP, RefPreferences.getDealerMsisdn(), RefPreferences.getDownloadLocation());
		smartloadInterface =  strapper.setupSmartload(WSDL,RefPreferences.getDealerMsisdn());
		smartloadBatchInterface = strapper.setupSmartloadBatch(WSDL_ORDER, RefPreferences.getDealerMsisdn());
	
	}


	/**
     * Initializes the root layout.
	 * @throws IOException 
     */
    public void initRootLayout() throws IOException {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            rootController = loader.getController();
            rootController.setMainApp(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
        	log.error("Init root layout",e);
            throw e;
        }
    }
    

    
   public boolean showSetupCertificate() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("SetupCertificateDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Setup Certificate");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SetupCertificateController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }
   
   public boolean showSetupInitial() {
       try {
           // Load the fxml file and create a new stage for the popup dialog.
           FXMLLoader loader = new FXMLLoader();
           loader.setLocation(MainApp.class.getClassLoader().getResource("InitialSetupDialog.fxml"));
           AnchorPane page = (AnchorPane) loader.load();
           Stage dialogStage = new Stage();
           dialogStage.setTitle("Initial Setup");
           dialogStage.initModality(Modality.WINDOW_MODAL);
           dialogStage.initOwner(primaryStage);
           Scene scene = new Scene(page);
           dialogStage.setScene(scene);

           InitialSetupDialogController controller = loader.getController();
           controller.setMainApp(this);
           controller.setDialogStage(dialogStage);

           // Show the dialog and wait until the user closes it
           dialogStage.showAndWait();

           return controller.isOkClicked();
       } catch (IOException e) {
           log.error("Error setup up initial setup dialog",e);
           return false;
       }
   }
    
    public boolean showModifyUrl() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("ModifySmartcallUrl.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Webservice");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ModifyUrlController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
        	log.error(e);
            return false;
        }
    }
    
   
    
    public boolean showSettings() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("Settings.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Logging Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SettingsController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
        	log.error(e);
            return false;
        }
    }
    
    
    public boolean showHandleExistingKeys() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("UseExistingKeysDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Handle existing keys");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            UseExistingKeystoreController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
        	log.error(e);
            return false;
        }
    }
    
    public void showIndividualTransaction(Transaction transaction) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("TransactionDetailDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Transaction details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TransactionDetailController controller = loader.getController();
            controller.setMainApp(this);
            controller.setTransaction(transaction);
            controller.setDialogStage(dialogStage);
          

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            
        } catch (IOException e) {
        	log.error(e);
            e.printStackTrace();
           
        }
    }
    
    public boolean showIndividualFile(File fileInformation) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("FileDetailsDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("File details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            FileDetailController controller = loader.getController();
            controller.setMainApp(this);
            controller.setFileInformation(fileInformation);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
        	log.error(e);
            return false;
        }
    }
    


    
    public String getDealerBalance(){
    	NumberFormat currency = NumberFormat.getCurrencyInstance();
		DealerBalanceResponse response = smartloadInterface.getDealerBalance();
		return currency.format(response.getBalance());
    }
    

    
   /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public void setDealerMsisdn(String dealerMsisdn) {
    	RefPreferences.setDealerMsisdn(dealerMsisdn);
        if (dealerMsisdn != null) {
           primaryStage.setTitle("SmartcallApp - " + dealerMsisdn);
        } else {
            primaryStage.setTitle("SmartcallApp");
        }
        
    }
    
      

    
     
		   
		   public void retrieveProducts(){
		    	    Networks networks = new Networks();
		    	    List<za.co.smartcall._2010._12.common.Network> genNetworks = smartloadInterface.getNetworks();
		    	    Hashtable<za.co.smartcall._2010._12.common.Network,List<za.co.smartcall._2010._12.message.ProductType>> genProductTypes = new  Hashtable<za.co.smartcall._2010._12.common.Network,List<za.co.smartcall._2010._12.message.ProductType>>();
		    	   	networks.addList(genNetworks);
					DataAccess.saveNetworksToDB(networks);
					genNetworks.forEach(network->genProductTypes.put(network,smartloadInterface.getProductTypesForNetwork(network)));
					ProductTypes productTypes = new ProductTypes();
					productTypes.addTable(genProductTypes);
					DataAccess.saveProductTypesToDB(productTypes);
					Products products = new Products();
					for (za.co.smartcall._2010._12.common.Network network : genNetworks) { // must be a way to remove this
						for (ProductType productType :genProductTypes.get(network)) {
							List<za.co.smartcall._2010._12.message.Product> productsList = smartloadInterface.getProductForNetworkProductTypes(network, productType);
							products.addList(productsList,productType,network);
						}
					}
					DataAccess.saveProductsToDB(products);
		    }
		   
			


		   
		   public void refresh(){
			   rootController.reset();
		   }
		   
		   
		   
		   @Override
		   public void stop() throws Exception {
			   log.info("App shutdown");
			   pollForFiles.stop();
			   service.cancel();
			   super.stop();
			   fa.close();
		   }
		   
   
}
