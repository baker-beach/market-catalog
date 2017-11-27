package com.bakerbeach.market.xcatalog.dao;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.bakerbeach.market.xcatalog.dao.AbstractSolrProductDao.GroupResult;
import com.bakerbeach.market.xcatalog.model.Facets;

public interface SolrProductDao {

	GroupResult groupQuery(Locale locale, String priceGroup, Currency currency, Facets facets, String query,
			List<String> filterQueries, String groupBy, Integer pageSize, Integer currentPage, String sort);

}
