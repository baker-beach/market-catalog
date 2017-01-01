package com.bakerbeach.market.catalog.service;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bakerbeach.market.catalog.model.CatalogSearchResult;
import com.bakerbeach.market.catalog.model.Pager;
import com.bakerbeach.market.catalog.utils.FacetFilterFactory;
import com.bakerbeach.market.core.api.model.FilterList;

import junit.framework.Assert;

@ActiveProfiles(profiles = { "env.test", "product.published" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/market-*.xml" })
public class CatalogServiceImplTest {

	@Autowired()
	@Qualifier("catalogService")
	protected CatalogService catalogService;

	@Autowired()
	protected FacetFilterFactory facetFilterFactory;

	@Test
	public void testGetProductByPrimaryGroup() {
		try {
			Locale locale = Locale.GERMANY;
			String priceGroup = "DEFAULT";
			Currency currency = Currency.getInstance("EUR");
			String countryOfDelivery = "IT";
			Date date = new Date();
			List<String> primaryGroup = Arrays.asList("chjk1510");

			CatalogSearchResult catalogSearchResult = catalogService.findGroupByGroupCode(locale, priceGroup,
					currency, countryOfDelivery, date, primaryGroup);

			Assert.assertTrue(catalogSearchResult != null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

//	@Test
//	public void testGetProductByGtin() {
//		try {
//			Locale locale = Locale.GERMANY;
//			String priceGroup = "DEFAULT";
//			Currency currency = Currency.getInstance("EUR");
//			String countryOfDelivery = "IT";
//			Date date = new Date();
//			List<String> gtin = Arrays.asList("4894487018155");
//
//			CatalogSearchResult catalogSearchResult = catalogService.fi.findGroupedProductsByGtin(locale, priceGroup, currency,
//					countryOfDelivery, date, gtin);
//
//			Assert.assertTrue(catalogSearchResult != null);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

	@Test
	public void testFindProduct() {
		try {
			Locale locale = Locale.GERMANY;
			String priceGroup = "DEFAULT";
			Currency currency = Currency.getInstance("EUR");
			String assortmentCode = "pm";
			String countryOfDelivery = "IT";
			Date date = new Date();
			String sort = null;

			Map<String, String[]> parameter = new HashMap<String, String[]>();
			parameter.put("color_code", new String[] { "colour-bear" });
			String lang = null;
			FilterList filterList = facetFilterFactory.newInstanceSearch(parameter, lang);
			// FilterList filterList = null;

			Pager pager = new Pager(120, 1);
			
			CatalogSearchResult catalogSearchResult = catalogService.groupIndexQuery(locale, priceGroup, currency,
					assortmentCode, countryOfDelivery, date, filterList, "*:*", "primnary_group", pager, sort);

			Assert.assertTrue(catalogSearchResult != null);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
