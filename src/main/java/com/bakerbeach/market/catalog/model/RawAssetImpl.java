package com.bakerbeach.market.catalog.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bakerbeach.market.core.api.model.Asset;

public class RawAssetImpl implements Serializable, Asset {
	private static final long serialVersionUID = 1L;
	
	public static String TYPE_IMAGE = "image";
	public static String TYPE_VIDEO = "video";
	public static String TYPE_YOUTUBE = "youtube";
	public static String TYPE_PDF = "pdf";
	
	private String type;
	private String path;
	private Map<Locale, String> alt = new HashMap<Locale, String>(5);

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Map<Locale, String> getAlt() {
		return alt;
	}

	@Override
	public String getAlt(Locale locale) {
		return alt.get(locale);
	}
	
	@Override
	public void setAlt(Map<Locale, String> alt) {
		this.alt = alt;
	}

}
