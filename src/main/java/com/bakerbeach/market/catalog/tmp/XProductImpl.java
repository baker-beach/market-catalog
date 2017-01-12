package com.bakerbeach.market.catalog.tmp;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class XProductImpl implements XProduct {
	@Id protected ObjectId id;
	protected String gtin;
	protected Map<String, XAssetImpl> assets = new HashMap<>();
//	protected Map<String, ? extends XAsset> assets = new HashMap<>();

	@Override
	public String getGtin() {
		return gtin;
	}
	
	@Override
	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Override
	public Map<String, ? extends XAsset> getAssets() {
		return assets;
	}
	
	public void setAssets(Map<String, XAssetImpl> assets) {
		this.assets = assets;
	}
	
	public void add(String group, XAssetImpl asset) {
		assets.put(group, asset);
	}

}
