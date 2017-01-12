package com.bakerbeach.market.catalog.tmp;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class XXServiceImpl<T> implements XXService<T> {
	private Class<T> productClass;
	private Datastore datastore;
	
	public XXServiceImpl(Class<T> productClass, Datastore datastore) {
		this.productClass = productClass;
		this.datastore = datastore;
	}
	
	@Override
	public void add(T product, List<T> products) {
		products.add(product);
	}
	
	@Override
	public List<T> findByGtin(List<String> gtin) {
		try {
			List<T> list = new ArrayList<T>();
			
			Query<T> query = datastore.createQuery(productClass).field("gtin").in(gtin);
			query.iterator().forEachRemaining(p -> {
				list.add(p);
			});
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return null;
	}
	
//	@Override
//	public List<XProduct> findByGtin(List<String> gtin) {
//		try {
//			List<XProduct> list = new ArrayList<>();
//			
//			Query<? extends XProduct> query = datastore.createQuery(productClass).field("gtin").in(gtin);
//			query.iterator().forEachRemaining(p -> {
//				list.add(p);
//			});
//			
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		
//		return null;
//	}


}
