package com.bakerbeach.market.catalog.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.core.api.model.BundleOption;

public class BundleOptionImpl implements BundleOption {
	private String gtin;
	private BigDecimal price = BigDecimal.ZERO;
	private BigDecimal monthlyPrice = BigDecimal.ZERO;
	private Map<String, BigDecimal> prices = new HashMap<String,BigDecimal>();
	private Integer defaultQty;
	private Integer minQty;
	private Integer maxQty;
	private Integer userDefinedQty;
	private Boolean preset = false;
	private Boolean isDefault = false;
	private Boolean isRequired = false;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BundleOption) {
			return getGtin().equals(((BundleOption) other).getGtin());
		}
		return false;
	}
	
	@Override
	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@Override
	public BigDecimal getMonthlyPrice() {
		return monthlyPrice;
	}
	
	public void setMonthlyPrice(BigDecimal monthlyPrice) {
		this.monthlyPrice = monthlyPrice;
	}

	@Override
	public Integer getDefaultQty() {
		return defaultQty;
	}

	public void setDefaultQty(Integer defaultQty) {
		this.defaultQty = defaultQty;
	}

	@Override
	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	@Override
	public Integer getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Integer maxQty) {
		this.maxQty = maxQty;
	}

	@Override
	public Integer getUserDefinedQty() {
		return userDefinedQty;
	}

	public void setUserDefinedQty(Integer userDefinedQty) {
		this.userDefinedQty = userDefinedQty;
	}

	@Override
	public Boolean isPreset() {
		return preset;
	}

	public void setPreset(Boolean isPreset) {
		this.preset = (isPreset != null)? isPreset : false;
	}
	
	@Override
	public Boolean isDefault() {
		return (isDefault != null)? isDefault : false;
	}
	
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = (isDefault != null)? isDefault : false;
	}
	
	@Override
	public Boolean isRequired() {
		return isRequired;
	}
	
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = (isRequired != null)? isRequired : false;
	}
	
	public Map<String, BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(Map<String, BigDecimal> prices) {
		this.prices = prices;
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
