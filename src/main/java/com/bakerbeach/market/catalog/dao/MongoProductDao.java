package com.bakerbeach.market.catalog.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.bakerbeach.market.catalog.model.AssetsImpl;
import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.catalog.model.GroupedProductImpl;
import com.bakerbeach.market.catalog.model.RawGroupTag;
import com.bakerbeach.market.catalog.model.RawProduct;
import com.bakerbeach.market.catalog.model.RawProductImpl;
import com.bakerbeach.market.core.api.model.Product;
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
	protected ProductConverter productConverter = new ProductConverter();

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

	private List<RawProduct> findRaw(QueryBuilder qb, String status, DBObject keys, DBObject orderBy, Integer limit,
			Integer offset) {
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
		 * Map<String, RawGroupTag> groups = findGroup(status, groupCodes);
		 * 
		 * for (RawProduct product : products) { RawGroupTag primaryGroup =
		 * product.getPrimaryGroup(); if (primaryGroup != null) { RawGroupTag
		 * group = groups.get(primaryGroup.getCode()); if (group != null) {
		 * ((RawProductImpl) product).setPrimaryGroup(group); } }
		 * 
		 * RawGroupTag secondaryGroup = product.getSecondaryGroup(); if
		 * (secondaryGroup != null) { RawGroupTag group =
		 * groups.get(secondaryGroup.getCode()); if (group != null) {
		 * ((RawProductImpl) product).setSecondaryGroup(group); } } }
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
			// Product product = decodeProduct(locale, priceGroup, currency,
			// countryOfDelivery, date, source);
			Product product = productConverter.decode(locale, priceGroup, defaultPriceGroup, currency,
					countryOfDelivery, defaultCountryOfDelivery, date, source);

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
//			AssetsImpl assets = decodeAssets((DBObject) source.get("assets"));
			AssetsImpl assets = productConverter.decodeAssets((DBObject) source.get("assets"));
			group.setAssets(assets);
		}

		return group;
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

	public void setProductConverter(ProductConverter productConverter) {
		this.productConverter = productConverter;
	}

}
