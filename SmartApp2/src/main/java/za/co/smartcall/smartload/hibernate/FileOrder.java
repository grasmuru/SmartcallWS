package za.co.smartcall.smartload.hibernate;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class FileOrder {

	private Network network;
	
	private Product product;
	
	private Producttype productType;
	
	private boolean selected;
	
	private int amount;
	
	private String history;
		
	public StringProperty network() {
		 return new SimpleStringProperty(network.getNetwork());
   }
	
		
	
	public StringProperty product() {
       return new SimpleStringProperty(product.getName());
   }
	
	
	public StringProperty productType() {
		 return new SimpleStringProperty(productType.getDescription());
   }
	
	public BooleanProperty selected() {
		 return new SimpleBooleanProperty(selected);
   }
	
	
	public StringProperty amount() {
       return new SimpleStringProperty(Integer.toString(amount));
   }
	
	public StringProperty history() {
	       return new SimpleStringProperty(history);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
