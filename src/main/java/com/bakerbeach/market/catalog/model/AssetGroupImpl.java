package com.bakerbeach.market.catalog.model;

import java.util.HashMap;

import com.bakerbeach.market.core.api.model.Asset;
import com.bakerbeach.market.core.api.model.AssetGroup;

public class AssetGroupImpl extends HashMap<String, Asset> implements AssetGroup {
	private static final long serialVersionUID = 1L;

	public static String SIZE_SMALL = "s";
	public static String SIZE_MEDIUM = "m";
	public static String SIZE_LARGE = "l";
	public static String SIZE_XLARGE = "xl";

}
