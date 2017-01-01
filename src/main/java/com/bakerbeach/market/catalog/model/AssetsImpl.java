package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bakerbeach.market.core.api.model.Asset;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.Assets;

public class AssetsImpl extends HashMap<String, List<AssetGroup>> implements Assets {
	private static final long serialVersionUID = 1L;

	@Override
	public List<Asset> get(String tag, String size) {
		List<Asset> toBeReturned = new ArrayList<Asset>();

		List<AssetGroup> assetGroups = get(tag);
		if (assetGroups != null) {
			for (AssetGroup assetGroup : assetGroups) {
				Asset asset = assetGroup.get(size);
				if (asset != null) {
					toBeReturned.add(asset);
				}
			}
		}

		return toBeReturned;
	}

	@Override
	public void add(String tag, AssetGroup assetGroup) {
		if (!containsKey(tag)) {
			put(tag, new ArrayList<AssetGroup>());
		}
		get(tag).add(assetGroup);
	}
}
