package com.bakerbeach.market.catalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bakerbeach.market.core.api.model.Assets;
import com.bakerbeach.market.core.api.model.Group;
import com.bakerbeach.market.core.api.model.Product;

public class GroupedProductImpl implements GroupedProduct {
	protected List<Product> members = new ArrayList<Product>();
	protected String code;
	protected BigDecimal minPrice;
	protected BigDecimal maxPrice;
	protected BigDecimal minStdPrice;
	protected BigDecimal maxStdPrice;
	protected BigDecimal minDiscountOnStandardPrice;
	protected BigDecimal maxDiscountOnStandardPrice;
	protected String dim1;
	protected String dim2;
	protected Assets assets = null;
	protected List<String> categories = new ArrayList<String>();
	protected Boolean isAvailable = false;
	protected String mainCategory;
	protected Group primaryGroup;
	protected Group secondaryGroup;
	protected String template;

	protected Variants variants;

	public GroupedProductImpl(String code) {
		this.code = code;
	}

	@Override
	public List<Product> getMembers() {
		return members;
	}

	public void setMembers(List<Product> members) {
		this.members = members;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public BigDecimal getMaxStdPrice() {
		return maxStdPrice;
	}

	@Override
	public void setMaxStdPrice(BigDecimal maxStdPrice) {
		this.maxStdPrice = maxStdPrice;
	}

	@Override
	public BigDecimal getMinStdPrice() {
		return minStdPrice;
	}

	@Override
	public void setMinStdPrice(BigDecimal minStdPrice) {
		this.minStdPrice = minStdPrice;
	}

	@Override
	public BigDecimal getMinPrice() {
		return minPrice;
	}

	@Override
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	@Override
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	@Override
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Override
	public BigDecimal getMinDiscountOnStandardPrice() {
		return minDiscountOnStandardPrice;
	}

	@Override
	public void setMinDiscountOnStandardPrice(BigDecimal minDiscountOnStandardPrice) {
		this.minDiscountOnStandardPrice = minDiscountOnStandardPrice;
	}

	@Override
	public BigDecimal getMaxDiscountOnStandardPrice() {
		return maxDiscountOnStandardPrice;
	}

	@Override
	public void setMaxDiscountOnStandardPrice(BigDecimal maxDiscountOnStandardPrice) {
		this.maxDiscountOnStandardPrice = maxDiscountOnStandardPrice;
	}

	@Override
	public Variants getVariants() {
		return variants;
	}

	@Override
	public void setVariants(Variants variants) {
		this.variants = variants;
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
	public Assets getAssets() {
		return assets;
	}

	@Override
	public void setAssets(Assets assets) {
		this.assets = assets;
	}

	@Override
	public List<String> getCategories() {
		return categories;
	}

	@Override
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	@Override
	public Boolean isAvailable() {
		return isAvailable;
	}

	@Override
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	@Override
	public String getMainCategory() {
		return mainCategory;
	}

	@Override
	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public Group getPrimaryGroup() {
		return primaryGroup;
	}

	public void setPrimaryGroup(Group primaryGroup) {
		this.primaryGroup = primaryGroup;
	}

	public Group getSecondaryGroup() {
		return secondaryGroup;
	}

	public void setSecondaryGroup(Group secondaryGroup) {
		this.secondaryGroup = secondaryGroup;
	}

	@Override
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
