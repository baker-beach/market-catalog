package com.bakerbeach.market.xcatalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;

import com.bakerbeach.market.core.api.model.TaxCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(value = "product")
public class ProductImpl implements Product, PriceAware {
	@Id
	protected ObjectId id;
	protected String code;
	protected String name;
	protected String gtin;
	@Property("shop_code")
	protected List<String> shopCode = new ArrayList<>();
	protected Status status = Product.Status.PUBLISHED;
	@Property("parent_code")
	protected String parentCode;
	protected Type type = Product.Type.PRODUCT;
	protected Unit unit = Product.Unit.SINGLE;
	protected String brand;
	@Property("primary_group")
	protected String primaryGroup;
	@Property("secondary_group")
	protected String secondaryGroup;
	protected List<Price> prices = new ArrayList<Price>();
	@Property("base_price1_divisor")
	protected BigDecimal basePrice1Divisor;
	@Property("base_price1_unit")
	protected String basePrice1Unit;
	@Property("base_price2_divisor")
	protected BigDecimal basePrice2Divisor;
	@Property("base_price2_unit")
	protected String basePrice2Unit;
	protected Map<String, List<Map<String, Asset>>> assets = new HashMap<>();
	@Property("required")
	protected Boolean isRequired = false;
	@Property("available")
	protected Boolean isAvailable = true;
	@Property("configurable")
	protected Boolean isConfigurable = false;
	@Property("size_code")
	protected String sizeCode;
	@Property("color_code")
	protected String colorCode;
	@Property("diet_code")
	protected String dietCode;
	@Property("material_codes")
	protected List<String> materialCodes;
	@Property("technology_codes")
	protected List<String> technologyCodes;
	@Property("net_weight")
	protected BigDecimal netWeight;
	@Property("gross_weight")
	protected BigDecimal grossWeight;
	protected Map<String, List<String>> logos = new HashMap<String, List<String>>();
	protected Map<String, List<String>> tags = new HashMap<String, List<String>>();
	@Property("tax_code")
	protected TaxCode taxCode = TaxCode.NORMAL;
	@Embedded
	protected LinkedHashMap<String, Component> components = new LinkedHashMap<>();
	@Embedded
	protected LinkedHashMap<String, Option> options = new LinkedHashMap<>();
	protected List<String> categories = new ArrayList<>();
	@Transient
	protected Map<String, Price> cachedPrices = new HashMap<>();

	/*
	 * public static Map<String, Price> getPrice(List<Price> prices, Currency
	 * currency, String priceGroup, Date date) { Map<String, Price> priceMap =
	 * new HashMap<>(); Map<String, Price> defaultPriceMap = new HashMap<>();
	 * 
	 * for (Price p : prices) { String tag = p.getTag();
	 * 
	 * Date start = p.getStart(); if (!start.after(date)) { if
	 * (currency.equals(p.getCurrency())) { if (priceGroup.equals(p.getGroup()))
	 * { if (priceMap.get(tag) == null) { priceMap.put(tag, p); } else if
	 * (priceMap.get(tag).getStart().before(p.getStart())) { priceMap.put(tag,
	 * p); } } else if ("default".equalsIgnoreCase(p.getGroup())) { if
	 * (defaultPriceMap.get(tag) == null) { defaultPriceMap.put(tag, p); } else
	 * if (defaultPriceMap.get(tag).getStart().before(p.getStart())) {
	 * defaultPriceMap.put(tag, p); } } } } }
	 * 
	 * // get default prices --- defaultPriceMap.forEach((tag, price) -> { if
	 * (!priceMap.containsKey(tag)) { priceMap.put(tag, price); } });
	 * 
	 * return priceMap; }
	 * 
	 * public static Price getPrice(List<Price> prices, String tag, Currency
	 * currency, String priceGroup, Date date) { Price price = null; Price
	 * defaultPrice = null;
	 * 
	 * for (Price p : prices) { if (tag.equals(p.getTag())) { Date start =
	 * p.getStart(); if (!start.after(date)) { if
	 * (currency.equals(p.getCurrency())) { if (priceGroup.equals(p.getGroup()))
	 * { if (price == null) { price = p; } else if
	 * (price.getStart().before(p.getStart())) { price = p; } } else if
	 * ("default".equalsIgnoreCase(p.getGroup())) { if (defaultPrice == null) {
	 * defaultPrice = p; } else if
	 * (defaultPrice.getStart().before(p.getStart())) { defaultPrice = p; } } }
	 * } } }
	 * 
	 * return (price != null) ? price : (defaultPrice != null) ? defaultPrice :
	 * null; }
	 */

	// @Override
	// public Price getPrice(String tag, Currency currency, String priceGroup,
	// Date date) {
	// return ProductImpl.getPrice(prices, tag, currency, priceGroup, date);
	// }

	// @Override
	// public Price getPrice(Currency currency, String priceGroup, Date date) {
	// return getPrice("std", currency, priceGroup, date);
	// }

	// @Override
	// public Map<String, Price> getCurrentPrices(Currency currency, String
	// priceGroup, Date date) {
	// return ProductImpl.getPrice(prices, currency, priceGroup, date);
	// }

	@Override
	public List<Price> getPrices() {
		return prices;
	}

	@Override
	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	@Override
	public void addPrice(Price price) {
		this.prices.add(price);
	}

	@Override
	public Map<String, Price> getCachedPrices() {
		return cachedPrices;
	}

	@Override
	public void setCachedPrices(Map<String, Price> cachedPrices) {
		this.cachedPrices = cachedPrices;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Override
	public List<String> getShopCode() {
		return shopCode;
	}

	@Override
	public void addShopCode(String shopCode) {
		this.shopCode.add(shopCode);
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(Product.Status status) {
		this.status = status;
	}

	@Override
	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Unit getUnit() {
		return unit;
	}

	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String getPrimaryGroup() {
		return primaryGroup;
	}

	public void setPrimaryGroup(String primaryGroup) {
		this.primaryGroup = primaryGroup;
	}

	@Override
	public String getSecondaryGroup() {
		return secondaryGroup;
	}

	public void setSecondaryGroup(String secondaryGroup) {
		this.secondaryGroup = secondaryGroup;
	}

	public BigDecimal getBasePrice1Divisor() {
		return basePrice1Divisor;
	}

	public void setBasePrice1Divisor(BigDecimal basePrice1Divisor) {
		this.basePrice1Divisor = basePrice1Divisor;
	}

	@Override
	public String getBasePrice1Unit() {
		return basePrice1Unit;
	}

	public void setBasePrice1Unit(String basePrice1Unit) {
		this.basePrice1Unit = basePrice1Unit;
	}

	@Override
	public BigDecimal getBasePrice2Divisor() {
		return basePrice1Divisor;
	}

	public void setBasePrice2Divisor(BigDecimal basePrice2Divisor) {
		this.basePrice2Divisor = basePrice2Divisor;
	}

	@Override
	public String getBasePrice2Unit() {
		return basePrice2Unit;
	}

	public void setBasePrice2Unit(String basePrice2Unit) {
		this.basePrice2Unit = basePrice2Unit;
	}

	@Override
	public Map<String, List<Map<String, Asset>>> getAssets() {
		return assets;
	}

	@Override
	public void setAssets(Map<String, List<Map<String, Asset>>> assets) {
		this.assets = assets;
	}

	@Override
	public List<Asset> getAssets(String tag, String size) {
		List<Asset> toBeReturned = new ArrayList<Asset>();

		List<Map<String, Asset>> groups = assets.get(tag);
		if (groups != null) {
			for (Map<String, Asset> group : groups) {
				Asset asset = group.get(size);
				if (asset != null) {
					toBeReturned.add(asset);
				}
			}
		}

		return toBeReturned;
	}

	public void addAsset(String tag, Map<String, Asset> assetGroup) {
		if (!assets.containsKey(tag)) {
			assets.put(tag, new ArrayList<Map<String, Asset>>());
		}

		assets.get(tag).add(assetGroup);
	}

	@Override
	public Map<String, Component> getComponents() {
		return components;
	}

	@Override
	public Component getComponent(String code) {
		return StringUtils.isNotEmpty(code) ? components.get(code) : null;
	}

	@Override
	public Map<String, Option> getOptions() {
		return options;
	}

	@Override
	public Option getOption(String code) {
		return StringUtils.isNotEmpty(code) ? options.get(code) : null;
	}

	@Override
	public Boolean isRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public Boolean isAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	@Override
	public Boolean isConfigurable() {
		return isConfigurable;
	}

	public void setIsConfigurable(Boolean isConfigurable) {
		this.isConfigurable = isConfigurable;
	}

	@Override
	public String getSizeCode() {
		return sizeCode;
	}

	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}

	@Override
	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	@Override
	public String getDietCode() {
		return dietCode;
	}

	public void setDietCode(String dietCode) {
		this.dietCode = dietCode;
	}

	@Override
	public List<String> getMaterialCodes() {
		return materialCodes;
	}

	public void setMaterialCodes(List<String> materialCodes) {
		this.materialCodes = materialCodes;
	}

	@Override
	public List<String> getTechnologyCodes() {
		return technologyCodes;
	}

	public void setTechnologyCodes(List<String> technologyCodes) {
		this.technologyCodes = technologyCodes;
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
	public TaxCode getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	@Override
	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public static class ComponentImpl implements Component {
		protected String code;
		protected String parentCode;
		protected BigDecimal minQty = BigDecimal.ZERO;
		protected BigDecimal maxQty = BigDecimal.ONE;
		protected Boolean isMultiselect = false;

		@Override
		@JsonIgnore
		public Boolean isRequired() {
			return minQty.compareTo(BigDecimal.ZERO) == 1;
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
		public String getParentCode() {
			return parentCode;
		}

		@Override
		public void setParentCode(String parentCode) {
			this.parentCode = parentCode;
		}

		@Override
		public BigDecimal getMinQty() {
			return minQty;
		}

		@Override
		public void setMinQty(BigDecimal minQty) {
			this.minQty = minQty;
		}

		@Override
		public BigDecimal getMaxQty() {
			return maxQty;
		}

		@Override
		public void setMaxQty(BigDecimal maxQty) {
			this.maxQty = maxQty;
		}

		@Override
		public Boolean isMultiselect() {
			return isMultiselect;
		}

		@Override
		@JsonProperty(value = "multiselect")
		public void setIsMultiselect(Boolean isMultiselect) {
			this.isMultiselect = isMultiselect;
		}

	}

	public static class OptionImpl implements Option, PriceAware {
		protected String code;
		protected String gtin;
		protected String componentCode;
		protected String tag;
		protected BigDecimal minQty = BigDecimal.ZERO;
		protected BigDecimal maxQty = BigDecimal.ONE;
		protected BigDecimal defaultQty;
		protected Boolean isMultiselect = false;
		protected Boolean isDefault = false;
		protected List<Price> prices = new ArrayList<Price>();
		@Transient
		protected Map<String, Price> cachedPrices = new HashMap<>();

		@Override
		public boolean isRequired() {
			return minQty.compareTo(BigDecimal.ZERO) == 1;
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
		public String getGtin() {
			return gtin;
		}

		@Override
		public void setGtin(String gtin) {
			this.gtin = gtin;
		}

		@Override
		public String getComponentCode() {
			return componentCode;
		}

		@Override
		public void setComponentCode(String componentCode) {
			this.componentCode = componentCode;
		}

		@Override
		public String getTag() {
			return tag;
		}

		@Override
		public void setTag(String tag) {
			this.tag = tag;
		}

		@Override
		public BigDecimal getMinQty() {
			return minQty;
		}

		@Override
		public void setMinQty(BigDecimal minQty) {
			this.minQty = minQty;
		}

		@Override
		public BigDecimal getMaxQty() {
			return maxQty;
		}

		@Override
		public void setMaxQty(BigDecimal maxQty) {
			this.maxQty = maxQty;
		}

		@Override
		public BigDecimal getDefaultQty() {
			return defaultQty != null ? defaultQty : minQty;
		}

		@Override
		public void setDefaultQty(BigDecimal defaultQty) {
			this.defaultQty = defaultQty;
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
		public Boolean isDefault() {
			return isDefault;
		}

		@Override
		public void setIsDefault(Boolean isDefault) {
			this.isDefault = isDefault;
		}

		@Override
		public List<Price> getPrices() {
			return prices;
		}

		@Override
		public void setPrices(List<Price> prices) {
			this.prices = prices;
		}

		@Override
		public void addPrice(Price price) {
			prices.add(price);
		}

		@Override
		public Map<String, Price> getCachedPrices() {
			return cachedPrices;
		}

		@Override
		public void setCachedPrices(Map<String, Price> cachedPrices) {
			this.cachedPrices = cachedPrices;
		}

	}
}
