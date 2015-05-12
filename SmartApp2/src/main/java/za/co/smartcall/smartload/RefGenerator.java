package za.co.smartcall.smartload;

import za.co.smartcall.smartload.model.DataAccess;

public class RefGenerator {
	
	
	    static int interval = 10;
	
	    static long count = 0;
	    
	    private static RefGenerator instance = null;
  		
	    public static synchronized RefGenerator getInstance(){
	    	if (instance == null){
	    		instance = new RefGenerator();
	    		count = DataAccess.loadCountFromDB(interval);
	    	}
	    	return instance;
	    }
    

		public String getCount(){
	    	count = count + 1;
	    	if ((count%interval == 0)&&(count > interval-1)) {
	    		count = DataAccess.loadCountFromDB(interval);
	    	}
	    	return Long.toString(count);
	    }
	    
	    
		public String getCurrentCount(){
	    	return Long.toString(count);
	    }
		
		public void updateCount(String pcount){
			count = Integer.parseInt(pcount);
			DataAccess.updateCount(pcount);
		}
	
}
