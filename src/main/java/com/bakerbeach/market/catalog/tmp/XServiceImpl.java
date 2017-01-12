package com.bakerbeach.market.catalog.tmp;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class XServiceImpl implements XService {
	private Datastore datastore;
	private XProduct productTemplate;

	public XServiceImpl(XProduct productTemplate, Datastore datastore) {
		this.productTemplate = productTemplate;
		this.datastore = datastore;
	}
	
	@Override
	public List<XProduct> findByGtin(List<String> gtin) {
		try {
			Query<? extends XProduct> query = datastore.createQuery(productTemplate.getClass()).field("gtin").in(gtin);
			List<? extends XProduct> products = query.asList();
			
			List<XProduct> list = new ArrayList<XProduct>(products);
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return null;
	}
	
//	private Class<T> productClass;
//	private Datastore datastore;
//	
//	public XServiceImpl(Class<T> productClass, Datastore datastore) {
//		this.productClass = productClass;
//		this.datastore = datastore;
//	}

//	public XServiceImpl(Class<T> productClass, Datastore datastore) {
//		this.productClass = productClass;
//		this.datastore = datastore;
//	}
	
//	@Override
//	public List<T> findByGtin(List<String> gtin) {
//		try {
//			
//			Query<T> query = datastore.createQuery(productClass).field("gtin").in(gtin);
//			List<T> products = query.asList();
//			
//			return products;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//		return null;
//	}
//	
//	@Override
//	public List<? super T> findByGtin2(List<String> gtin) {
//		try {
//			
//			Query<T> query = datastore.createQuery(productClass).field("gtin").in(gtin);
//			List<T> products = query.asList();
//			
//			return new ArrayList<T>(products);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//		return null;
//	}
	

}
