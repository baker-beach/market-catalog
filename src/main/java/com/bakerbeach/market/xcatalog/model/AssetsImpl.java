package com.bakerbeach.market.xcatalog.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.xcatalog.model.Asset;
import com.bakerbeach.market.xcatalog.model.Assets;

public class AssetsImpl implements Assets {

	Map<String, List<Map<String, Asset>>> map = new HashMap<>();

	@Override
	public void add(String tag, Map<String, Asset> assetGroup) {
		if (!map.containsKey(tag)) {
			map.put(tag, new ArrayList<Map<String, Asset>>());
		}
		map.get(tag).add(assetGroup);
	}
	
	
	@Override
	public List<Asset> get(String tag, String size) {
		// TODO Auto-generated method stub
		return null;
	}

}
