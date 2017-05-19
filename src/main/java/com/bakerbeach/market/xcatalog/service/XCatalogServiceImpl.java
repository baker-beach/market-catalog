package com.bakerbeach.market.xcatalog.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.xcatalog.dao.AbstractSolrProductDao.GroupResult;
import com.bakerbeach.market.xcatalog.dao.MongoProductDao;
import com.bakerbeach.market.xcatalog.dao.SolrProductDao;
import com.bakerbeach.market.xcatalog.model.Facets;
import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Pager;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.SearchResult;

public class XCatalogServiceImpl implements XCatalogService {

	protected static final Logger log = LoggerFactory.getLogger(XCatalogServiceImpl.class);

	protected Map<String, MongoProductDao> mongoProductDaos;
	protected Map<String, SolrProductDao> solrProductDaos;

	@Override
	public List<String> productCodes(String shopCode, Collection<Product.Type> types,
			Collection<Product.Status> status, Integer pageSize, Integer currentPage, String sort) {
		
		Integer offset = (pageSize != null && currentPage != null)? Math.abs(currentPage * pageSize) : 0;

		List<String> list = mongoProductDaos.get(shopCode).productCodes(shopCode, types, status, sort, offset, pageSize);
		return list;
	}

	@Override
	public List<Product> rawByGtin(String shopCode, Product.Status status, Collection<String> codes) {
		List<Product> list = mongoProductDaos.get(shopCode).byCode(shopCode, status,
				Arrays.asList(Product.Type.PRODUCT), codes);
		return list;
	}

	@Override
	public Group groupByCode(String shopCode, Product.Status status, Locale locale, String priceGroup,
			Currency currency, String countryOfDelivery, Date date, String groupBy, String code)
			throws XCatalogServiceException {
		try {
			if (StringUtils.isBlank(groupBy)) {
				groupBy = "primaryGroup";
			}

			Group group;
			List<Group> groups = mongoProductDaos.get(shopCode).groupByCode(shopCode, status, Arrays.asList(code));
			if (CollectionUtils.isNotEmpty(groups)) {
				group = groups.get(0);
			} else {
				group = mongoProductDaos.get(shopCode).newInstance(code, shopCode);
				group.setCode(code);
			}

			List<Product> products = mongoProductDaos.get(shopCode).productByGroupCode(shopCode, status, groupBy,
					Arrays.asList(code));
			group.getMembers().addAll(products);

			// refine(products, date, currency, priceGroup);
			// products.forEach(p -> p.refine(currency, priceGroup, date));

			// group.refine(date, currency, priceGroup);

			return group;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new XCatalogServiceException(e);
		}
	}

	@Override
	public SearchResult groupByIndexQuery(String shopCode, Product.Status status, Locale locale, String priceGroup,
			Currency currency, String countryOfDelivery, Date date, Facets facets, String query,
			List<String> filterQueries, String groupBy, Pager pager, String sort) {

		if (StringUtils.isBlank(query)) {
			query = "*:*";
		}

		// specifyPriceFilter(filterList, currency.getCurrencyCode(),
		// priceGroup);

		GroupResult groupResult = solrProductDaos.get(shopCode).groupQuery(locale, priceGroup, currency, facets, query,
				filterQueries, groupBy, pager.getPageSize(), pager.getCurrentPage(), sort);

		SearchResult result = new SearchResult();
		result.getGroups().addAll(groupResult.getGroups());
		result.setPager(new Pager(pager.getPageSize(), pager.getCurrentPage(), groupResult.getNGroups()));
		result.setFacets(facets);

		return result;
	}

	@Override
	public void save(String shopCode, Product product) {
		mongoProductDaos.get(shopCode).save(product);
	}

	@Override
	public void groupSave(String shopCode, Group group) {
		mongoProductDaos.get(shopCode).groupSave(group);
	}

	/*
	@Override
	public SearchResult groupByCode(String shopCode, Product.Status status, Locale locale, String priceGroup,
			Currency currency, String countryOfDelivery, Date date, String groupBy, List<String> codes)
			throws XCatalogServiceException {
		try {
			if (StringUtils.isBlank(groupBy)) {
				groupBy = "primaryGroup";
			}

			Map<String, Group> groups = new HashMap<>();

			List<Product> products = mongoProductDaos.get(shopCode).productByGroupCode(shopCode, status, groupBy,
					codes);
			for (Product product : products) {
				try {
					String key = BeanUtils.getProperty(product, groupBy);

					if (!groups.containsKey(key)) {
						groups.put(key, mongoProductDaos.get(shopCode).newInstance(key, shopCode));
					}
					groups.get(key).getMembers().add(product);

				} catch (InvocationTargetException | NoSuchMethodException e) {
					log.error(ExceptionUtils.getStackTrace(e));
				}
			}

			SearchResult result = new SearchResult();

			return result;
		} catch (Exception e) {
			throw new XCatalogServiceException(e);
		}
	}
	*/
	
//	@Override
//	public Price getPrice(List<Price> prices, Currency currency, String priceGroup, Date date) {
//		Price price = null;
//		Price defaultPrice = null;
//
//		for (Price p : prices) {
//			Date start = p.getStart();
//			if (!start.after(date)) {
//				if (currency.getCurrencyCode().equals(p.getCurrency())) {
//					if (priceGroup.equals(p.getGroup())) {
//						if (price == null) {
//							price = p;
//						} else if (price.getStart().before(p.getStart())) {
//							price = p;
//						}
//					} else if ("default".equalsIgnoreCase(p.getGroup())) {
//						if (defaultPrice == null) {
//							defaultPrice = p;
//						} else if (defaultPrice.getStart().before(p.getStart())) {
//							defaultPrice = p;
//						}
//					}
//				}
//			}
//		}
//
//		return (price != null) ? price : (defaultPrice != null) ? defaultPrice : null;
//	}

//	@Override
//	public Price getMinPrice(Group group, Currency currency, String priceGroup, Date date) {
//		try {
//			PriceImpl minPrice = new PriceImpl();
//			minPrice.setCurrency(currency.getCurrencyCode());
//			minPrice.setGroup(priceGroup);
//			for (Product product : group.getMembers()) {
//				Price price = getPrice(product.getPrices(), currency, priceGroup, date);
//				for (String tag : price.getValues().keySet()) {
//					BigDecimal value = price.getValue(tag);
//					value = minPrice.getValue(tag) != null ? minPrice.getValue(tag).min(value) : value;
//					minPrice.getValues().put(tag, value);
//				}
//			}
//			return minPrice;
//		} catch (Exception e) {
//			log.error(ExceptionUtils.getMessage(e));
//			return null;
//		}
//	}

	public void setMongoProductDaos(Map<String, MongoProductDao> mongoProductDaos) {
		this.mongoProductDaos = mongoProductDaos;
	}

	public void setSolrProductDaos(Map<String, SolrProductDao> solrProductDaos) {
		this.solrProductDaos = solrProductDaos;
	}

}
