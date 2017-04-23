package com.bakerbeach.market.catalog.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.LocaleUtils;

import com.bakerbeach.market.catalog.model.RawAssetGroupImpl;
import com.bakerbeach.market.catalog.model.RawAssetImpl;
import com.bakerbeach.market.catalog.model.RawAssetsImpl;
import com.bakerbeach.market.catalog.model.RawGroupTagImpl;
import com.bakerbeach.market.catalog.model.RawItem;
import com.bakerbeach.market.catalog.model.RawItemImpl;
import com.bakerbeach.market.catalog.model.RawOption;
import com.bakerbeach.market.catalog.model.RawOptionImpl;
import com.bakerbeach.market.catalog.model.RawProductImpl;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.ScaledPrice;
import com.bakerbeach.market.core.api.model.Status;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Type;
import com.mongodb.DBObject;

public class RawProductConverter {

	@SuppressWarnings("unchecked")
	public RawProductImpl decode(DBObject source) {
		RawProductImpl product = new RawProductImpl();
		
		product.setGtin((String) source.get("gtin"));

		if (source.containsField("type") && source.get("type") != null) {
			String type = (String) source.get("type");
			product.setType(Type.valueOf(type.toUpperCase()));
		}

		if (source.containsField("status") && source.get("status") != null) {
			product.setStatus(Status.valueOf((String) source.get("status")));
		}
		
		product.setBrand((String) source.get("brand"));

		product.setName((String) source.get("name"));

		if (source.containsField("primary_group")) {
			product.setPrimaryGroup(new RawGroupTagImpl((String) source.get("primary_group")));			
		}
		
		if (source.containsField("secondary_group")) {
			product.setSecondaryGroup(new RawGroupTagImpl((String) source.get("secondary_group")));			
		}
		
		product.setMainCategory((String) source.get("main_category"));

		List<String> categories = product.getCategories();
		if (source.containsField("categories")) {
			DBObject categoriesDbo = (DBObject) source.get("categories");
			categories.addAll((List<String>) categoriesDbo);
		}

		if (source.containsField("visibilities") && source.get("visibilities") != null) {
			DBObject dbo = (DBObject) source.get("visibilities");
			if (dbo instanceof Map<?,?>) {
				product.getVisibilities().putAll((Map<String, Boolean>) dbo);
			}
		}

		if (source.containsField("index")) {
			product.setIsIndex((Boolean) source.get("index")); 
		}

		if (source.containsField("tax_codes") && source.get("tax_codes") != null) {
			DBObject dbo = (DBObject) source.get("tax_codes");
			for (String key : dbo.keySet()) {
				TaxCode value = TaxCode.valueOf((String) dbo.get(key));
				product.getTaxCodes().put(key, value);
			}
		}

		if (source.containsField("std_prices") && source.get("std_prices") != null) {
			DBObject pricesDbo = (DBObject) source.get("std_prices");
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice price = new ScaledPrice();
				price.setGroup((String) priceDbo.get("group"));
				price.setStart(new GregorianCalendar(2010, 1, 1).getTime());
				price.setValue(new BigDecimal(priceDbo.get("value").toString()));
				price.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				
				product.getStdPrices().add(price);
			}
		}

		if (source.containsField("prices") && source.get("prices") != null) {
			DBObject pricesDbo = (DBObject) source.get("prices");
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice price = new ScaledPrice();
				price.setGroup((String) priceDbo.get("group"));
				price.setStart((Date) priceDbo.get("start"));
				price.setValue(new BigDecimal(priceDbo.get("value").toString()));
				price.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				
				product.getPrices().add(price);
			}
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
			RawAssetsImpl assets = decodeRawAssets((DBObject) source.get("assets"));
			product.setAssets(assets);
		}

		product.setSize((String) source.get("size"));
		
		product.setColor((String) source.get("color"));
		
		if (source.containsField("colorpicker")) {
			if (source.get("colorpicker") instanceof List) {
				product.put("colorpicker", (List<String>) source.get("colorpicker"));
			} else if (source.get("colorpicker") instanceof String) {				
				product.put("colorpicker", (String) source.get("colorpicker"));
			}
		}
		
		if (source.containsField("sort") && source.get("sort") != null) {
			product.setSort((String) source.get("sort"));
		}
		
		if (source.containsField("net_weight") && source.get("net_weight") != null) {
			product.setNetWeight(new BigDecimal(source.get("net_weight").toString()));			
		}

		if (source.containsField("gross_weight") && source.get("gross_weight") != null) {
			product.setGrossWeight(new BigDecimal(source.get("gross_weight").toString()));			
		}
		
		if (source.containsField("items") && source.get("items") != null) {
			DBObject itemsDbo = (DBObject) source.get("items");
			for (DBObject itemDbo : ((List<DBObject>) itemsDbo)) {
				RawItem item = decodeRawItem(itemDbo);
				product.getItems().add(item);
			}
		}
		
		return product;
	}

	@SuppressWarnings("unchecked")
	private RawItem decodeRawItem(DBObject source) {
		RawItemImpl item = new RawItemImpl();

		item.setName((String) source.get("name"));
		item.setParent((String) source.get("parent"));
		item.setMinQty((Integer) source.get("min_qty"));
		item.setMaxQty((Integer) source.get("max_qty"));
		item.setIsRequired((Boolean) source.get("required"));
		item.setIsMultiselect((Boolean) source.get("multiselect"));

		if (source.containsField("options")) {
			DBObject optionsDbo = (DBObject) source.get("options");
			for (DBObject optionDbo : ((List<DBObject>) optionsDbo)) {
				RawOption option = decodeRawOption(optionDbo);
				item.getOptions().add(option);
			}
		}

		return item;
	}

	@SuppressWarnings("unchecked")
	private RawOption decodeRawOption(DBObject source) {
		RawOptionImpl option = new RawOptionImpl();

		option.setGtin((String) source.get("gtin"));
		option.setMinQty((Integer) source.get("min_qty"));
		option.setMaxQty((Integer) source.get("max_qty"));
		option.setDefaultQty((Integer) source.get("default_qty"));
		option.setIsDefault((Boolean) source.get("default"));
		option.setIsRequired((Boolean) source.get("required"));

		if (source.containsField("prices") && source.get("prices") != null) {
			DBObject pricesDbo = (DBObject) source.get("prices");
			for (DBObject priceDbo : ((List<DBObject>) pricesDbo)) {
				ScaledPrice price = new ScaledPrice();
				price.setGroup((String) priceDbo.get("group"));
				price.setStart((Date) priceDbo.get("start"));
				price.setValue(new BigDecimal(priceDbo.get("value").toString()));
				price.setCurrency(Currency.getInstance((String) priceDbo.get("currency")));
				
				option.getPrices().add(price);
			}
		}

		return option;	
	}

	@SuppressWarnings("unchecked")
	public RawAssetsImpl decodeRawAssets(DBObject assetsDbo) {
		RawAssetsImpl assets = new RawAssetsImpl();
		
		for (String tag : assetsDbo.keySet()) {
			List<DBObject> assetGroupsDbo = (List<DBObject>) assetsDbo.get(tag);
			List<AssetGroup> assetGroups = new ArrayList<AssetGroup>();
			for (DBObject assetGroupDbo : assetGroupsDbo) {
				RawAssetGroupImpl assetGroup = decodeRawAssetGroup(assetGroupDbo);
				assetGroups.add(assetGroup);
			}
			assets.put(tag, assetGroups);
		}

		return assets;
	}
	
	public RawAssetGroupImpl decodeRawAssetGroup(DBObject assetGroupDbo) {
		RawAssetGroupImpl assetGroup = new RawAssetGroupImpl();
		
		for (String tag : assetGroupDbo.keySet()) {
			DBObject assetDbo = (DBObject) assetGroupDbo.get(tag);
			RawAssetImpl asset = decodeAsset(assetDbo);
			assetGroup.put(tag, asset);
		}
		
		return assetGroup;
	}

	public RawAssetImpl decodeAsset(DBObject assetDbo) {
		RawAssetImpl asset = new RawAssetImpl();
		
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
	
	public RawGroupTagImpl decodeRawGroupTag(DBObject source) {
		RawGroupTagImpl group = new RawGroupTagImpl((String) source.get("code"));
		
		group.setBrand((String) source.get("brand"));
		
		group.setDim1((String) source.get("dim1"));
		
		group.setDim2((String) source.get("dim2"));
		
		if (source.containsField("assets") && source.get("asstes") != null) {
			RawAssetsImpl assets = decodeRawAssets((DBObject) source.get("assets"));
			group.setAssets(assets);
		}

		return group;
	}

}
