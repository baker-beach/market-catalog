package com.bakerbeach.market.catalog.tmp;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class ServiceImpl implements Service {
	private Datastore datastore;
	
	public ServiceImpl(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public <T extends XProduct> List<T> findByGtin2(Class<T> clazz, List<String> gtin) {		
		try {

			final Query<T> query = datastore.createQuery(clazz).field("gtin").in(gtin);
			List<T> products = query.asList();
			
			
			
//			T product = clazz.newInstance();
//			product.setGtin("my xp 1");
//			// dann über morphia ---
//			if (product instanceof MyXProduct) {
//				((MyXProduct) product).setSize("size 1");				
//			}
//			products.add(product);
			
			
			return products;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <T extends XProduct> void save(T product) {
		datastore.save(product);
	}
	
	
	@Override
	public List<XProduct> findByGtin(List<String> asList) {
		// TODO Auto-generated method stub
		
		List<XProduct> products = new ArrayList<>();

		MyXProductImpl product = new MyXProductImpl();
		product.setGtin("my xp 1");
		product.setSize("size 1");
		
		products.add(product);
		
		return products;
	}

}
