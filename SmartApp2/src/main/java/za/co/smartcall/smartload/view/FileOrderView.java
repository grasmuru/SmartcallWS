package za.co.smartcall.smartload.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j;



import za.co.smartcall._2010._12.common.ResponseCode;
import za.co.smartcall._2010._12.message.BatchVoucherRequest;
import za.co.smartcall._2010._12.message.BatchVoucherResponse;
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefGenerator;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.FileOrder;
import za.co.smartcall.smartload.hibernate.Product;
import za.co.smartcall.smartload.hibernate.SubmissionStatus;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.hibernate.Transactions;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class FileOrderView {

    // Reference to the main application.
    private MainApp mainApp;
    
    @FXML
    private TableView<FileOrder> orderTable;
    
    @FXML
    private TableColumn<FileOrder,String> orderNetworkColumn;
    @FXML
    private TableColumn<FileOrder,String> orderProductTypeColumn;
    @FXML
    private TableColumn<FileOrder,String> orderProductColumn;
    @FXML
    private TableColumn<FileOrder,Boolean> orderSelectedColumn;
    @FXML
    private TableColumn<FileOrder,String> orderAmountColumn;
    @FXML
    private TableColumn<FileOrder,String> orderHistoryColumn;
       
      
    @FXML
    private Label dealerMsisdn;
    
    @FXML
    private Label dealerBalance;
    
          
    private boolean okClicked = false;
    
 
    private List<FileOrder> possibleFileOrders = new ArrayList<FileOrder>();
    
    
    private Pagination pagination;
    
    private final static int rowsPerPage = 100;
    
    @FXML
    private AnchorPane anchorPaneTable;
    
    private List<FileOrder> selectedOrders = new ArrayList<FileOrder>();
    
    

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public FileOrderView() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    private Node createPage(int pageIndex) {
   	 
    	int fromIndex = pageIndex * rowsPerPage;
    	int toIndex = Math.min(fromIndex + rowsPerPage, possibleFileOrders.size());
    	 orderTable.setItems(FXCollections.observableArrayList(possibleFileOrders.subList(fromIndex, toIndex)));
		try{
			  orderNetworkColumn.setCellValueFactory(cellData -> cellData.getValue().network());
			  orderNetworkColumn.setCellFactory(new Callback<TableColumn<FileOrder,String>, TableCell<FileOrder,String>>() {
				
				@Override
				public TableCell<FileOrder, String> call(
						TableColumn<FileOrder, String> param) {
					TableCell<FileOrder, String> cell = new TableCell<FileOrder,String>(){
						@Override
						public void updateItem(String item,boolean empty){
							if ((item != null)){
								try {
									URL uri = this.getClass().getClassLoader().getResource("images/"+item.toLowerCase()+".png");
									Image image  = new Image(uri.toString());
									ImageView imgVw = new ImageView(image);
									imgVw.setFitHeight(orderNetworkColumn.getWidth()/2.5);
									setGraphic(imgVw);
								} catch (Exception e) {
									log.error("Unknown icon " + item,e);
								}
							}
						}
					};
					cell.setAlignment(Pos.CENTER);
					return cell;
				}
			});
			orderProductTypeColumn.setCellValueFactory(cellData -> cellData.getValue().productType());
			orderProductColumn.setCellValueFactory(cellData -> cellData.getValue().product());
			Callback<TableColumn<FileOrder, Boolean>, TableCell<FileOrder, Boolean>> booleanCellFactory = 
		            new Callback<TableColumn<FileOrder, Boolean>, TableCell<FileOrder, Boolean>>() {
		            @Override
		                public TableCell<FileOrder, Boolean> call(TableColumn<FileOrder, Boolean> p) {
		                    return new BooleanCell();
		            }
		        };
		    orderSelectedColumn.setCellValueFactory(cellData -> cellData.getValue().selected());
		    orderSelectedColumn.setCellFactory(booleanCellFactory);
		    orderSelectedColumn.setMaxWidth(50);
		    orderSelectedColumn.setMinWidth(50);


			Callback<TableColumn<FileOrder, String>, TableCell<FileOrder, String>> stringCellFactory = 
		            new Callback<TableColumn<FileOrder, String>, TableCell<FileOrder, String>>() {
		            @Override
		                public TableCell<FileOrder, String> call(TableColumn<FileOrder, String> p) {
		                    return new EditTextCell();
		            }
		        };
			 orderAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amount());    
			 orderAmountColumn.setCellFactory(stringCellFactory);
			 orderAmountColumn.setMaxWidth(100);
			 orderAmountColumn.setMinWidth(100);
			
			 orderHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().history());
			
		}catch (Exception e) {
			log.error("Error",e);
		}
		 orderTable.getItems().forEach(fileOrder ->fileOrder.setSelected(false));
		
    	 
    	return new BorderPane(orderTable);
    	
    	} 
    
    
   
    @FXML
    public boolean isButtonSelect() {
    	returnRequestDialog();
        return okClicked;
    }
    
    public void returnRequestDialog(){
    	StringJoiner item = new StringJoiner("\n\n"); 
    	selectedOrders.forEach(fileorder ->item.add(parse(fileorder)));
    	 
    	String combined = "You have selected to order:\n" +item.toString()+"\n\n";
    	String answer = FxDialogs.showTextInput(combined + "Do you want to continue?", "Yes", "No");
    	if (answer.equals("Yes")) {
    		long batchId = System.currentTimeMillis();
    		selectedOrders.forEach(fileOrder->performBatchRequest(fileOrder,batchId));
    		DialogFactory.createDialogInformation("Information Dialog", null, "Your requests have been submitted please check the overview for request results");
    		clearButtonSelect();
    	} else if (answer.equals("No")){
    	   //
    	} else {
    		clearButtonSelect();
    	}
   }
    
   
    private void performBatchRequest(FileOrder fileOrder,long batchId) {
    	    log.info("Performing batch Id " + batchId + ":" + fileOrder);
           	BatchVoucherRequest batchRequest = new BatchVoucherRequest();
        	batchRequest.setQuantity(fileOrder.getAmount());
        	String clientReferenceNumber = RefGenerator.getInstance().getCount();
        	batchRequest.setProductId(fileOrder.getProduct().getId());
         	BatchVoucherResponse response = mainApp.getSmartloadBatchInterface().orderVoucherBatch(batchRequest,clientReferenceNumber);
			saveTransaction(batchRequest, response,fileOrder.getProduct().getDescription(),clientReferenceNumber,batchId);
			mainApp.refresh();
		
    }
    

    private void saveTransaction(BatchVoucherRequest batchRequest,BatchVoucherResponse response,String description,String clientRefNumber,long batchId){
    	Transaction transaction = new Transaction();
   	   	transaction.setAmount(Integer.toString(batchRequest.getQuantity()));
    	transaction.setDescription("File:"+description);
    	transaction.setTransactionStatus("Submitted");
    	transaction.setStatusDate(new Date());
    	transaction.setDeviceId("");
    	transaction.setLastFileName("");
    	transaction.setClientReference(clientRefNumber);
       	transaction.setPassword(response.getFilePassword());
       	transaction.setFile(true);
       	transaction.setProduct(DataAccess.findProduct(batchRequest.getProductId()));
       	if (response.getRecharge()!=null)
       		transaction.setOrderRef(response.getRecharge().getOrderReferenceId());
       	else 
       		transaction.setOrderRefNumber("");
       	transaction.setRetrieved(false);
       	if (response.getError()!=null)
        	transaction.setResponseMessage(response.getError().getMessage());
       	ResponseCode responseCode = response.getResponseCode();
       
       	if (responseCode!=null){
       		SubmissionStatus status = DataAccess.loadSubmissionStatus(responseCode.name());
       		transaction.setSubmissionStatus(status);
       		if (!status.getSubStatus().equals("SUCCESS"))
       			transaction.setTransactionStatus(response.getError().getMessage());
       	}
       	else { 
       		SubmissionStatus status = DataAccess.loadSubmissionStatus("FAILURE");
       		transaction.setSubmissionStatus(status);
       		transaction.setTransactionStatus("Error");
       	}
       	transaction.setBatchRequest(batchId);
       	transaction.setDealer(mainApp.currentDealer);
     	DataAccess.saveOrUpdateTransactionToDB(transaction);
    }   
    
    
    private String parse(FileOrder fileOrder){
    	return fileOrder.getAmount() + " X " + fileOrder.getProduct().getDescription() + " (" + fileOrder.getNetwork().getNetwork()+")"; 
    }
    

    
    @FXML
    public boolean clearButtonSelect() {
    	selectedOrders.clear();
    	possibleFileOrders.clear();
    	anchorPaneTable.getChildren().remove(pagination);
    	setup();
    	return true;
    }
    
    public void setup(){
    	
		dealerMsisdn.setText(RefPreferences.getDealerMsisdn());
		dealerBalance.setText(mainApp.getDealerBalance());

        List<Product> products = DataAccess.returnAllproductsThatReturnFiles();
        possibleFileOrders.clear();
        products.forEach(product->addOption(product));
        populateFileOrder();
        possibleFileOrders.sort((p1, p2) -> Integer.valueOf(p1.getNetwork().getId()).compareTo(Integer.valueOf(p2.getNetwork().getId())));
        
       
    	pagination = new Pagination((possibleFileOrders.size() / rowsPerPage + 1), 0);
		pagination.setPageFactory(this::createPage); 
		pagination.setMaxSize(800, 800);
	    anchorPaneTable.getChildren().add(pagination); 
    }
 
    
    public void addOption(Product product){
    	FileOrder order = new FileOrder();
    	order.setProduct(product);
    	order.setProductType(product.getProducttype());
    	order.setNetwork(product.getProducttype().getNetwork());
    	possibleFileOrders.add(order);
    }
    
    class BooleanCell extends TableCell<FileOrder, Boolean> {
        private CheckBox checkBox;
        private Integer index;
        public BooleanCell() {
            checkBox = new CheckBox();
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean> () {
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                	if(isEditing())
                        commitEdit(newValue == null ? false : newValue);
                	try {
						getTableView().getSelectionModel().select(index);
					} catch (Exception e) {
						return;
					}
                    FileOrder fileOrder = getTableView().getSelectionModel().getSelectedItem();
                    fileOrder.setSelected(true);
                    if (fileOrder.getAmount() > 0) {
                    	selectedOrders.add(fileOrder);
                    }
                }
            });
             
            this.setGraphic(checkBox);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(true);
            setAlignment(Pos.CENTER);
           
        }
        @Override
        public void startEdit() {
            super.startEdit();
            if (isEmpty()) {
                return;
            }
            checkBox.setDisable(false);
            checkBox.requestFocus();
        }
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            checkBox.setDisable(true);
        }
        public void commitEdit(Boolean value) {
            super.commitEdit(value);
            checkBox.setDisable(true);
        }
        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if ((item != null)){
	            if (!isEmpty()) {
	                checkBox.setSelected(item);
	            }
            } else {
            	this.setGraphic(null);
            }
            this.index= getIndex();
        }
    }
    
    
    class EditTextCell extends TableCell<FileOrder, String> {
        private TextField textField;
        private Integer index;
        public EditTextCell() {
        	textField = new TextField();
        	textField.textProperty().addListener(new ChangeListener<String> () {
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(isEditing())
                        commitEdit(newValue == null ? "" : newValue);
                    if (index!=null){
                    	getTableView().getSelectionModel().select(index);
                        FileOrder fileOrder = getTableView().getSelectionModel().getSelectedItem();
                        try {
							fileOrder.setAmount(Integer.parseInt(newValue));
						} catch (Exception e) {
							fileOrder.setAmount(0);
						}
                        if (fileOrder.isSelected()) {
                        	if (oldValue.equals("0"))
                        		selectedOrders.add(fileOrder);
                        	else {
                        		selectedOrders.remove(fileOrder);
                        		selectedOrders.add(fileOrder);
                        	}
                        }
                    }
                }
            });
            this.setGraphic(textField);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(false);
            textField.setEditable(true);
            setAlignment(Pos.CENTER);
           
        }
        @Override
        public void startEdit() {
            super.startEdit();
            if (isEmpty()) {
                return;
            }
            textField.setDisable(false);
            textField.requestFocus();
        }
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            textField.setDisable(true);
        }
        public void commitEdit(String value) {
            super.commitEdit(value);
            textField.setDisable(true);
        }
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if ((item != null)){
	            if (!isEmpty()) {
	                textField.setText(item);
	            }
            } else {
            	this.setGraphic(null);
            }
            this.index= getIndex();
        }
     }
      /**
       * Update screen after everything is refreshed, used to capture the latest submissions on the 
       * page	
       */
      private void populateFileOrder(){
    	 List<Transaction> transactions = itemsFromLatestBatch();
    	 log.info("Found item matching latest batch " + transactions.size());
    	 transactions.forEach(transaction->updateOrder(transaction));
      }
      
      private List<Transaction> itemsFromLatestBatch(){
    	  long batch = getLatestBatchNumber();
    	  Transactions transactions = DataAccess.loadTransactionsFromDB();
    	  if (batch ==0) return new ArrayList<Transaction>();
    	  return transactions.getTransactions().stream().filter(transaction->transaction.getBatchRequest()==batch).collect(Collectors.toList());

    	  
      }
      
      private void updateOrder(Transaction transaction){
    	 long transProductId = transaction.getProduct().getId();
    	 FileOrder order = possibleFileOrders.stream().filter(fileOrder->fileOrder.getProduct().getId()==transProductId).findFirst().get();
         order.setHistory(transaction.getStatusDate() + " " + transaction.getTransactionStatus());
      }
      
      
      private long getLatestBatchNumber(){
    	  try {
    		long batchNo = DataAccess.loadTransactionsFromDB().getTransactions().stream().mapToLong(trans->trans.getBatchRequest()).max().getAsLong();
			log.info("Getting latest from batch " + batchNo);
			return batchNo;
		} catch (Exception e) {
			log.error("No latest batch number");
			return 0;
		}
      }
   
}	
	

