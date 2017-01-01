package com.bakerbeach.market.catalog.model;

import com.bakerbeach.market.core.api.model.Assets;

public class RawGroupTagImpl implements RawGroupTag {
	private String code;
	private String brand;
	private Assets assets = null;
	private String dim1;
	private String dim2;
	private String sort;
	
	public RawGroupTagImpl(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public Assets getAssets() {
		return assets;
	}

	public void setAssets(Assets assets) {
		this.assets = assets;
	}

	@Override
	public String getDim1() {
		return dim1;
	}

	public void setDim1(String dim1) {
		this.dim1 = dim1;
	}

	@Override
	public String getDim2() {
		return dim2;
	}

	public void setDim2(String dim2) {
		this.dim2 = dim2;
	}

	@Override
	public String getSort() {
		return this.sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
}
