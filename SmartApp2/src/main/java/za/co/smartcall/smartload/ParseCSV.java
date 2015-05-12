package za.co.smartcall.smartload;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Voucher;
import za.co.smartcall.smartload.model.DataAccess;

@Log4j
public class ParseCSV {
	
	
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    public static final String FILE_FORMAT_EXTENSION = ".zip";
    
    public static final String FILE_FORMAT_CONTENT_EXTENSION = ".csv";
	
	public void parse(String csvFileNameAndpath,int fileId) throws FileNotFoundException, IOException {
		 
	
		csvFileNameAndpath = csvFileNameAndpath.replaceAll(FILE_FORMAT_EXTENSION, FILE_FORMAT_CONTENT_EXTENSION);
		
		String line = "";
		String cvsSplitBy = ",";
	   
	    File file = DataAccess.loadFileFromDB(fileId);
	    log.info("Linked to following file " + file + " " + fileId);
		try (final BufferedReader br = new BufferedReader(new FileReader(csvFileNameAndpath))){
			List<Voucher> vouchers = new ArrayList<Voucher>();
			while ((line = br.readLine()) != null) {
				String[] voucher = line.split(cvsSplitBy);
				Voucher voucherObject = new Voucher(voucher,file);
				vouchers.add(voucherObject);
				log.debug("Adding voucher");
			}
	        saveFileInfoToDB(vouchers);
		} 
	  }
	 
	
	  
	   /**
	    * Saves the current person data to the specified file.
	    * 
	    * @param file
	    */
	   public void saveFileInfoToDB(List<Voucher> vouchers) {
	       try {
	          DataAccess.saveVouchersToDB(vouchers);
	       } catch (Exception e) {
	    	   DialogFactory.createDialogException("Saving vouchers", "Could not save file information data", e);
	       }
	   }
	
}
