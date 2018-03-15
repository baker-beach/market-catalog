package com.bakerbeach.market.xcatalog.dao;

import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group.*;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.query.Query;

import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.Product.Status;
import com.bakerbeach.market.xcatalog.model.Product.Type;

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
	public List<String> productCodes(String shopCode, Collection<Type> types, Collection<Status> status, String order,
			Integer offset, Integer limit) {
		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass)
				.retrievedFields(true, "code").field("shopCode").equal(shopCode);

		if (CollectionUtils.isNotEmpty(types)) {
			query.field("type").in(types);
		}

		if (CollectionUtils.isNotEmpty(status)) {
			query.field("status").in(status);
		}

		if (StringUtils.isNotBlank(order)) {
			query.order(order);
		}

		if (offset != null) {
			query.offset(offset);
		}

		if (limit != null) {
			query.limit(limit);
		}

		List<String> list = new ArrayList<>();
		query.forEach(p -> {
			list.add(p.getCode());
		});

		return list;
	}

	@Override
	public List<Product> byCode(String shopCode, Product.Status status, Collection<Product.Type> types,
			Collection<String> codes) {
		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass)
				.field("shopCode").equal(shopCode).field("status").equal(status);
		if (CollectionUtils.isNotEmpty(types)) {
			query.field("type").in(types);
		}
		if (CollectionUtils.isNotEmpty(codes)) {
			query.field("code").in(codes);
		}
		
		List<Product> products = new ArrayList<>();
		query.forEach(i -> {
			try {
				products.add(i);
			} catch (Exception e) {
			}
		});

		return products;
	}

	@Override
	public List<Product> byFilters(Map<String, Object> filter) {
		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass);
		for (String filterKey : filter.keySet()) {
			query.filter(filterKey, filter.get(filterKey));
		}

		List<Product> products = new ArrayList<>();
		query.forEach(i -> {
			try {
				products.add(i);
			} catch (Exception e) {
			}
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

	private static class AggregationGroup {
		public String _id;
		public List<String> codes;
		public String sort;
	}

	@Override	
	public List<Group> groupByCode(String shopCode, Status status, String groupBy, List<String> codes, List<Product.Unit> units) {

		Query<P> query = ((AdvancedDatastore) datastore).createQuery(productCollectionName, productClass)
				.field("shopCode").equal(shopCode).field("status").equal(status);
		if (CollectionUtils.isNotEmpty(units)) {
			query.field("unit").in(units);
		}
		if (CollectionUtils.isNotEmpty(codes)) {
			query.field("code").in(codes);
		}
		
		AggregationPipeline pipeline = datastore.createAggregation(productClass).match(query);
		pipeline.group(groupBy,
				org.mongodb.morphia.aggregation.Group.grouping("codes", org.mongodb.morphia.aggregation.Group.push("code")),
				org.mongodb.morphia.aggregation.Group.grouping("sort", org.mongodb.morphia.aggregation.Group.min("sort")));
		pipeline.sort(Sort.ascending("sort"));
		
		List<String> allProductCodes = new ArrayList<>();
		List<AggregationGroup> aggregationGroups = new ArrayList<>();
		pipeline.aggregate(AggregationGroup.class).forEachRemaining((p) -> {
			aggregationGroups.add(p);
			allProductCodes.addAll(p.codes);
		});

		List<Product> allProducts = byCode(shopCode, status, Arrays.asList(Product.Type.PRODUCT), allProductCodes);
		Map<String, Product> allProductsMap = new HashMap<>();
		allProducts.forEach(p -> {
			allProductsMap.put(p.getCode(), p);
		});
		
		List<Group> groups = new ArrayList<>();
		aggregationGroups.forEach(ag -> {
			try {
				Group group = groupClass.newInstance();
				group.setCode(ag._id);
				ag.codes.forEach(c -> {
					group.getMembers().add(allProductsMap.get(c));
				});
				groups.add(group);
			} catch (Exception e) {
				// TODO: handle exception
			}
		});
		
		return groups;
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
