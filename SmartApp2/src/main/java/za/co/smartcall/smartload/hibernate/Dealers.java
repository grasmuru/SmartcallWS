package za.co.smartcall.smartload.hibernate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;


@Data
public class Dealers {

	private List<Dealer> dealers;

	private Predicate<Dealer> filter;
	
	public void setFilters(final Predicate<Dealer>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(network->true);
		
		}
	
	public List<Dealer> applyFilters(){
		return dealers.parallelStream().filter(filter).collect(Collectors.toList());
	}
	
	
	
}
