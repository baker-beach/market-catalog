package com.bakerbeach.market.catalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.core.api.model.Asset;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.Assets;
import com.bakerbeach.market.core.api.model.Product;
import com.bakerbeach.market.core.api.model.Status;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Type;

public abstract class AbstractProduct extends HashMap<String, Object> implements Product {
	protected static final long serialVersionUID = 1L;
	
	protected String gtin;
	protected Status status = Status.UNDEFINED;
	protected String brand;
	protected String name;
	protected String description1;
	protected String description2;
	protected String description3;
	protected String description4;
	protected String description5;
	protected String primaryGroup;
	protected String secondaryGroup;
	protected String mainCategory;
	protected List<String> categories = new ArrayList<String>();
	protected Boolean visible;
	protected TaxCode taxCode;
	protected BigDecimal stdPrice;
	protected BigDecimal price;
	protected BigDecimal monthlyPrice;
	protected BigDecimal basePrice1;
	protected BigDecimal basePrice1Divisor;
	protected String basePrice1Unit;
	protected BigDecimal basePrice2;
	protected BigDecimal basePrice2Divisor;
	protected String basePrice2Unit;
	protected String variant1;
	protected String variant1Sort;
	protected String variant2;
	protected String variant2Sort;
	protected String materialText;
	protected List<String> materialCodes;
	protected String technologyText;
	protected List<String> technologyCodes;
	protected String careText;
	protected List<String> careCodes = new ArrayList<String>();
	protected String size;
	protected String color;
	protected String diet;
	protected BigDecimal netWeight;
	protected BigDecimal grossWeight;
	protected Map<String, List<String>> logos = new HashMap<String, List<String>>();
	protected Map<String, List<String>> tags = new HashMap<String, List<String>>();
	protected Assets assets = null;
	protected String additionalText1;
	protected String additionalText2;
	protected String additionalText3;
	protected String additionalText4;
	protected String additionalText5;
	protected Date startDate = new Date();
	protected String sort = "0000";
	protected Integer moq;
	protected Integer outOfStockLimit;
	protected Boolean available = false;
	protected Map<String, BigDecimal> prices = new HashMap<String, BigDecimal>();

	public Map<String, BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(Map<String, BigDecimal> prices) {
		this.prices = prices;
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getDescription3() {
		return description3;
	}

	public void setDescription3(String description3) {
		this.description3 = description3;
	}

	public String getDescription4() {
		return description4;
	}

	public void setDescription4(String description4) {
		this.description4 = description4;
	}

	public String getDescription5() {
		return description5;
	}

	public void setDescription5(String description5) {
		this.description5 = description5;
	}
	
	public String getPrimaryGroup() {
		return primaryGroup;
	}
	
	public void setPrimaryGroup(String primaryGroup) {
		this.primaryGroup = primaryGroup;
	}
	
	public String getSecondaryGroup() {
		return secondaryGroup;
	}
	
	public void setSecondaryGroup(String secondaryGroup) {
		this.secondaryGroup = secondaryGroup;
	}
	
	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public Boolean getVisible() {
		return visible;
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public TaxCode getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	@Override
	public BigDecimal getStdPrice() {
		return stdPrice;
	}

	@Override
	public void setStdPrice(BigDecimal stdPrice) {
		this.stdPrice = stdPrice;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@Override
	public BigDecimal getMonthlyPrice() {
		return monthlyPrice;
	}
	
	@Override
	public void setMonthlyPrice(BigDecimal monthlyPrice) {
		this.monthlyPrice = monthlyPrice;
	}

	@Override
	public BigDecimal getDiscountOnStandardPrice() {
		if (stdPrice.compareTo(BigDecimal.ZERO) != 0) {
			return (stdPrice.subtract(price)).divide(stdPrice, 4, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100))
					.setScale(0, BigDecimal.ROUND_DOWN);			
		} else {
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal getBasePrice1() {
		return basePrice1;
	}

	public void setBasePrice1(BigDecimal basePrice1) {
		this.basePrice1 = basePrice1;
	}

	public BigDecimal getBasePrice1Divisor() {
		return basePrice1Divisor;
	}

	public void setBasePrice1Divisor(BigDecimal basePrice1Divisor) {
		this.basePrice1Divisor = basePrice1Divisor;
	}

	public String getBasePrice1Unit() {
		return basePrice1Unit;
	}

	public void setBasePrice1Unit(String basePrice1Unit) {
		this.basePrice1Unit = basePrice1Unit;
	}

	public BigDecimal getBasePrice2() {
		return basePrice2;
	}

	public void setBasePrice2(BigDecimal basePrice2) {
		this.basePrice2 = basePrice2;
	}

	public BigDecimal getBasePrice2Divisor() {
		return basePrice2Divisor;
	}

	public void setBasePrice2Divisor(BigDecimal basePrice2Divisor) {
		this.basePrice2Divisor = basePrice2Divisor;
	}

	public String getBasePrice2Unit() {
		return basePrice2Unit;
	}

	public void setBasePrice2Unit(String basePrice2Unit) {
		this.basePrice2Unit = basePrice2Unit;
	}

	public String getMaterialText() {
		return materialText;
	}

	public void setMaterialText(String materialText) {
		this.materialText = materialText;
	}

	public List<String> getMaterialCodes() {
		return materialCodes;
	}

	public void setMaterialCodes(List<String> materialCodes) {
		this.materialCodes = materialCodes;
	}

	public String getTechnologyText() {
		return technologyText;
	}

	public void setTechnologyText(String technologyText) {
		this.technologyText = technologyText;
	}

	public List<String> getTechnologyCodes() {
		return technologyCodes;
	}

	public void setTechnologyCodes(List<String> technologyCodes) {
		this.technologyCodes = technologyCodes;
	}

	public String getCareText() {
		return careText;
	}

	public void setCareText(String careText) {
		this.careText = careText;
	}

	public List<String> getCareCodes() {
		return careCodes;
	}

	public void setCareCodes(List<String> careCodes) {
		this.careCodes = careCodes;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public String getDiet() {
		return diet;
	}
	
	public void setDiet(String diet) {
		this.diet = diet;
	}

	public BigDecimal getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(BigDecimal netWeight) {
		this.netWeight = netWeight;
	}

	public BigDecimal getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(BigDecimal grossWeight) {
		this.grossWeight = grossWeight;
	}

	public Map<String, List<String>> getLogos() {
		return logos;
	}

	public void setLogos(Map<String, List<String>> logos) {
		this.logos = logos;
	}

	public Map<String, List<String>> getTags() {
		return tags;
	}

	public void setTags(Map<String, List<String>> tags) {
		this.tags = tags;
	}

	public Assets getAssets() {
		return assets;
	}
	
	public Asset getAsset(String type, int i, String size) {
		if (assets != null && assets.containsKey(type)) {
			List<AssetGroup> assetGroups = assets.get(type);
			if (assetGroups.size() > i) {
				AssetGroup assetGroup = assetGroups.get(i);
				if (assetGroup.containsKey(size)) {
					return assetGroup.get(size);
				}
			}
		}
		
		return null;
	}


	public void setAssets(Assets assets) {
		this.assets = assets;
	}

	public String getAdditionalText1() {
		return additionalText1;
	}

	public void setAdditionalText1(String additionalText1) {
		this.additionalText1 = additionalText1;
	}

	public String getAdditionalText2() {
		return additionalText2;
	}

	public void setAdditionalText2(String additionalText2) {
		this.additionalText2 = additionalText2;
	}

	public String getAdditionalText3() {
		return additionalText3;
	}

	public void setAdditionalText3(String additionalText3) {
		this.additionalText3 = additionalText3;
	}

	public String getAdditionalText4() {
		return additionalText4;
	}

	public void setAdditionalText4(String additionalText4) {
		this.additionalText4 = additionalText4;
	}

	public String getAdditionalText5() {
		return additionalText5;
	}

	public void setAdditionalText5(String additionalText5) {
		this.additionalText5 = additionalText5;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public Integer getMoq() {
		return moq;
	}

	@Override
	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public Integer getOutOfStockLimit() {
		return outOfStockLimit;
	}

	public void setOutOfStockLimit(Integer outOfStockLimit) {
		this.outOfStockLimit = outOfStockLimit;
	}

	public Boolean getAvailable() {
		return available;
	}

	public Boolean isAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	@Override
	public String getVariant1() {
		return variant1;
	}
	
	public void setVariant1(String variant1) {
		this.variant1 = variant1;
	}
	
	@Override
	public String getVariant2() {
		return variant2;
	}
	
	public void setVariant2(String variant2) {
		this.variant2 = variant2;
	}
	
	@Override
	public String getVariant1Sort() {
		return variant1Sort;
	}
	
	public void setVariant1Sort(String variant1Sort) {
		this.variant1Sort = variant1Sort;
	}

	@Override
	public String getVariant2Sort() {
		return variant2Sort;
	}
	
	public void setVariant2Sort(String variant2Sort) {
		this.variant2Sort = variant2Sort;
	}

	public abstract Type getType();

	
}
