package za.co.smartcall.smartload.hibernate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class Files {

	private List<File> files;
	
	private Predicate<File> filter;
	
	
	private ObservableList<File> fileData = FXCollections
			.observableArrayList();
	
	
	public void setFilters(final Predicate<File>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(fileInfo->true);
		
		}
	
	public List<File> applyFilters(){
		return files.parallelStream().filter(filter).collect(Collectors.toList());
	}
	
	/**
	 * Returns the data as an observable list of Transactions.
	 * 
	 * @return
	 */
	public ObservableList<File> getFileData(boolean withArchive) {
		
		 fileData.clear();
	    	if (files != null){
	    		if (withArchive)
	    			files.forEach(file ->fileData.add(file));
	    		else {
	    			Predicate<File> filter1 = (fileInfo->!fileInfo.isArchived());
	    			files.stream().filter(filter1).forEach(file ->fileData.add(file));
	    		}
	    	}
		return fileData;
	}
}
