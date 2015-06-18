package za.co.smartcall.smartload;

import java.util.prefs.Preferences;

import lombok.extern.log4j.Log4j;

@Log4j
public class RefPreferences {
	
	
	
	public static String DEFAULT_DOWNLOAD_LOCATION = "Not set";
	    
	public static String DEFAULT_EXTRACT_LOCATION = "Not set";
	    
	public static String DEFAULT_LOGGING_LOCATION = "Not set";
	    
	public static String DEFAULT_ARCHIVE_LOCATION = "Not set";
	    
	public static String DEFAULT_LOG_LEVEL ="INFO";
	
	public static String dealerMsisdn;
	    


		    
	    public static String  getImportSelection() {
	    	   Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	           String url = prefs.get("importSelection", null);
	           if (url == null){
	           	setImportSelection("XML File");
	           	return "XML File";
	           }
	           return url;
	    }
	    
	   /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setImportSelection(String importSelection) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (importSelection != null) {
	            prefs.put("importSelection", importSelection);
	        } else {
	            prefs.remove("importSelection"); // what is this remove!?
	        }
	    }   
	    
	    public static String  getLogLevel() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String debugLevel = prefs.get("logLevel", null);
	        if (debugLevel == null){
	        	setLogLevel(DEFAULT_LOG_LEVEL);
	        	return DEFAULT_LOG_LEVEL;
	        }
	        return debugLevel;
	    }

	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setLogLevel(String location) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (location != null) {
	            prefs.put("logLevel", location);
	        } else {
	            prefs.remove("logLevel");
	        }
	    }    
	    
	    
	    
	    
	    public static String  getDownloadLocation() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String url = prefs.get("downloadLocation", null);
	        if (url == null){
	        	setDownloadLocation(DEFAULT_DOWNLOAD_LOCATION);
	        	return DEFAULT_DOWNLOAD_LOCATION;
	        }
	        return url;
	    }

	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setDownloadLocation(String location) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (location != null) {
	            prefs.put("downloadLocation", location);
	        } else {
	            prefs.remove("downloadLocation");
	        }
	    }
	    
	    public static String  getArchiveLocation() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String location = prefs.get("archiveLocation", null);
	        if (location == null){
	        	setDownloadLocation(DEFAULT_ARCHIVE_LOCATION);
	        	return DEFAULT_ARCHIVE_LOCATION;
	        }
	        return location;
	    }

	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setArchiveLocation(String location) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (location != null) {
	            prefs.put("archiveLocation", location);
	        } else {
	            prefs.remove("archiveLocation");
	        }
	    }
	    
	    
	    public static String  getExtractLocation() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String url = prefs.get("extractLocation", null);
	        if (url == null){
	        	setExtractLocation(DEFAULT_EXTRACT_LOCATION);
	        	return DEFAULT_EXTRACT_LOCATION;
	        }
	        return url;
	    }
	    
	    public static String  getLoggingLocation() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String url = prefs.get("loggingLocation", null);
	        if (url == null){
	        	setExtractLocation(DEFAULT_LOGGING_LOCATION);
	        	return DEFAULT_LOGGING_LOCATION;
	        }
	        return url;
	    }
	    
	    public static void setLoggingLocation(String location) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (location != null) {
	            prefs.put("loggingLocation", location);
	        } else {
	            prefs.remove("loggingLocation");
	        }
	    }

	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setExtractLocation(String location) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (location != null) {
	            prefs.put("extractLocation", location);
	        } else {
	            prefs.remove("extractLocation");
	        }
	    }
    
	    public static String  getDealerMsisdn() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        dealerMsisdn = prefs.get("dealerMsisdn", null);
	        return dealerMsisdn;
	    }
	    
	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setDealerMsisdn(String dealerMsisdn) {
	    	log.info("Setting dealer to " + dealerMsisdn);
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (dealerMsisdn != null) {
	            prefs.put("dealerMsisdn", dealerMsisdn);
	        } else {
	            prefs.remove("dealerMsisdn");
	        }
	        
	    }
	    
	    public static String  getWebServiceUrl() {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        String url = prefs.get("url", null);
	        if (url == null){
	        	setWebServiceUrl(MainApp.ENDPOINT_ADDRESS_BASE);
	        	return MainApp.ENDPOINT_ADDRESS_BASE;
	        }
	        return url;
	    }

	    /**
	     * Sets the file path of the currently loaded file. The path is persisted in
	     * the OS specific registry.
	     * 
	     * @param file the file or null to remove the path
	     */
	    public static void setWebServiceUrl(String url) {
	        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	        if (url != null) {
	            prefs.put("url", url);
	        } else {
	            prefs.remove("url");
	        }
	    }

}
