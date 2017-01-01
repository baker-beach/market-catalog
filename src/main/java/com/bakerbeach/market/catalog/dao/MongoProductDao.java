package com.bakerbeach.market.catalog.dao;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.bakerbeach.market.catalog.model.AbstractProduct;
import com.bakerbeach.market.catalog.model.AssetGroupImpl;
import com.bakerbeach.market.catalog.model.AssetImpl;
import com.bakerbeach.market.catalog.model.AssetsImpl;
import com.bakerbeach.market.catalog.model.BundleComponentImpl;
import com.bakerbeach.market.catalog.model.BundleOptionImpl;
import com.bakerbeach.market.catalog.model.BundleProductImpl;
import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.catalog.model.GroupedProductImpl;
import com.bakerbeach.market.catalog.model.RawGroupTag;
import com.bakerbeach.market.catalog.model.RawProduct;
import com.bakerbeach.market.catalog.model.RawProductImpl;
import com.bakerbeach.market.catalog.model.SimpleProductImpl;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.BundleComponent;
import com.bakerbeach.market.core.api.model.BundleOption;
import com.bakerbeach.market.core.api.model.BundleProduct;
import com.bakerbeach.market.core.api.model.Product;
import com.bakerbeach.market.core.api.model.ScaledPrice;
import com.bakerbeach.market.core.api.model.Status;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class MongoProductDao implements ProductDao {
	protected static final Logger log = LoggerFactory.getLogger(MongoProductDao.class);
	
	private static final String DEFAULT_PRODUCT_COLLECTION = "product_published";
	private static final String DEFAULT_GROUP_COLLECTION = "group_published";
	
	protected MongoTemplate mongoTemplate;
	
	protected String productCollection = DEFAULT_PRODUCT_COLLECTION;
	protected String groupCollection = DEFAULT_GROUP_COLLECTION;;
	protected String defaultCountryOfDelivery;
	protected String defaultPriceGroup;
	
	protected RawProductConverter rawProductConverter;
	
	@Override
	public List<RawProduct> findRawByGtin(Collection<String> gtin, String status) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("gtin").in(gtin);
		List<RawProduct> products = findRaw(qb, status, null, null, null, null);

		return products;
	}

	@Override
	public List<RawProduct> findRawByField(String field, List<String> values, String status) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and(field).in(values);
		List<RawProduct> products = findRaw(qb, status, null, null, null, null);
		
		return products;
	}

	@Override
	public List<String> findGtinByStatusAndIndex(String status, Boolean index) {
		QueryBuilder qb = QueryBuilder.start();
		if (index != null) {
			qb.and("index").is(index);			
		}
		DBCursor cursor = getProductCollection().find(qb.get(), new BasicDBObject("gtin", 1));
		
		List<String> list = new ArrayList<String>(cursor.size());
		for (Iterator<DBObject> it = cursor.iterator(); it.hasNext();) {
			String gtin = (String) it.next().get("gtin");
			list.add(gtin);
		}
		
		return list;
	}

	@Override
	public List<GroupedProduct> findGroupByGroupCode(Locale locale, String priceGroup, Currency currency,
			Collection<String> groupCode, String countryOfDelivery, Date date, String groupBy) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(groupBy), "_").toLowerCase()).in(groupCode);
		qb.and("type").in(Arrays.asList("bundle", "simple"));
		
		List<Product> products = find(locale, priceGroup, currency, countryOfDelivery, date, qb, null,
				new BasicDBObject("sort", 1), null, null);
		List<GroupedProduct> groupedProducts = getProductGroups(products, groupBy);
		
		return groupedProducts;
	}	
	
	@Override
	public List<Product> findByGtin(Locale locale, String priceGroup, Currency currency, String countryOfDelivery,
			Date date, Collection<String> gtins) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("gtin").in(gtins);

		List<Product> products = find(locale, priceGroup, currency, countryOfDelivery, date, qb, null, null, null,
				null);

		return products;
	}

//	@Override
//	public Collection<GroupedProduct> groupQueryByGtin(Locale locale, String priceGroup, Currency currency,
//			String countryOfDelivery, Date date, Collection<String> gtins) {
//		QueryBuilder qb = QueryBuilder.start();
//		qb.and("gtin").in(gtins);
//
//		List<Product> products = find(locale, priceGroup, currency, countryOfDelivery, date, qb, null, null, null,
//				null);
//
//		String groupAttribute = "primary_group";
//		Collection<GroupedProduct> groupedProducts = getProductGroups(products, groupAttribute);
//
//		return groupedProducts;
//	}

	private List<GroupedProduct> getProductGroups(List<Product> products, String groupBy) {
		Set<String> groupCodes = new HashSet<String>();
		for (Product product : products) {
			try {
				String groupValue = BeanUtils.getSimpleProperty(product, groupBy);
				if (groupValue != null && !groupValue.isEmpty()) {
					groupCodes.add(groupValue);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}

		Map<String, GroupedProduct> groups = findGroup(groupCodes);

		Map<String, GroupedProduct> map = new TreeMap<String, GroupedProduct>();
		for (Product product : products) {
			try {
				String groupValue = BeanUtils.getSimpleProperty(product, groupBy);
				if (!map.containsKey(groupValue)) {
					if (groups.containsKey(groupValue)) {
						GroupedProduct group = groups.get(groupValue);
						group.getMembers().add(product);

						map.put(groupValue, group);
					} else {
						GroupedProductImpl groupedProduct = new GroupedProductImpl(groupValue);
						groupedProduct.getMembers().add(product);

						map.put(groupValue, groupedProduct);
					}
				} else {
					map.get(groupValue).getMembers().add(product);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}

		return new ArrayList<GroupedProduct>(map.values());
	}

	private List<RawProduct> findRaw(QueryBuilder qb, String status, DBObject keys, DBObject orderBy, Integer limit, Integer offset) {
		DBCursor cur = null;
		if (orderBy != null)
			cur = getProductCollection().find(qb.get(), keys).sort(orderBy);
		else
			cur = getProductCollection().find(qb.get(), keys);

		if (limit != null) {
			cur.limit(limit);
		}
		if (offset != null) {
			cur.skip(offset);
		}

		List<RawProduct> products = new ArrayList<RawProduct>();
		for (Iterator<DBObject> it = cur.iterator(); it.hasNext();) {
			DBObject source = it.next();
			
			RawProductImpl product = rawProductConverter.decode(source);
			products.add(product);
		}

		Set<String> groupCodes = new HashSet<String>();
		for (RawProduct product : products) {
			RawGroupTag primaryGroup = product.getPrimaryGroup();
			if (primaryGroup != null) {
				groupCodes.add(primaryGroup.getCode());
			}
			
			RawGroupTag secondaryGroup = product.getSecondaryGroup();
			if (secondaryGroup != null) {
				groupCodes.add(secondaryGroup.getCode());
			}			
		}
		
		/*
		Map<String, RawGroupTag> groups = findGroup(status, groupCodes);
		
		for (RawProduct product : products) {
			RawGroupTag primaryGroup = product.getPrimaryGroup();
			if (primaryGroup != null) {
				RawGroupTag group = groups.get(primaryGroup.getCode());
				if (group != null) {
					((RawProductImpl) product).setPrimaryGroup(group);
				}
			}
			
			RawGroupTag secondaryGroup = product.getSecondaryGroup();
			if (secondaryGroup != null) {
				RawGroupTag group = groups.get(secondaryGroup.getCode());
				if (group != null) {
					((RawProductImpl) product).setSecondaryGroup(group);
				}
			}
		}
		*/

		return products;
	}

	private List<Product> find(Locale locale, String priceGroup, Currency currency, String countryOfDelivery, Date date,
			QueryBuilder qb, DBObject keys, DBObject orderBy, Integer limit, Integer offset) {
		DBCursor cur = null;
		if (orderBy != null)
			cur = getProductCollection().find(qb.get(), keys).sort(orderBy);
		else
			cur = getProductCollection().find(qb.get(), keys);

		if (limit != null) {
			cur.limit(limit);
		}
		if (offset != null) {
			cur.skip(offset);
		}

		List<Product> products = new ArrayList<Product>();
		for (Iterator<DBObject> it = cur.iterator(); it.hasNext();) {
			DBObject source = it.next();
			Product product = decodeProduct(locale, priceGroup, currency, countryOfDelivery, date, source);
			products.add(product);
		}

		return products;
	}

	private Map<String, GroupedProduct> findGroup(Collection<String> code) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").in(code);
		DBCursor cursor = getGroupCollection().find(qb.get());

		Map<String, GroupedProduct> map = new HashMap<String, GroupedProduct>(cursor.size());
		for (Iterator<DBObject> it = cursor.iterator(); it.hasNext();) {
			GroupedProduct group = decodeProductGroup(it.next());
			map.put(group.getCode(), group);
		}

		return map;
	}

	private GroupedProduct decodeProductGroup(DBObject source) {
		GroupedProductImpl group = new GroupedProductImpl((String) source.get("code"));
		group.setDim1((String) source.get("dim1"));
		group.setDim2((String) source.get("dim2"));
		group.setTemplate((String) source.get("template"));

		if (source.containsField("assets") && source.get("asstes") != null) {
			AssetsImpl assets = decodeAssets((DBObject) source.get("assets"));
			group.setAssets(assets);
		}

		return group;
	}

	@SuppressWarnings("unchecked")
	private Product decodeProduct(Locale locale, String priceGroup, Currency currency, String countryOfDelivery,
			Date date, DBObject source) {
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
	
	private BigDecimal getStdPrice(List<ScaledPrice> prices, Currency currency, String group, Date date) {
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

	private ScaledPrice getPrice(List<ScaledPrice> prices, Currency currency, String group, Date date) {
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
	/*
	private BigDecimal getPrice(List<ScaledPrice> prices, Currency currency, String group, Date date) {
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
		
		return (price != null) ? price.getValue() : (defaultPrice != null) ? defaultPrice.getValue() : null;
	}
	*/

	@SuppressWarnings("unchecked")
	private BundleComponent decodeBundleItem(Currency currency, String priceGroup, Date date, DBObject source) {
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

		return item;
	}

	@SuppressWarnings("unchecked")
	private BundleOption decodeBundleOption(Currency currency, String priceGroup, Date date, DBObject source) {
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

		return option;
	}

	@SuppressWarnings("unchecked")
	private AssetsImpl decodeAssets(DBObject assetsDbo) {
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

	private AssetGroupImpl decodeAssetGroup(DBObject assetGroupDbo) {
		AssetGroupImpl assetGroup = new AssetGroupImpl();

		for (String tag : assetGroupDbo.keySet()) {
			DBObject assetDbo = (DBObject) assetGroupDbo.get(tag);
			AssetImpl asset = decodeAsset(assetDbo);
			assetGroup.put(tag, asset);
		}

		return assetGroup;
	}

	private AssetImpl decodeAsset(DBObject assetDbo) {
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

	protected DBCollection getProductCollection() {
		return mongoTemplate.getCollection(productCollection);
	}

	protected DBCollection getGroupCollection() {
		return mongoTemplate.getCollection(groupCollection);
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void setProductCollection(String productCollection) {
		this.productCollection = productCollection;
	}

	public void setGroupCollection(String groupCollection) {
		this.groupCollection = groupCollection;
	}

	public void setDefaultCountryOfDelivery(String defaultCountryOfDelivery) {
		this.defaultCountryOfDelivery = defaultCountryOfDelivery;
	}

	public void setDefaultPriceGroup(String defaultPriceGroup) {
		this.defaultPriceGroup = defaultPriceGroup;
	}

	public void setRawProductConverter(RawProductConverter rawProductConverter) {
		this.rawProductConverter = rawProductConverter;
	}
	
}
