package com.bakerbeach.market.catalog.tmp;

import java.util.Map;

public interface XProduct {

	String getGtin();

	void setGtin(String gtin);

	Map<String, ? extends XAsset> getAssets();

}
