package com.bakerbeach.market.xcatalog.dao;

import java.util.Locale;

import org.apache.solr.common.SolrDocument;

import com.bakerbeach.market.xcatalog.model.GroupImpl;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.ProductImpl;

public class SolrProductDaoImpl extends AbstractSolrProductDao<GroupImpl, ProductImpl> {
	
	protected SolrProductDaoImpl(String url) {
		super(GroupImpl.class, ProductImpl.class, url);
	}

	@Override
	protected Product createProduct(SolrDocument doc, Locale locale, String priceGroup, String currencyCode) {
		return super.createProduct(doc, locale, priceGroup, currencyCode);
	}

}
