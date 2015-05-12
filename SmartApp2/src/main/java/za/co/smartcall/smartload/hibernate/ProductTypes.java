package za.co.smartcall.smartload.hibernate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


@Data
public class ProductTypes {
	
	public ProductTypes(){
		productTypes = new 	ArrayList<Producttype>();	
	}
	private List<Producttype> productTypes;
	
	private Predicate<Producttype> filter;
	
	public void setFilters(final Predicate<Producttype>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(productType->true);
	}
	
	public List<Producttype> applyFilters(){
		List<Producttype> filteredReturns =productTypes.parallelStream().filter(filter).collect(Collectors.toList());
		List<Producttype> filteredReturns2 =  filteredReturns.stream().filter(i ->i !=null).collect(Collectors.toList());
	    return filteredReturns2;
	}
	
	
	public void addTable(Hashtable<za.co.smartcall._2010._12.common.Network,List<za.co.smartcall._2010._12.message.ProductType>> genProductTypes){
		if (productTypes == null) productTypes = new ArrayList<Producttype>();
		genProductTypes.keySet().forEach(network -> addList(network,genProductTypes.get(network)));
	}
	
	private void addList(za.co.smartcall._2010._12.common.Network network,List<za.co.smartcall._2010._12.message.ProductType> list){
		list.forEach(productType ->productTypes.add(new Producttype(productType,network)));
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
