package za.co.smartcall.smartload.hibernate;

import java.util.ArrayList;
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
public class Networks {

	private List<Network> networks;

	private Predicate<Network> filter;
	
	public void setFilters(final Predicate<Network>... filters) {
		filter =
		Stream.of(filters)
		.reduce((filter, next) -> filter.and(next)).orElse(network->true);
		
		}
	
	public List<Network> applyFilters(){
		return networks.parallelStream().filter(filter).collect(Collectors.toList());
	}
	
	public void addList(List<za.co.smartcall._2010._12.common.Network> genNetworks){
		if (networks == null) networks = new ArrayList<Network>();
		genNetworks.forEach(network -> networks.add(new Network(network)));
	}
	

	
	
	
}
