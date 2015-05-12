package za.co.smartcall.smartload.view;

import java.net.URL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.model.DataAccess;
/**
 * general overview page contains balance user and all transactions under this user
 * @author rudig
 *
 */
@Log4j
public class DealerView {

    // Reference to the main application.
    private MainApp mainApp;
    
    @FXML
    private Label dealerMsisdn;
    
    @FXML
    private Label dealerBalance;
    
    @FXML
    private TableView<Transaction> transactionTable;
    
    @FXML
    private TableColumn<Transaction,String> transactionDateColumn;
    @FXML
    private TableColumn<Transaction,String> transactionNetworkColumn;
    @FXML
    private TableColumn<Transaction,String> transactionTypeColumn;
    @FXML
    private TableColumn<Transaction,String> transactionAmountColumn;
    @FXML
    private TableColumn<Transaction,String> transactionStatusColumn;
    @FXML
    private TableColumn<Transaction,String> transactionRecipientColumn;
    @FXML
    private TableColumn<Transaction,String> transactionPinColumn;
    

    private Pagination pagination;
    
    private final static int rowsPerPage = 15;
    
    @FXML
    private AnchorPane anchorPaneTable;
    
    public static boolean individualShowing = false;
    
    private ObservableList<Transaction> transactionData = FXCollections
			.observableArrayList();
    
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public DealerView() {
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
    	int toIndex = Math.min(fromIndex + rowsPerPage, transactionData.size());
    	 transactionTable.setItems(FXCollections.observableArrayList(transactionData.subList(fromIndex, toIndex)));
		 try {
			  transactionNetworkColumn.setCellValueFactory(cellData -> cellData.getValue().network());
			  transactionNetworkColumn.setCellFactory(new Callback<TableColumn<Transaction,String>, TableCell<Transaction,String>>() {
				
				@Override
				public TableCell<Transaction, String> call(
						TableColumn<Transaction, String> param) {
					TableCell<Transaction, String> cell = new TableCell<Transaction,String>(){
						@Override
						public void updateItem(String item,boolean empty){
							if ((item != null)){
								try {
									URL uri = this.getClass().getClassLoader().getResource("images/"+item.toLowerCase()+".png");
									Image image  = new Image(uri.toString());
									ImageView imgVw = new ImageView(image);
									imgVw.setFitHeight(transactionNetworkColumn.getWidth()/2.5);
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
			  transactionPinColumn.setCellValueFactory(cellData -> cellData.getValue().voucherPin());
			  transactionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().description());
			  transactionAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amount());    
			  transactionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().submissionStatus());
			  transactionRecipientColumn.setCellValueFactory(cellData -> cellData.getValue().deviceId());
		      transactionDateColumn.setCellValueFactory(cellData -> cellData.getValue().statusDate());

		      transactionTable.setRowFactory(new Callback<TableView<Transaction>, TableRow<Transaction>>() {
		          @Override
		          public TableRow<Transaction> call(TableView<Transaction> tableView) {
		              final TableRow<Transaction> row = new TableRow<Transaction>() {
		                  @Override
		                  protected void updateItem(Transaction transaction, boolean empty){
		                	  if (transaction!=null){
			                      super.updateItem(transaction, empty);
			                      if (transaction.submissionStatus().getValue().equals("SUCCESS")) {
			                             getStyleClass().add("sucess-row-color");
			                      } else {
			                          getStyleClass().add("unsuccess-row-color");
			                      }
		                      }
		                  }
		              };
		              row.setOnMouseClicked(event -> {
			    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
			    	            Transaction rowData = row.getItem();
			    	            mainApp.showIndividualTransaction(rowData);
			    	            individualShowing = true;
			    	        }
			    	    });
		              return row;
		          }
		      });
		} catch (Exception e) {
			log.error("Error",e);
		}
    	 
    	return  new BorderPane(transactionTable);
    	
    	} 
    

    public void refresh(){
    		
			dealerMsisdn.setText(RefPreferences.getDealerMsisdn());
			dealerBalance.setText(mainApp.getDealerBalance());
		    transactionData = DataAccess.loadTransactionsFromDB().returnAsObservableList();
 			pagination = new Pagination((transactionData.size() / rowsPerPage + 1), 0);
			pagination.setPageFactory(this::createPage); 
			pagination.setMaxSize(800, 800);
		    anchorPaneTable.getChildren().add(pagination); 
		    log.info("Refreshed dealer view");
    	
    }
    
    
    
 
    
}	
	

