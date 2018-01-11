package com.bakerbeach.market.xcatalog.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.xcatalog.dao.AbstractSolrProductDao.GroupResult;
import com.bakerbeach.market.xcatalog.dao.MongoProductDao;
import com.bakerbeach.market.xcatalog.dao.SolrProductDao;
import com.bakerbeach.market.xcatalog.model.Facets;
import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Pager;
import com.bakerbeach.market.xcatalog.model.Price;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.SearchResult;
import com.bakerbeach.market.xcatalog.model.Product.Option;

public class XCatalogServiceImpl implements XCatalogService {

	protected static final Logger log = LoggerFactory.getLogger(XCatalogServiceImpl.class);

	protected Map<String, MongoProductDao> mongoProductDaos;
	protected Map<String, SolrProductDao> solrProductDaos;

	@Override
	public List<String> productCodes(String shopCode, Collection<Product.Type> types, Collection<Product.Status> status,
			Integer pageSize, Integer currentPage, String sort) {

		Integer offset = (pageSize != null && currentPage != null) ? Math.abs(currentPage * pageSize) : 0;

		List<String> list = mongoProductDaos.get(shopCode).productCodes(shopCode, types, status, sort, offset,
				pageSize);
		return list;
	}

	@Override
	public List<Product> rawByGtin(String shopCode, Product.Status status, Collection<String> codes) {
		List<Product> list = mongoProductDaos.get(shopCode).byCode(shopCode, status,
				Arrays.asList(Product.Type.PRODUCT), codes);		
		return list;
	}
	
	@Override
	public List<Product> rawByFilter(String shopCode,Map<String,Object> filters) {
		List<Product> list = mongoProductDaos.get(shopCode).byFilters(filters);
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

			refine(group, date, currency, priceGroup);

			return group;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new XCatalogServiceException(e);
		}
	}

	protected void refine(List<Group> groups, Date date, Currency currency, String priceGroup) {
		for (Group group : groups) {
			refine(group, date, currency, priceGroup);
		}
	}

	protected void refine(Group group, Date date, Currency currency, String priceGroup) {

		// cache current prices for currency, price group and given date ---
		group.getMembers().forEach(member -> {
			refine(member, date, currency, priceGroup);
		});

		// get min price for the group ---
		Map<String, Price> cachedMinPrices = new HashMap<>();
		group.getMembers().forEach(member -> {
			member.getCachedPrices().forEach((tag, price) -> {
				if (!cachedMinPrices.containsKey(tag)) {
					cachedMinPrices.put(tag, price);
				} else if (cachedMinPrices.get(tag).getValue().compareTo(price.getValue()) > 0) {
					cachedMinPrices.put(tag, price);
				}
			});
		});
		group.setCachedMinPrices(cachedMinPrices);

	}

	private void refine(Product product, Date date, Currency currency, String priceGroup) {
		{
			Map<String, Price> currentPrices = getCurrentPrices(product, currency, priceGroup, date);
			product.setCachedPrices(currentPrices);			
		}
		
		if (MapUtils.isNotEmpty(product.getOptions())) {
			product.getOptions().forEach((key, option) -> {
				Map<String, Price> prices = getCurrentPrices(option.getPrices(), currency, priceGroup, date);
				option.setCachedPrices(prices);
			});			
		}
	}
	
	@Override
	public Map<String, Price> getCurrentPrices(List<Price> prices, Currency currency, String priceGroup, Date date) {
		Map<String, Price> priceMap = new HashMap<>();
		Map<String, Price> defaultPriceMap = new HashMap<>();

		for (Price p : prices) {
			String tag = p.getTag();

			Date start = p.getStart();
			if (!start.after(date)) {
				if (currency.equals(p.getCurrency())) {
					if (priceGroup.equals(p.getGroup())) {
						if (priceMap.get(tag) == null) {
							priceMap.put(tag, p);
						} else if (priceMap.get(tag).getStart().before(p.getStart())) {
							priceMap.put(tag, p);
						}
					} else if ("default".equalsIgnoreCase(p.getGroup())) {
						if (defaultPriceMap.get(tag) == null) {
							defaultPriceMap.put(tag, p);
						} else if (defaultPriceMap.get(tag).getStart().before(p.getStart())) {
							defaultPriceMap.put(tag, p);
						}
					}
				}
			}
		}

		// get default prices ---
		defaultPriceMap.forEach((tag, price) -> {
			if (!priceMap.containsKey(tag)) {
				priceMap.put(tag, price);
			}
		});

		return priceMap;
	}
	
	@Override
	public Map<String, Price> getCurrentPrices(Option option, Currency currency, String priceGroup, Date date) {
		return getCurrentPrices(option.getPrices(), currency, priceGroup, date);
	}

	@Override
	public Map<String, Price> getCurrentPrices(Product product, Currency currency, String priceGroup, Date date) {
		return getCurrentPrices(product.getPrices(), currency, priceGroup, date);	
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

		refine(groupResult.getGroups(), date, currency, priceGroup);

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

	public void setMongoProductDaos(Map<String, MongoProductDao> mongoProductDaos) {
		this.mongoProductDaos = mongoProductDaos;
	}

	public void setSolrProductDaos(Map<String, SolrProductDao> solrProductDaos) {
		this.solrProductDaos = solrProductDaos;
	}

}
