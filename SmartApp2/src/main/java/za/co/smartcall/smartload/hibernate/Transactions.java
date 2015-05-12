package za.co.smartcall.smartload.hibernate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class Transactions {
	
	private List<Transaction> transactions;
	
	private Predicate<Transaction> filter;
	
	private ObservableList<Transaction> transactionData = FXCollections
			.observableArrayList();
	
	
	public void setFilters(final Predicate<Transaction>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(transaction->true);
		
		}
	
	public List<Transaction> applyFilters(){
		return transactions.parallelStream().filter(filter).collect(Collectors.toList());
	}
	
	
	public ObservableList<Transaction> returnAsObservableList(){
		transactionData.clear();
	    	if (transactions != null)
	    		transactions.forEach(transaction ->transactionData.add(transaction));
		return transactionData;
	}
}
