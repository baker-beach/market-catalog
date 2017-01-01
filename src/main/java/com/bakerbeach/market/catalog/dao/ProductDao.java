package com.bakerbeach.market.catalog.dao;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.catalog.model.RawProduct;
import com.bakerbeach.market.core.api.model.Product;

public interface ProductDao {

	List<RawProduct> findRawByGtin(Collection<String> gtins, String status);

	List<RawProduct> findRawByField(String field, List<String> values, String status);

	List<String> findGtinByStatusAndIndex(String status, Boolean index);

	List<GroupedProduct> findGroupByGroupCode(Locale locale, String priceGroup, Currency currency,
			Collection<String> groupCode, String countryOfDelivery, Date date, String groupBy);
	
	List<Product> findByGtin(Locale locale, String priceGroup, Currency currency, String countryOfDelivery, Date date,
			Collection<String> gtins);


	
	// ---
	


//	@Deprecated
//	Collection<GroupedProduct> groupQueryByGtin(Locale locale, String priceGroup, Currency currency,
//			String countryOfDelivery, Date date, Collection<String> gtins);

	
	
}
