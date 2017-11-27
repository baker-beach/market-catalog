package com.bakerbeach.market.xcatalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;

import com.bakerbeach.market.xcatalog.model.Product.Status;

@Entity(noClassnameStored = true)
public class GroupImpl implements Group {

	@Id
	protected ObjectId id;
	protected String code;
	@Property("shop_code")
	protected String shopCode;
	protected Status status;
	@Property("dim_1")
	protected String dim1;
	@Property("dim_2")
	protected String dim2;
	protected String template;
	@Transient
	protected Map<String, BigDecimal> minPrices;
	@Transient
	protected List<Product> members = new ArrayList<>();
	@Transient
	protected Map<String, Price> cachedMinPrices = new HashMap<>();

	public Map<String, Price> getCachedMinPrices() {
		return cachedMinPrices;
	}

	@Override
	public void setCachedMinPrices(Map<String, Price> cachedMinPrices) {
		this.cachedMinPrices = cachedMinPrices;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getShopCode() {
		return shopCode;
	}

	@Override
	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String getDim1() {
		return dim1;
	}

	@Override
	public void setDim1(String dim1) {
		this.dim1 = dim1;
	}

	@Override
	public String getDim2() {
		return dim2;
	}

	@Override
	public void setDim2(String dim2) {
		this.dim2 = dim2;
	}

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public List<Product> getMembers() {
		return members;
	}

	@Override
	public void setMembers(List<Product> members) {
		this.members = members;
	}

}
