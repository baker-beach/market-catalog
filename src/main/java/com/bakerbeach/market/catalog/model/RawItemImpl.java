package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.List;

public class RawItemImpl implements RawItem {
	protected String name;
	protected String parent;
	protected Boolean isRequired = false;
	protected Integer minQty;
	protected Integer maxQty;
	protected Boolean isMultiselect = false;
	protected List<RawOption> options = new ArrayList<RawOption>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getParent() {
		return parent;
	}

	@Override
	public void setParent(String parent) {
		this.parent = parent;
	}

	@Override
	public Boolean isRequired() {
		return isRequired;
	}

	@Override
	public Boolean getIsRequired() {
		return isRequired;
	}
	
	@Override
	public void setIsRequired(Boolean required) {
		this.isRequired = required;
	}

	@Override
	public Integer getMinQty() {
		return minQty;
	}

	@Override
	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	@Override
	public Integer getMaxQty() {
		return maxQty;
	}

	@Override
	public void setMaxQty(Integer maxQty) {
		this.maxQty = maxQty;
	}

	@Override
	public Boolean isMultiselect() {
		return isMultiselect;
	}
	
	@Override
	public Boolean getIsMultiselect() {
		return isMultiselect;
	}

	@Override
	public void setIsMultiselect(Boolean isMultiselect) {
		this.isMultiselect = isMultiselect;
	}
	
	@Override
	public List<RawOption> getOptions() {
		return options;
	}

	@Override
	public void setOptions(List<RawOption> options) {
		this.options = options;
	}

}
