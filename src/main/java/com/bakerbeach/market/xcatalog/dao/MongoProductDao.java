package com.bakerbeach.market.xcatalog.dao;

import java.util.Collection;
import java.util.List;

import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.Product.Status;
import com.bakerbeach.market.xcatalog.model.Product.Type;

public interface MongoProductDao {

	List<Product> byCode(String shopCode, Product.Status status, Collection<Product.Type> types,
			Collection<String> codes);

	List<Product> productByGroupCode(String shopCode, Product.Status status, String groupBy, Collection<String> codes);

	void save(Product product);

	List<Group> groupByCode(String shopCode, Product.Status status, Collection<String> codes);

	void groupSave(Group group);

	Group newInstance(String code, String shopCode) throws InstantiationException, IllegalAccessException;

	List<String> productCodes(String shopCode, Collection<Type> types, Collection<Status> status, String order,
			Integer offset, Integer limit);

}
