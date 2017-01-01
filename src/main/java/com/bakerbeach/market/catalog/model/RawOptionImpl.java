package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.List;

import com.bakerbeach.market.core.api.model.ScaledPrice;

public class RawOptionImpl implements RawOption {
	protected String gtin;
	protected Integer defaultQty;
	protected Integer minQty;
	protected Integer maxQty;
	protected Integer userDefinedQty;
	protected Boolean isPreset = false;
	protected Boolean isDefault = false;
	protected Boolean isRequired = false;
	protected List<ScaledPrice> prices = new ArrayList<ScaledPrice>();

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public Integer getDefaultQty() {
		return defaultQty;
	}

	public void setDefaultQty(Integer defaultQty) {
		this.defaultQty = defaultQty;
	}

	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	public Integer getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Integer maxQty) {
		this.maxQty = maxQty;
	}

	public Integer getUserDefinedQty() {
		return userDefinedQty;
	}

	public void setUserDefinedQty(Integer userDefinedQty) {
		this.userDefinedQty = userDefinedQty;
	}

	public Boolean isPreset() {
		return isPreset;
	}

	public Boolean getIsPreset() {
		return isPreset;
	}
	
	public void setIsPreset(Boolean isPreset) {
		this.isPreset = isPreset;
	}

	public Boolean isDefault() {
		return isDefault;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Boolean isRequired() {
		return isRequired;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public List<ScaledPrice> getPrices() {
		return prices;
	}

	public void setPrices(List<ScaledPrice> prices) {
		this.prices = prices;
	}

}
