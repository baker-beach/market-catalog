package com.bakerbeach.market.catalog.tmp;

import java.util.List;

public interface Service {

	List<XProduct> findByGtin(List<String> asList);

	<T extends XProduct> List<T> findByGtin2(Class<T> clazz, List<String> asList);

	<T extends XProduct> void save(T product);

}
