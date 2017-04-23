package com.bakerbeach.market.catalog.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.LocaleUtils;

import com.bakerbeach.market.catalog.model.AbstractProduct;
import com.bakerbeach.market.catalog.model.AssetGroupImpl;
import com.bakerbeach.market.catalog.model.AssetImpl;
import com.bakerbeach.market.catalog.model.AssetsImpl;
import com.bakerbeach.market.catalog.model.BundleComponentImpl;
import com.bakerbeach.market.catalog.model.BundleOptionImpl;
import com.bakerbeach.market.catalog.model.BundleProductImpl;
import com.bakerbeach.market.catalog.model.RawProduct;
import com.bakerbeach.market.catalog.model.SimpleProductImpl;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.BundleComponent;
import com.bakerbeach.market.core.api.model.BundleOption;
import com.bakerbeach.market.core.api.model.BundleProduct;
import com.bakerbeach.market.core.api.model.Product;
import com.bakerbeach.market.core.api.model.ScaledPrice;
import com.bakerbeach.market.core.api.model.Status;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ProductConverter {

	public DBObject encode(RawProduct source) {
		BasicDBObject product = new BasicDBObject();

		product.put("gtin", source.getGtin());
		product.put("type", "simple");
		product.put("status", "PUBLISHED");
		product.put("index", true);
		product.put("visible", true);

		product.put("primary_group", source.getPrimaryGroup().getCode());
		product.put("secondary_group", source.getSecondaryGroup().getCode());

		for (Entry<String, Object> e : source.entrySet()) {
			product.put(e.getKey(), e.getValue());
		}

		BasicDBList categories = new BasicDBList();
		categories.addAll(source.getCategories());
		product.put("categories", categories);

		product.put("tags", source.getTags());
		product.put("logos", source.getLogos());

		product.put("size", source.getSize());
		product.put("color", source.getColor());

		BasicDBList stdPrices = new BasicDBList();
		source.getStdPrices().forEach(p -> {
			BasicDBObject price = new BasicDBObject();
			price.put("group", p.getGroup());
			price.put("start", p.getStart());
			price.put("currency", p.getCurrency().getCurrencyCode());
			price.put("value", p.getValue().doubleValue());
			stdPrices.add(price);
		});
		product.put("std_prices", stdPrices);

		BasicDBList prices = new BasicDBList();
		source.getStdPrices().forEach(p -> {
			BasicDBObject price = new BasicDBObject();
			price.put("group", p.getGroup());
			price.put("start", p.getStart());
			price.put("currency", p.getCurrency().getCurrencyCode());
			price.put("value", p.getValue().doubleValue());
			prices.add(price);
		});

		product.put("prices", prices);

		return product;
	}

	public Product decode(Locale locale, String priceGroup, String defaultPriceGroup, Currency currency,
			String countryOfDelivery, String defaultCountryOfDelivery, Date date, DBObject source) {
		AbstractProduct product;
		if (source.containsField("items")) {
			product = new BundleProductImpl();
		} else {
			product = new SimpleProductImpl();
		}

		product.setGtin((String) source.get("gtin"));
		if (source.containsField("status") && source.get("status") != null) {
			product.setStatus(Status.valueOf((String) source.get("status")));
		}
		product.setBrand((String) source.get("brand"));

		product.setName((String) source.get("name"));

		if (source.containsField("primary_group")) {
			product.setPrimaryGroup((String) source.get("primary_group"));
		}

		if (source.containsField("secondary_group")) {
			product.setSecondaryGroup((String) source.get("secondary_group"));
		}

		product.setMainCategory((String) source.get("main_category"));

		List<String> categories = product.getCategories();
		if (source.containsField("categories")) {
			DBObject categoriesDbo = (DBObject) source.get("categories");
			if (categoriesDbo instanceof List<?>) {
				for (String category : ((List<String>) categoriesDbo)) {
					categories.add(category);
				}
			}
		}

		// visibility by customer price group ---
		if (source.containsField("visibilities") && source.get("visibilities") != null) {
			DBObject dbo = (DBObject) source.get("visibilities");
			if (dbo.containsField(priceGroup)) {
				product.setVisible((Boolean) dbo.get(priceGroup));
			} else if (dbo.containsField(defaultPriceGroup)) {
				product.setVisible((Boolean) dbo.get(defaultPriceGroup));
			} else {
				product.setVisible(false);
			}
		}

		// per country (of delivery) ---
		if (source.containsField("tax_codes") && source.get("tax_codes") != null) {
			DBObject dbo = (DBObject) source.get("tax_codes");
			if (dbo.containsField(countryOfDelivery)) {
				TaxCode taxCode = TaxCode.valueOf((String) dbo.get(countryOfDelivery));
				product.setTaxCode(taxCode);
			} else {
				TaxCode taxCode = TaxCode.valueOf((String) dbo.get(defaultCountryOfDelivery));
				product.setTaxCode(taxCode);
			}
		}

		if (source.containsField("std_prices")) {

			DBObject pricesDbo = (DBObject) source.get("std_prices");
			List<ScaledPrice> scaledPrices = new ArrayList<ScaledPrice>();
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice scaledPrice = new ScaledPrice();
				scaledPrice.setGroup((String) priceDbo.get("group"));
				scaledPrice.setStart(new GregorianCalendar(2010, 1, 1).getTime());
				scaledPrice.setValue(new BigDecimal(priceDbo.get("value").toString()));
				scaledPrice.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				scaledPrices.add(scaledPrice);
			}

			BigDecimal stdPrice = getStdPrice(scaledPrices, currency, priceGroup, date);
			product.setStdPrice(stdPrice);
		}

		if (source.containsField("prices")) {
			Set<String> currencies = new TreeSet<String>();
			DBObject pricesDbo = (DBObject) source.get("prices");
			List<ScaledPrice> prices = new ArrayList<ScaledPrice>();
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice scaledPrice = new ScaledPrice();
				scaledPrice.setGroup((String) priceDbo.get("group"));
				scaledPrice.setStart((Date) priceDbo.get("start"));
				scaledPrice.setValue(new BigDecimal(priceDbo.get("value").toString()));
				if (priceDbo.containsField("monthly")) {
					scaledPrice.setMonthlyValue(new BigDecimal(priceDbo.get("monthly").toString()));
				}
				scaledPrice.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				currencies.add((String) priceDbo.get("currency"));
				prices.add(scaledPrice);
			}

			ScaledPrice price = getPrice(prices, currency, priceGroup, date);
			product.setPrice(price.getValue());
			product.setMonthlyPrice(price.getMonthlyValue());

			for (String cStr : currencies) {
				BigDecimal cPrice = getStdPrice(prices, Currency.getInstance(cStr), priceGroup, date);
				product.getPrices().put(cStr, cPrice);
			}
		}

		product.setMaterialText((String) source.get("material_text"));

		if (source.containsField("material_codes") && source.get("material_codes") != null) {
			product.setMaterialCodes((List<String>) source.get("material_codes"));
		}

		product.setCareText((String) source.get("care_text"));

		if (source.containsField("care_codes") && source.get("care_codes") != null) {
			product.setCareCodes((List<String>) source.get("care_codes"));
		}

		product.setSize((String) source.get("size"));

		product.setColor((String) source.get("color"));

		product.setDiet((String) source.get("diet"));

		if (source.containsField("net_weight")) {
			product.setNetWeight(new BigDecimal(source.get("net_weight").toString()));
		}

		if (source.containsField("gross_weight")) {
			product.setGrossWeight(new BigDecimal(source.get("gross_weight").toString()));
		}

		if (source.containsField("logos") && source.get("logos") != null) {
			DBObject logosDbo = (DBObject) source.get("logos");
			for (String key : logosDbo.keySet()) {
				List<String> values = (List<String>) logosDbo.get(key);
				product.getLogos().put(key, values);
			}
		}

		if (source.containsField("tags") && source.get("tags") != null) {
			DBObject tagsDbo = (DBObject) source.get("tags");
			for (String key : tagsDbo.keySet()) {
				List<String> values = (List<String>) tagsDbo.get(key);
				product.getTags().put(key, values);
			}
		}

		if (source.containsField("assets") && source.get("assets") != null) {
			AssetsImpl assets = decodeAssets((DBObject) source.get("assets"));
			product.setAssets(assets);
		}

		if (source.containsField("start_date")) {
			product.setStartDate((Date) source.get("start_date"));
		}

		if (source.containsField("sort")) {
			product.setSort((String) source.get("sort"));
		}

		if (product instanceof BundleProduct) {
			DBObject itemsDbo = (DBObject) source.get("items");
			for (DBObject itemDbo : ((List<DBObject>) itemsDbo)) {
				BundleComponent item = decodeBundleItem(currency, priceGroup, date, itemDbo);
				((BundleProduct) product).getComponents().add(item);
			}
		}

		return product;
	}

	@SuppressWarnings("unchecked")
	public BundleComponent decodeBundleItem(Currency currency, String priceGroup, Date date, DBObject source) {
		BundleComponentImpl item = new BundleComponentImpl();

		item.setName((String) source.get("name"));
		item.setParent((String) source.get("parent"));
		item.setMinQty((Integer) source.get("min_qty"));
		item.setMaxQty((Integer) source.get("max_qty"));
		item.setIsRequired((Boolean) source.get("required"));
		item.setIsMultiselect((Boolean) source.get("multiselect"));

		if (source.containsField("options")) {
			DBObject optionsDbo = (DBObject) source.get("options");
			for (DBObject optionDbo : ((List<DBObject>) optionsDbo)) {
				BundleOption option = decodeBundleOption(currency, priceGroup, date, optionDbo);
				item.getOptions().add(option);
			}
		}

		if (source.containsField("attributes")) {
			DBObject dbo = (DBObject) source.get("attributes");
			item.getAttributes().putAll(dbo.toMap());
		}

		return item;
	}

	@SuppressWarnings("unchecked")
	public BundleOption decodeBundleOption(Currency currency, String priceGroup, Date date, DBObject source) {
		BundleOptionImpl option = new BundleOptionImpl();

		option.setGtin((String) source.get("gtin"));
		option.setMinQty((Integer) source.get("min_qty"));
		option.setMaxQty((Integer) source.get("max_qty"));
		option.setDefaultQty((Integer) source.get("default_qty"));
		option.setIsDefault((Boolean) source.get("default"));
		option.setIsRequired((Boolean) source.get("required"));

		if (source.containsField("prices")) {
			Set<String> currencies = new TreeSet<String>();
			DBObject pricesDbo = (DBObject) source.get("prices");
			List<ScaledPrice> prices = new ArrayList<ScaledPrice>();
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice scaledPrice = new ScaledPrice();
				scaledPrice.setGroup((String) priceDbo.get("group"));
				scaledPrice.setStart((Date) priceDbo.get("start"));
				scaledPrice.setValue(new BigDecimal(priceDbo.get("value").toString()));
				if (priceDbo.containsField("monthly")) {
					scaledPrice.setMonthlyValue(new BigDecimal(priceDbo.get("monthly").toString()));
				}
				scaledPrice.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				currencies.add((String) priceDbo.get("currency"));
				prices.add(scaledPrice);
			}

			ScaledPrice price = getPrice(prices, currency, priceGroup, date);
			option.setPrice(price.getValue());
			option.setMonthlyPrice(price.getMonthlyValue());

			for (String cStr : currencies) {
				BigDecimal cPrice = getStdPrice(prices, Currency.getInstance(cStr), priceGroup, date);
				option.getPrices().put(cStr, cPrice);
			}
		}

		if (source.containsField("attributes")) {
			DBObject dbo = (DBObject) source.get("attributes");
			option.getAttributes().putAll(dbo.toMap());
		}

		return option;
	}

	public BigDecimal getStdPrice(List<ScaledPrice> prices, Currency currency, String group, Date date) {
		ScaledPrice price = null;
		ScaledPrice defaultPrice = null;

		for (ScaledPrice p : prices) {
			if (currency.equals(p.getCurrency())) {
				if (group.equals(p.getGroup())) {
					price = p;
				} else if ("default".equals(p.getGroup())) {
					defaultPrice = p;
				}
			}
		}

		return (price != null) ? price.getValue() : defaultPrice.getValue();
	}

	public ScaledPrice getPrice(List<ScaledPrice> prices, Currency currency, String group, Date date) {
		ScaledPrice price = null;
		ScaledPrice defaultPrice = null;

		for (ScaledPrice p : prices) {
			Date start = p.getStart();
			if (!start.after(date)) {
				if (currency.equals(p.getCurrency())) {
					if (group.equals(p.getGroup())) {
						if (price == null) {
							price = p;
						} else if (price.getStart().before(p.getStart())) {
							price = p;
						}
					} else if ("default".equals(p.getGroup())) {
						if (defaultPrice == null) {
							defaultPrice = p;
						} else if (defaultPrice.getStart().before(p.getStart())) {
							defaultPrice = p;
						}
					}
				}
			}
		}

		return (price != null) ? price : (defaultPrice != null) ? defaultPrice : null;
	}

	@SuppressWarnings("unchecked")
	public AssetsImpl decodeAssets(DBObject assetsDbo) {
		AssetsImpl assets = new AssetsImpl();

		for (String tag : assetsDbo.keySet()) {
			List<DBObject> assetGroupsDbo = (List<DBObject>) assetsDbo.get(tag);
			List<AssetGroup> assetGroups = new ArrayList<AssetGroup>();
			for (DBObject assetGroupDbo : assetGroupsDbo) {
				AssetGroup assetGroup = decodeAssetGroup(assetGroupDbo);
				assetGroups.add(assetGroup);
			}
			assets.put(tag, assetGroups);
		}

		return assets;
	}

	public AssetGroupImpl decodeAssetGroup(DBObject assetGroupDbo) {
		AssetGroupImpl assetGroup = new AssetGroupImpl();

		for (String tag : assetGroupDbo.keySet()) {
			DBObject assetDbo = (DBObject) assetGroupDbo.get(tag);
			AssetImpl asset = decodeAsset(assetDbo);
			assetGroup.put(tag, asset);
		}

		return assetGroup;
	}

	public AssetImpl decodeAsset(DBObject assetDbo) {
		AssetImpl asset = new AssetImpl();

		asset.setType((String) assetDbo.get("type"));

		asset.setPath((String) assetDbo.get("path"));

		if (assetDbo.containsField("alt")) {
			DBObject altDbo = (DBObject) assetDbo.get("alt");
			Map<Locale, String> alt = asset.getAlt();
			for (String key : altDbo.keySet()) {
				alt.put(LocaleUtils.toLocale(key), (String) altDbo.get(key));
			}
		}

		return asset;
	}

}
