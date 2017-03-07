package za.co.smartcall.smartload;

import javafx.concurrent.Service;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.view.FxDialogs;


/**
 * All popup dialogs placed here so there is some overview and consistency
 * @author rudig
 *
 */
@Log4j
public class DialogFactory {

	public static void createDialogException(String title,String masthead,Exception e){
		log.error(title +" Error",e);
		FxDialogs.showException(title, masthead, e);
	}
	
//	public static void createDialogProgress(Stage owner,String title,String masthead,Service<Void> service){
//		Dialogs.create().owner(owner).title(title).masthead(masthead).showWorkerProgress(service);
//	}
	
	public static void createDialogInformation(String title,String masthead,String message){
		FxDialogs.showInformation(title, message);
	}
	
	public static void createDialogWarning(String title,String masthead,String message){
		FxDialogs.showWarning(title, message);
	}
	
	public static void createDialogError(String title,String masthead,String message){
		 FxDialogs.showError(title, message);
	}
	

}
