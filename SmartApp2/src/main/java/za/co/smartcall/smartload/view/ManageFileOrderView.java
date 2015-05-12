package za.co.smartcall.smartload.view;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
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
import za.co.smartcall.smartload.DialogFactory;
import za.co.smartcall.smartload.MainApp;
import za.co.smartcall.smartload.RefPreferences;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Files;
import za.co.smartcall.smartload.hibernate.Transaction;
import za.co.smartcall.smartload.hibernate.Transactions;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class ManageFileOrderView {


    private MainApp mainApp;
    
    
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ManageFileOrderView() {
    }
    
    @FXML
    private Label numOutstanding;
    
    @FXML
    private Label unimported;
    
    @FXML
    private Label unextracted;
    
    @FXML
    private Label lastFile;
    
    @FXML
    private Label lastArchive;
    
    @FXML
    private Label downloadLocation;
    
    @FXML
    private Label extractLocation;
    
    
    @FXML
    private TableView<File> fileTable;
    
    @FXML
    private TableColumn<File,String> fileNetworkColumn;
    @FXML
    private TableColumn<File,String> fileDateColumn;
    @FXML
    private TableColumn<File,String> fileTypeColumn;
    @FXML
    private TableColumn<File,String> fileAmountColumn;
    @FXML
    private TableColumn<File,String> fileNameColumn;
    @FXML
    private TableColumn<File,String> fileExtractedColumn;
    @FXML
    private TableColumn<File,String> fileImportedColumn;

    @FXML
    private ChoiceBox<String> importSelection;
    
    private String selectedImport;
    
     private Pagination pagination;
    
    private final static int rowsPerPage = 15;
    
    @FXML
    private AnchorPane anchorPaneTable;
    
	private ObservableList<File> fileData = FXCollections
			.observableArrayList();

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
			selectedImport = RefPreferences.getImportSelection();
			importSelection.getItems().clear();
			importSelection.getItems().add("XML File");
	    	importSelection.getItems().add("Database");
//	    	importSelection.getItems().add("SVD format");
			
    }
    
    @FXML
    public void populateLabels(){
    	
   	     lastFile.setText("-");
         lastArchive.setText("-");

      
         numOutstanding.setText(Integer.toString(DataAccess.unretrievedButSuccessfullFileRequest()));
         
         unextracted.setText(Long.toString(DataAccess.numOfUnextractedFiles()));
       
         unimported.setText(Long.toString(DataAccess.numOfUnimportedFiles()));
                  
         lastFile.setText(DataAccess.getLastReceivedFile());
         
         lastArchive.setText(DataAccess.getlastArchivedDate());
   
       
    }
    
    
    private Node createPage(int pageIndex) {
   	 
    	int fromIndex = pageIndex * rowsPerPage;
    	int toIndex = Math.min(fromIndex + rowsPerPage, fileData.size());
    	 fileTable.setItems(FXCollections.observableArrayList(fileData.subList(fromIndex, toIndex)));
		 try {
			 fileNetworkColumn.setCellValueFactory(cellData -> cellData.getValue().network());
				fileNetworkColumn.setCellFactory(new Callback<TableColumn<File,String>, TableCell<File,String>>() {
					
					@Override
					public TableCell<File, String> call(
							TableColumn<File, String> param) {
						TableCell<File, String> cell = new TableCell<File,String>(){
							@Override
							public void updateItem(String item,boolean empty){
								if ((item != null)){
									URL uri = this.getClass().getClassLoader().getResource("images/"+item.toLowerCase()+".png");
									Image image  = new Image(uri.toString());
									ImageView imgVw = new ImageView(image);
									imgVw.setFitHeight(fileNetworkColumn.getWidth()/2.5);
									setGraphic(imgVw);
								}
									
								
							}
						};
						cell.setAlignment(Pos.CENTER);
						return cell;
					}
				});
				  fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileName());
				 fileTypeColumn.setCellValueFactory(cellData -> cellData.getValue().content());
				  fileAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amount());    
				  fileExtractedColumn.setCellValueFactory(cellData -> cellData.getValue().extracted().asString());
				  fileImportedColumn.setCellValueFactory(cellData -> cellData.getValue().imported().asString());
			      fileDateColumn.setCellValueFactory(cellData -> cellData.getValue().receivedDateProperty());

			     fileTable.setRowFactory(new Callback<TableView<File>, TableRow<File>>() {
			          @Override
			          public TableRow<File> call(TableView<File> tableView) {
			              final TableRow<File> row = new TableRow<File>() {
			                  @Override
			                  protected void updateItem(File fileInfo, boolean empty){
			                	  if (fileInfo!=null){
				                      super.updateItem(fileInfo, empty);
				                      if (fileInfo.isImported()) {
				                             getStyleClass().add("sucess-row-color");
				                      } else {
				                          getStyleClass().add("unsuccess-row-color");
				                      }
			                      }
			                  }
			              };
			              row.setOnMouseClicked(event -> {
				    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
				    	            File File = row.getItem();
				    	            mainApp.showIndividualFile(File);
				    	        }
				    	    });
			              return row;
			          }
			      });
		    	
		        
		        populateLabels();
		 } catch (Exception e) {
				log.error("Error creating page ",e);
			}
    	 
    	return  new BorderPane(fileTable);
    	
    	 
    }
    
    
    
    @FXML
    private void handleArchive() {
    	  try {
			  List<File> unarchived = DataAccess.fileAllFilesReadyForArchiving();
			  unarchived.forEach(fileInfo->archiveFile(fileInfo));
			  String message = unarchived.size() + " files archived";
			  DialogFactory.createDialogInformation("File archiving", "Archiving successful", message);
			  setup();
		} catch (Exception e) {
			DialogFactory.createDialogException("Archive", "Error archiving extracted and imported files", e);
		}
    }
    
    public void archiveFile(File fileInfo){
    	
    	String fileName = fileInfo.getFileName();
    	java.io.File file = new java.io.File(RefPreferences.getDownloadLocation());
    	java.io.File f = new java.io.File(RefPreferences.getArchiveLocation());
    	f.mkdirs();
    	if(file.renameTo(new java.io.File(RefPreferences.getArchiveLocation()+FILE_SEPARATOR+fileName))){
    		log.info("File is moved successful!");
    	}else{
    		log.info("File is failed to move!");
    	}
    	fileInfo.setArchived(true);
    	fileInfo.setLastarchived(new Date());
    	java.io.File fileExtract = new java.io.File(fileInfo.getExtractLocation()+FILE_SEPARATOR+fileName.replaceAll(".zip", ".csv"));
    	fileExtract.delete();
    	DataAccess.saveFileToDB(fileInfo);
    }
    
    public void setup(){
    	downloadLocation.setText(RefPreferences.getDownloadLocation());
		extractLocation.setText(RefPreferences.getExtractLocation());
		importSelection.setValue(selectedImport);
		fileData = DataAccess.loadFilesFromDB().getFileData(false);
		pagination = new Pagination((fileData.size() / rowsPerPage + 1), 0);
		pagination.setPageFactory(this::createPage); 
		pagination.setMaxSize(800, 800);
	    anchorPaneTable.getChildren().add(pagination); 
    	
    }
    
   
}	
	

