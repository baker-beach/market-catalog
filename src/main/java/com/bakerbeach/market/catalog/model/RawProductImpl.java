package com.bakerbeach.market.catalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.core.api.model.Assets;
import com.bakerbeach.market.core.api.model.ScaledPrice;
import com.bakerbeach.market.core.api.model.Status;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Type;

public class RawProductImpl extends HashMap<String, Object> implements RawProduct {
	private static final long serialVersionUID = 1L;

	private String gtin;
	private Type type;
	private Status status = Status.UNDEFINED;
	private String brand;
	private String name;
	private RawGroupTag primaryGroup;
	private RawGroupTag secondaryGroup;	
	private String mainCategory;
	private List<String> categories = new ArrayList<String>();
	private Boolean visible = false;
	private Map<String, Boolean> visibilities = new HashMap<String, Boolean>();
	private Boolean index = false;
	private Boolean single = false;
	private Map<String, TaxCode> taxCodes = new HashMap<String, TaxCode>();
	private List<ScaledPrice> stdPrices = new ArrayList<ScaledPrice>();
	private List<ScaledPrice> prices = new ArrayList<ScaledPrice>();
	private Map<String, List<String>> logos = new HashMap<String, List<String>>();
	private Map<String, List<String>> tags = new HashMap<String, List<String>>();
	private Assets assets = new RawAssetsImpl();
	private String size;
	private String color;
	private String diet;
	private String variant1;
	private String variant1Sort;
	private String variant2;
	private String variant2Sort;
	private BigDecimal netWeight;
	private BigDecimal grossWeight;
	private Date startDate = new Date();
	private String sort = "0000";
	private String url;
	private List<RawItem> items = new ArrayList<RawItem>();
	
	@Override
	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Override
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public RawGroupTag getPrimaryGroup() {
		return primaryGroup;
	}
	
	public void setPrimaryGroup(RawGroupTag primaryGroup) {
		this.primaryGroup = primaryGroup;
	}
	
	@Override
	public RawGroupTag getSecondaryGroup() {
		return secondaryGroup;
	}
	
	public void setSecondaryGroup(RawGroupTag secondaryGroup) {
		this.secondaryGroup = secondaryGroup;
	}
	
	@Override
	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	@Override
	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

//	@Override
	public Map<String, Boolean> getVisibilities() {
		return visibilities;
	}
	
	public void setVisibilities(Map<String, Boolean> visibilities) {
		this.visibilities = visibilities;
	}
	
	@Deprecated
	@Override
	public Boolean isVisible() {
		return visible;
	}
	
	public void setIsVisible(Boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public Boolean isIndex() {
		return index;
	}
	
	public void setIsIndex(Boolean index) {
		this.index = index;
	}
	
	@Override
	public Boolean isSingle() {
		return single;
	}
	
	public void setIsSingle(Boolean single) {
		this.single = single;
	}
	
	@Override
	public Map<String, TaxCode> getTaxCodes() {
		return taxCodes;
	}
	
	public void setTaxCodes(Map<String, TaxCode> taxCodes) {
		this.taxCodes = taxCodes;
	}
	
	@Override
	public List<ScaledPrice> getStdPrices() {
		return stdPrices;
	}
	
	public void setStdPrices(List<ScaledPrice> stdPrices) {
		this.stdPrices = stdPrices;
	}
	
	@Override
	public List<ScaledPrice> getPrices() {
		return prices;
	}
	
	public void setPrices(List<ScaledPrice> prices) {
		this.prices = prices;
	}
	
	@Override
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	@Override
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String getDiet() {
		return diet;
	}
	
	public void setDiet(String diet) {
		this.diet = diet;
	}
	
	@Override
	public String getVariant1() {
		return variant1;
	}
	
	public void setVariant1(String variant1) {
		this.variant1 = variant1;
	}

	@Override
	public String getVariant1Sort() {
		return variant1Sort;
	}
	
	public void setVariant1Sort(String variant1Sort) {
		this.variant1Sort = variant1Sort;
	}
	
	@Override
	public String getVariant2() {
		return variant2;
	}
	
	public void setVariant2(String variant2) {
		this.variant2 = variant2;
	}
	
	@Override
	public String getVariant2Sort() {
		return variant2Sort;
	}
	
	public void setVariant2Sort(String variant2Sort) {
		this.variant2Sort = variant2Sort;
	}
	
	@Override
	public BigDecimal getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(BigDecimal netWeight) {
		this.netWeight = netWeight;
	}
	
	@Override
	public BigDecimal getGrossWeight() {
		return grossWeight;
	}
	
	public void setGrossWeight(BigDecimal grossWeight) {
		this.grossWeight = grossWeight;
	}

	@Override
	public Map<String, List<String>> getLogos() {
		return logos;
	}
	
	public void setLogos(Map<String, List<String>> logos) {
		this.logos = logos;
	}
	
	@Override
	public Map<String, List<String>> getTags() {
		return tags;
	}
	
	public void setTags(Map<String, List<String>> tags) {
		this.tags = tags;
	}
	
	@Override
	public Assets getAssets() {
		return assets;
	}
	
	public void setAssets(Assets assets) {
		this.assets = assets;
	}
	
	@Override
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Override
	public String getSort() {
		return sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<RawItem> getItems() {
		return items;
	}
	
}
