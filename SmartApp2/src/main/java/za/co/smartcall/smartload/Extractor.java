package za.co.smartcall.smartload;

import java.io.File;
import java.util.List;

import lombok.extern.log4j.Log4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
/**
 * Extarcts content from encrypted zip file with password to
 * designated directory.
 * @author rudig
 *
 */
@Log4j
public class Extractor {
	
	public String extractLocation = "not set";
	
	/**
	 * Passed location of the extracted files
	 * @param extractLocation
	 */
    public Extractor(String extractLocation) {
		this.extractLocation = extractLocation;
		File f = new File(extractLocation);
		f.mkdirs();
	}

	
   public void  extractSingleFile(String fileNameAndLocation,String password) {
		log.info("Extracting " +fileNameAndLocation + " to " + extractLocation);
		try {
			
			ZipFile zipFile = new ZipFile(fileNameAndLocation);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			fileHeaderList.forEach(
			    fileHeader->	 {
				 try {
				    zipFile.extractFile(fileHeader, extractLocation);
			     } catch (ZipException ze){
			    	 log.error("Error extracting",ze);
			     }
			     }
			    );
		
			log.info("Extract " + fileHeaderList.get(0).getFileName());
		} catch (ZipException e) {
			log.error("Zip error ",e);
		}
		
	}
	
	
}
