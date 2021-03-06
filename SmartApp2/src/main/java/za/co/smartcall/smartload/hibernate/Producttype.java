package za.co.smartcall.smartload.hibernate;

// Generated 16 Feb 2015 3:45:10 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import za.co.smartcall._2010._12.message.ProductType;

/**
 * Producttype generated by hbm2java
 */
@Entity
@Table(name = "PRODUCTTYPE")
public class Producttype implements java.io.Serializable {

	private int id;
	private Network network;
	private String code;
	private String description;
	private boolean fixed;
	private Set<Product> products = new HashSet<Product>(0);

	public Producttype() {
	}

	public Producttype(int id, Network network, String code,
			String description, boolean fixed) {
		this.id = id + (network.getId()*1000);
		this.network = network;
		this.code = code;
		this.description = description;
		this.fixed = fixed;
	}
	
	public Producttype(ProductType productType,za.co.smartcall._2010._12.common.Network network) {
		this.id = (int)productType.getId() + ((int)network.getId()*1000); // lazy way to handle non unique producttypeIds
		this.network = new Network(network);
		this.code = productType.getCode();
		this.description = productType.getDescription();
		this.fixed = productType.isFixedAmount();
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NETWORK", nullable = false)
	public Network getNetwork() {
		return this.network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	@Column(name = "CODE", nullable = false, length = 5)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "DESCRIPTION", nullable = false, length = 50)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "FIXED", nullable = false)
	public boolean isFixed() {
		return this.fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "producttype")
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

}
