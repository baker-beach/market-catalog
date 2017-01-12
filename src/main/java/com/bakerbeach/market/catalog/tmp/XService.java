package com.bakerbeach.market.catalog.tmp;

import java.util.List;

public interface XService {

	List<XProduct> findByGtin(List<String> gtin);


}
