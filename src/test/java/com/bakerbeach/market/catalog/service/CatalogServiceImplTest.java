package com.bakerbeach.market.catalog.service;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bakerbeach.market.catalog.model.CatalogSearchResult;
import com.bakerbeach.market.catalog.tmp.MyXProduct;
import com.bakerbeach.market.catalog.tmp.MyXProductImpl;
import com.bakerbeach.market.catalog.tmp.Service;
import com.bakerbeach.market.catalog.tmp.ServiceImpl;
import com.bakerbeach.market.catalog.tmp.XAssetImpl;
import com.bakerbeach.market.catalog.tmp.XProduct;
import com.bakerbeach.market.catalog.tmp.XProductImpl;
import com.bakerbeach.market.catalog.tmp.XService;
import com.bakerbeach.market.catalog.tmp.XServiceImpl;
import com.bakerbeach.market.catalog.tmp.XXService;
import com.bakerbeach.market.catalog.tmp.XXServiceImpl;

import junit.framework.Assert;

@ActiveProfiles(profiles = { "env.test", "product.published" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/market-*.xml" })
public class CatalogServiceImplTest {

	@Autowired()
	@Qualifier("catalogService")
	protected CatalogService catalogService;

	@Autowired
	protected Datastore datastore;

	
	@Test
	public void bar() {
		Service service = new ServiceImpl(datastore);

		MyXProductImpl product = new MyXProductImpl();
		product.setGtin("my first product gtin");
		product.setSize("my first product size");
		product.setColor("my first product color");
		
		product.add("detail", new XAssetImpl("image", "/images/test/1"));
		
//		product.getAssets().put("detail", new XAssetImpl());
//		product.getAssets().put("listing", new XAssetImpl());

		service.save(product);
		
	}
	
	@Test
	public void foo() {

//		XService<MyXProductImpl> xService = new XServiceImpl<MyXProductImpl>(MyXProductImpl.class, datastore);
//		List<MyXProductImpl> list = xService.findByGtin(Arrays.asList("my first product gtin"));
//		List<? super MyXProductImpl> list2 = xService.findByGtin2(Arrays.asList("my first product gtin"));
		
	
		XXServiceImpl<MyXProductImpl> xxService = new XXServiceImpl<MyXProductImpl>(MyXProductImpl.class, datastore);
		List<? extends XProduct> list = xxService.findByGtin(Arrays.asList("my first product gtin"));
		List<MyXProductImpl> list2 = xxService.findByGtin(Arrays.asList("my first product gtin"));
//		xxService.add(new MyXProductImpl(), list);
//		xxService.add(new XProductImpl(), list);
//		xxService.add(new String(), list);
		

//		XService xService = new XServiceImpl(new MyXProductImpl(), datastore);
//		List<XProduct> list = xService.findByGtin(Arrays.asList("my first product gtin"));
		
//		list.add(new XProductImpl());
//		list.add(new MyXProductImpl());
		
		list.forEach(i -> {
			System.out.println(i);
			if (i instanceof MyXProduct) {				
				System.out.println("YES");
			}
		});
		
		
		Service service = new ServiceImpl(datastore);

		/*
		List<MyXProductImpl> products2 = service.findByGtin2(MyXProductImpl.class, Arrays.asList("my first product gtin"));
		Assert.assertTrue(!products2.isEmpty());
		for (MyXProductImpl p : products2) {
			System.out.println(p);
			System.out.println(p.getSize());
		}
		*/
		
		/*
		List<? extends MyXProduct> products3 = service.findByGtin2(MyXProductImpl.class, Arrays.asList("my first product gtin"));		
		Assert.assertTrue(!products3.isEmpty());
		for (MyXProduct p : products3) {
			System.out.println(p);
			System.out.println(p.getSize());
			System.out.println(p.getColor());
			
			p.getAssets().values().forEach(a -> System.out.println(a.getPath()));
		}
		*/		
		
		/*
		List<RawProduct> products = catalogService.findRawByGtin(Status.PUBLISHED.name(), Arrays.asList("test-prod-1"));
		if (CollectionUtils.isNotEmpty(products)) {
			catalogService.foo(products.get(0));
		}
		Assert.assertTrue(false);
		*/
	}

	@Test
	public void testGetProductByPrimaryGroup() {
		try {
			Locale locale = Locale.GERMANY;
			String priceGroup = "DEFAULT";
			Currency currency = Currency.getInstance("EUR");
			String countryOfDelivery = "IT";
			Date date = new Date();
			List<String> primaryGroup = Arrays.asList("chjk1510");

			CatalogSearchResult catalogSearchResult = catalogService.findGroupByGroupCode(locale, priceGroup, currency,
					countryOfDelivery, date, primaryGroup);

			Assert.assertTrue(catalogSearchResult != null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	// @Test
	// public void testGetProductByGtin() {
	// try {
	// Locale locale = Locale.GERMANY;
	// String priceGroup = "DEFAULT";
	// Currency currency = Currency.getInstance("EUR");
	// String countryOfDelivery = "IT";
	// Date date = new Date();
	// List<String> gtin = Arrays.asList("4894487018155");
	//
	// CatalogSearchResult catalogSearchResult =
	// catalogService.fi.findGroupedProductsByGtin(locale, priceGroup, currency,
	// countryOfDelivery, date, gtin);
	//
	// Assert.assertTrue(catalogSearchResult != null);
	// } catch (Exception e) {
	// fail(e.getMessage());
	// }
	// }

	// @Test
	// public void testFindProduct() {
	// try {
	// Locale locale = Locale.GERMANY;
	// String priceGroup = "DEFAULT";
	// Currency currency = Currency.getInstance("EUR");
	// String assortmentCode = "pm";
	// String countryOfDelivery = "IT";
	// Date date = new Date();
	// String sort = null;
	//
	// Map<String, String[]> parameter = new HashMap<String, String[]>();
	// parameter.put("color_code", new String[] { "colour-bear" });
	// String lang = null;
	// FilterList filterList = facetFilterFactory.newInstanceSearch(parameter,
	// lang);
	// // FilterList filterList = null;
	//
	// Pager pager = new Pager(120, 1);
	//
	// CatalogSearchResult catalogSearchResult =
	// catalogService.groupIndexQuery(locale, priceGroup, currency,
	// assortmentCode, countryOfDelivery, date, filterList, "*:*",
	// "primnary_group", pager, sort);
	//
	// Assert.assertTrue(catalogSearchResult != null);
	//
	// } catch (Exception e) {
	// fail(e.getMessage());
	// }
	// }
}
