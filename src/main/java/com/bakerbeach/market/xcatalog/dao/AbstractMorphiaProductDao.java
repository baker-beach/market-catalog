package com.bakerbeach.market.xcatalog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Product;

public abstract class AbstractMorphiaProductDao<G extends Group, P extends Product> implements MongoProductDao {
	protected Morphia morphia = new Morphia();
	protected Datastore datastore;

	protected String uri;
	protected String dbName;
	protected String productCollectionName;
	protected String groupCollectionName;
	protected String packages;

	protected Class<P> productClass;
	protected Class<G> groupClass;

	public AbstractMorphiaProductDao(Class<G> groupClass, Class<P> productClass, Datastore datastore,
			String productCollectionName, String groupCollectionName) {
		this.datastore = datastore;
		this.groupClass = groupClass;
		this.productClass = productClass;
		this.productCollectionName = productCollectionName;
		this.groupCollectionName = groupCollectionName;
	}

	@Override
	public List<Product> byCode(String shopCode, Product.Status status, Collection<Product.Type> types,
			Collection<String> codes) {
		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass)
				.field("shopCode").equal(shopCode).field("status").equal(status).field("type").in(types).field("code")
				.in(codes);

		List<Product> products = new ArrayList<>();
		query.forEach(i -> {
			products.add(i);
		});

		return products;
	}

	@Override
	public List<Product> productByGroupCode(String shopCode, Product.Status status, String groupBy,
			Collection<String> codes) {
		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass)
				.field("shopCode").equal(shopCode).field("status").equal(status).field(groupBy).in(codes);

		List<Product> products = new ArrayList<>();
		query.forEach(i -> {
			products.add(i);
		});

		return products;
	}

	@Override
	public void save(Product product) {
		((AdvancedDatastore) datastore).save(productCollectionName, product);
	}

	@Override
	public List<Group> groupByCode(String shopCode, Product.Status status, Collection<String> codes) {
		Query<G> query = ((AdvancedDatastore) datastore).createQuery(groupCollectionName, groupClass).field("shopCode")
				.equal(shopCode).field("status").equal(status).field("code").in(codes);

		List<Group> groups = new ArrayList<>();
		query.forEach(i -> {
			groups.add(i);
		});

		return groups;
	}

	@Override
	public void groupSave(Group group) {
		((AdvancedDatastore) datastore).save(groupCollectionName, group);
	}

	@Override
	public Group newInstance(String code, String shopCode) throws InstantiationException, IllegalAccessException {
		G group = groupClass.newInstance();
		group.setCode(code);
		group.setShopCode(shopCode);

		return group;
	}

}
