package za.co.smartcall.smartload.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import za.co.smartcall._2010._12.common.Network;
import za.co.smartcall._2010._12.message.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(callSuper=false)
public class Products {
	
	public Products(){
		products = new 	ArrayList<Product>();	
	}
	
	@XmlElement(name = "products")
	private List<Product> products;
	
	@XmlTransient
	private Predicate<Product> filter;
	
	public void setFilters(final Predicate<Product>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(product->true);
	}
	
	public List<Product> applyFilters(){
		List<Product> filteredReturns = new ArrayList<Product>();
		products.parallelStream().filter(filter).forEach(product->filteredReturns.add(product));
		List<Product> filteredReturns2 = filteredReturns.stream().filter(i ->i !=null).collect(Collectors.toList());
	    return filteredReturns2;
	}
	
	public void addList(List<za.co.smartcall._2010._12.message.Product> list,ProductType type,Network network){
		list.forEach(product ->products.add(new Product(product,type,network)));
	}
	
}
