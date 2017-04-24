package com.bakerbeach.market.catalog.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.catalog.dao.ProductDao;
import com.bakerbeach.market.catalog.dao.SolrProductDao;
import com.bakerbeach.market.catalog.model.CatalogSearchResult;
import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.catalog.model.Pager;
import com.bakerbeach.market.catalog.model.PriceRangeFilter;
import com.bakerbeach.market.catalog.model.RawProduct;
import com.bakerbeach.market.catalog.model.ShopProduct;
import com.bakerbeach.market.catalog.model.SimpleProduct;
import com.bakerbeach.market.catalog.model.VariantsStdImpl;
import com.bakerbeach.market.catalog.utils.FacetFilterFactory;
import com.bakerbeach.market.core.api.model.BundleProduct;
import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.FilterList;
import com.bakerbeach.market.core.api.model.Option;
import com.bakerbeach.market.core.api.model.Product;
import com.bakerbeach.market.core.api.model.ShopContext;
import com.bakerbeach.market.inventory.api.model.InventoryStatus;
import com.bakerbeach.market.inventory.api.service.InventoryService;

public class CatalogServiceImpl implements CatalogService {
	protected static final Logger log = LoggerFactory.getLogger(CatalogServiceImpl.class);

	private ProductDao mongoProductDao;
	private SolrProductDao solrProductDao;
	private InventoryService inventoryService;

	@Override
	public List<RawProduct> findRawByGtin(String status, List<String> gtin) {
		List<RawProduct> products = mongoProductDao.findRawByGtin(gtin, status);
		return products;
	}
	
	@Override
	public List<RawProduct> findRawByField(String status, String key, List<String> values) {
		List<RawProduct> products = mongoProductDao.findRawByField(key, values, status);
		return products;
	}
	
	@Override
	public List<String> findGtin(String status, Boolean index) {
		List<String> gtin = mongoProductDao.findGtinByStatusAndIndex(status, index);
		return gtin;
	}

	@Override
	public CatalogSearchResult findGroupByGroupCode(Locale locale, String priceGroup, Currency currency,
			String countryOfDelivery, Date date, Collection<String> primaryGroups, String groupField) {
		Collection<GroupedProduct> products = mongoProductDao.findGroupByGroupCode(locale, priceGroup, currency,
				primaryGroups, countryOfDelivery, date, groupField);
		setInventory(products);
		refineProduct(products);
		
		CatalogSearchResult catalogSearchResult = new CatalogSearchResult();
		catalogSearchResult.setProducts(products);
		catalogSearchResult.setPager(new Pager(products.size(), 1, products.size()));
		return catalogSearchResult;
	}

	@Override
	public CatalogSearchResult findGroupByGroupCode(Locale locale, String priceGroup, Currency currency,
			String countryOfDelivery, Date date, Collection<String> primaryGroups) {
		return findGroupByGroupCode(locale, priceGroup, currency, countryOfDelivery, date, primaryGroups, "primaryGroup");
	}
	
	@Override
	public CatalogSearchResult groupIndexQuery(Locale locale, String priceGroup, Currency currency,
			String assortmentCode, String countryOfDelivery, Date date, FilterList filterList, String query, List<String> filterQueries,
			String groupBy, Pager pager, String sort) {

		if (StringUtils.isBlank(query)) {
			query = "*:*";
		}
		
		specifyPriceFilter(filterList, currency.getCurrencyCode(), priceGroup);
		List<GroupedProduct> products = solrProductDao.groupQuery(locale, priceGroup, currency, filterList, query, filterQueries,
				groupBy, pager.getPageSize(), pager.getCurrentPage(), sort);
		refineProduct(products);

		CatalogSearchResult catalogSearchResult = new CatalogSearchResult();
		catalogSearchResult.setProducts(products);
		catalogSearchResult.setFilterList(filterList);
		catalogSearchResult.setPager(new Pager(products.size(), 1, products.size()));
		return catalogSearchResult;
	}

	@Override
	public CatalogSearchResult groupIndexQuery(Locale locale, String priceGroup, Currency currency,
			String assortmentCode, String countryOfDelivery, Date date, FilterList filterList, String query,
			String groupBy, Pager pager, String sort) {
		
		if (StringUtils.isBlank(query)) {
			query = "*:*";
		}
		
		List<String> filterQueries = null;
		
		specifyPriceFilter(filterList, currency.getCurrencyCode(), priceGroup);
		List<GroupedProduct> products = solrProductDao.groupQuery(locale, priceGroup, currency, filterList, query, filterQueries,
				groupBy, pager.getPageSize(), pager.getCurrentPage(), sort);
		refineProduct(products);
		
		CatalogSearchResult catalogSearchResult = new CatalogSearchResult();
		catalogSearchResult.setProducts(products);
		catalogSearchResult.setFilterList(filterList);
		catalogSearchResult.setPager(new Pager(products.size(), 1, products.size()));
		return catalogSearchResult;
	}
	
	@Override
	public List<Product> findByGtin(Locale locale, String priceGroup, Currency currency, String countryOfDelivery,
			Date date, Collection<String> gtins) {
		List<Product> products = mongoProductDao.findByGtin(locale, priceGroup, currency, countryOfDelivery, date,
				gtins);
		return products;
	}

//	@Override
//	public CatalogSearchResult findGroupedProductsByGtin(Locale locale, String priceGroup, Currency currency,
//			String countryOfDelivery, Date date, Collection<String> gtins) {
//		Collection<GroupedProduct> products = mongoProductDao.groupQueryByGtin(locale, priceGroup, currency,
//				countryOfDelivery, date, gtins);
//		// setInventory(products);
//
//		CatalogSearchResult catalogSearchResult = new CatalogSearchResult();
//		catalogSearchResult.setProducts(products);
//		catalogSearchResult.setPager(new Pager(products.size(), 1, products.size()));
//		return catalogSearchResult;
//	}

//	@Override
//	public CatalogSearchResult findGroupedProductsByPrimaryGroup(Locale locale, String priceGroup, Currency currency,
//			String countryOfDelivery, Date date, Collection<String> primaryGroups) {
//		Collection<GroupedProduct> products = mongoProductDao.findGroupByGroupCode(locale, priceGroup, currency,
//				primaryGroups, countryOfDelivery, date, "primaryGroup");
//		setInventory(products);
//		refineProduct(products);
//
//		CatalogSearchResult catalogSearchResult = new CatalogSearchResult();
//		catalogSearchResult.setProducts(products);
//		catalogSearchResult.setPager(new Pager(products.size(), 1, products.size()));
//		return catalogSearchResult;
//	}

	@Override
	public ShopProduct findShopProductByGtin(ShopContext shopContext, String gtin) {
		// TODO Auto-generated method stub
		throw new RuntimeException("mach mal");
	}

	@Override
	public Map<String, Integer> getBom(String shopCode, String ean, String string) {
		// TODO Auto-generated method stub
		throw new RuntimeException("mach mal");
	}

	@Override
	public CatalogSearchResult findGroupedProductByPrimaryGroup(ShopContext cmsContext, String primary_group) {
		// TODO Auto-generated method stub
		throw new RuntimeException("mach mal");
	}

	private void refineProduct(Collection<GroupedProduct> products) {

		// TODO: check for group specific data

		for (GroupedProduct groupedProduct : products) {
			BigDecimal minPrice = null;
			BigDecimal maxPrice = null;
			BigDecimal minStdPrice = null;
			BigDecimal maxStdPrice = null;
			BigDecimal minDiscountOnStandardPrice = null;
			BigDecimal maxDiscountOnStandardPrice = null;
			BigDecimal minBasePrice = null;
			BigDecimal maxBasePrice = null;
			Boolean isAvailable = false;

			for (Product member : groupedProduct.getMembers()) {
				if (member instanceof BundleProduct || member instanceof SimpleProduct) {
					BigDecimal price = member.getPrice();
					BigDecimal standardPrice = member.getStdPrice();
					BigDecimal discountOnStandardPrice = member.getDiscountOnStandardPrice();
					BigDecimal basePrice = member.getBasePrice1();
					String basePrice1Unit = member.getBasePrice1Unit();

					minPrice = minPrice != null ? minPrice.min(price) : price;
					maxPrice = maxPrice != null ? maxPrice.max(price) : price;

					if (standardPrice != null) {
						minStdPrice = minStdPrice != null ? minStdPrice.min(standardPrice) : standardPrice;
						maxStdPrice = maxStdPrice != null ? maxStdPrice.max(standardPrice) : standardPrice;

						maxDiscountOnStandardPrice = maxDiscountOnStandardPrice != null
								? maxDiscountOnStandardPrice.max(discountOnStandardPrice) : discountOnStandardPrice;
						minDiscountOnStandardPrice = minDiscountOnStandardPrice != null
								? minDiscountOnStandardPrice.min(discountOnStandardPrice) : discountOnStandardPrice;
					}

					if (basePrice != null && member.getBasePrice1Unit() != null) {
						minBasePrice = minBasePrice != null ? minBasePrice.min(basePrice) : basePrice;
						maxBasePrice = maxBasePrice != null ? maxBasePrice.max(basePrice) : basePrice;
					}

					if (member.getMoq() > 0) {
						isAvailable = true;
					}

				}
			}

			VariantsStdImpl variants = new VariantsStdImpl(groupedProduct.getDim1(), groupedProduct.getDim2());
			for (Product member : groupedProduct.getMembers()) {
				if (member instanceof BundleProduct || member instanceof SimpleProduct) {
					variants.add(member);
				}
			}
			groupedProduct.setVariants(variants);

			if (groupedProduct.getAssets() == null || groupedProduct.getAssets().isEmpty()) {
				for (Product member : groupedProduct.getMembers()) {
					if (member.getAssets() != null) {
						groupedProduct.setAssets(member.getAssets());
						break;
					}
				}
			}

			groupedProduct.setMinPrice(minPrice);
			groupedProduct.setMaxPrice(maxPrice);
			groupedProduct.setMinStdPrice(minStdPrice);
			groupedProduct.setMaxStdPrice(maxStdPrice);
			groupedProduct.setMinDiscountOnStandardPrice(minDiscountOnStandardPrice);
			groupedProduct.setMaxDiscountOnStandardPrice(maxDiscountOnStandardPrice);
			groupedProduct.setIsAvailable(isAvailable);
		}
	}

	private void setInventory(Collection<GroupedProduct> products) {
		List<Product> list = new ArrayList<Product>();
		for (GroupedProduct gp : products) {
			list.addAll(gp.getMembers());
		}

		// TODO: besser nur einer abfrage für alle eans
		for (Product p : list) {
			try {
				InventoryStatus status = inventoryService.getInventoryStatus(p.getGtin());
				Integer stock = status.getStock();
				Integer outOfStockLimit = (status.getOutOfStockLimit() != null) ? status.getOutOfStockLimit() : 0;

				p.setMoq(stock + outOfStockLimit);
				p.setOutOfStockLimit(outOfStockLimit);
				if (stock + outOfStockLimit > 0) {
					p.setAvailable(true);
				}
			} catch (Exception e) {
				p.setMoq(0);
				p.setOutOfStockLimit(0);
				p.setAvailable(false);
			}
		}
	}

	private void specifyPriceFilter(FilterList filterList, String currencyCode, String priceGroup) {
		if (filterList != null && filterList.containsId(FacetFilterFactory.GENERAL_PRICE_KEY)) {
			Filter generalPriceFilter = filterList.get(FacetFilterFactory.GENERAL_PRICE_KEY);
			filterList.remove(FacetFilterFactory.GENERAL_PRICE_KEY);

			String id = new StringBuilder(currencyCode).append("_").append(priceGroup).append("_price").toString()
					.toLowerCase();
			PriceRangeFilter customerPriceFilter = new PriceRangeFilter(id);
			customerPriceFilter.setActive(generalPriceFilter.isActive());
			for (Option option : generalPriceFilter.getSelectedOptions()) {
				customerPriceFilter.addOption(option);
			}

			filterList.add(customerPriceFilter);
		}
	}

	public void setMongoProductDao(ProductDao mongoProductDao) {
		this.mongoProductDao = mongoProductDao;
	}

	public void setSolrProductDao(SolrProductDao solrProductDao) {
		this.solrProductDao = solrProductDao;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@Override
	public void save(RawProduct product) {
		
		mongoProductDao.save(product);
		
	}

}
