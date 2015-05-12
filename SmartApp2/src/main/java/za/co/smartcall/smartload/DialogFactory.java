package za.co.smartcall.smartload;

import javafx.concurrent.Service;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
/**
 * All popup dialogs placed here so there is some overview and consistency
 * @author rudig
 *
 */
@Log4j
public class DialogFactory {

	public static void createDialogException(String title,String masthead,Exception e){
		log.error(title +" Error",e);
		Dialogs.create().title(title).masthead(masthead).showException(e);
	}
	
	public static void createDialogProgress(Stage owner,String title,String masthead,Service<Void> service){
		Dialogs.create().owner(owner).title(title).masthead(masthead).showWorkerProgress(service);
	}
	
	public static void createDialogInformation(String title,String masthead,String message){
		Dialogs.create().title(title).masthead(masthead).message(message).showInformation();
	}
	
	public static void createDialogWarning(String title,String masthead,String message){
		Dialogs.create().title(title).masthead(masthead).message(message).showWarning();
	}
	
	public static void createDialogError(String title,String masthead,String message){
		Dialogs.create().title(title).masthead(masthead).message(message).showError();
	}
	
	public static Action createDialogConfirm(String title,String masthead,String message){
		return Dialogs.create().title(title).masthead(masthead).message(message).showConfirm();
	}
}
