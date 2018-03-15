package com.bakerbeach.market.xcatalog.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.Product.Status;
import com.bakerbeach.market.xcatalog.model.Product.Type;
import com.bakerbeach.market.xcatalog.model.Product.Unit;

public interface MongoProductDao {

	List<Product> byCode(String shopCode, Product.Status status, Collection<Product.Type> types,
			Collection<String> codes);

	List<Product> productByGroupCode(String shopCode, Product.Status status, String groupBy, Collection<String> codes);

	void save(Product product);

	List<Group> groupByCode(String shopCode, Product.Status status, Collection<String> codes);

	List<Group> groupByCode(String shopCode, Status status, String groupBy, List<String> codes, List<Unit> units);

	void groupSave(Group group);

	Group newInstance(String code, String shopCode) throws InstantiationException, IllegalAccessException;

	List<String> productCodes(String shopCode, Collection<Type> types, Collection<Status> status, String order,
			Integer offset, Integer limit);

	List<Product> byFilters(Map<String, Object> filters);

}
