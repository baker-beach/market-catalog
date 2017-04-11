package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.core.api.model.BundleComponent;
import com.bakerbeach.market.core.api.model.BundleOption;

public class BundleComponentImpl implements BundleComponent {
	private String name;
	private String parent;
	private String description1;
	private String description2;
	private String description3;
	private String description4;
	private String description5;
	private Boolean required = false;
	private Integer minQty;
	private Integer maxQty;
	private Boolean isMultiselect = false;
	private List<BundleOption> options = new ArrayList<BundleOption>();
	private Map<String, Object> attributes = new HashMap<String, Object>();

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
	public String getDescription1() {
		return description1;
	}

	@Override
	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	@Override
	public String getDescription2() {
		return description2;
	}

	@Override
	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	@Override
	public String getDescription3() {
		return description3;
	}

	@Override
	public void setDescription3(String description3) {
		this.description3 = description3;
	}

	@Override
	public String getDescription4() {
		return description4;
	}

	@Override
	public void setDescription4(String description4) {
		this.description4 = description4;
	}

	@Override
	public String getDescription5() {
		return description5;
	}

	@Override
	public void setDescription5(String description5) {
		this.description5 = description5;
	}

	@Override
	public Boolean isRequired() {
		return (required != null)? required : false;
	}
	
	public void setIsRequired(Boolean isRequired) {
		this.required = (isRequired != null)? isRequired : false;
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
	public void setIsMultiselect(Boolean isMultiselect) {
		this.isMultiselect = (isMultiselect != null)? isMultiselect : false;
	}

	@Override
	public List<BundleOption> getOptions() {
		return options;
	}
	
	@Override
	public BundleOption getOption(String gtin) {
		for (BundleOption option : options) {
			if (option.getGtin().equals(gtin))
				return option;
		}
		
		return null;
	}

	@Override
	public Map<String,Object> getAttributes() {
		return attributes;
	}

}
