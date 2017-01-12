package com.bakerbeach.market.catalog.tmp;

import java.util.List;

public interface XXService<T> {

	List<T> findByGtin(List<String> gtin);

	void add(T product, List<T> products);

}
