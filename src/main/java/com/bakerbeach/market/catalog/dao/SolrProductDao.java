package com.bakerbeach.market.catalog.dao;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.core.api.model.FilterList;

public interface SolrProductDao {

	List<GroupedProduct> groupQuery(Locale locale, String priceGroup, Currency currency, FilterList filterList,
			String query, List<String> filterQueries, String groupBy, Integer pageSize, Integer currentPage,
			String sort);

	@Deprecated
	List<GroupedProduct> groupQuery(Locale locale, String priceGroup, Currency currency, FilterList filterList,
			String query, String groupBy, Integer pageSize, Integer currentPage, String sort);

}
