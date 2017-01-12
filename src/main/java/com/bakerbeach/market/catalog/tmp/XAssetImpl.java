package com.bakerbeach.market.catalog.tmp;

public class XAssetImpl implements XAsset {

	protected String type = "image";
	protected String path;

	public XAssetImpl() {
	}

	public XAssetImpl(String type, String path) {
		this.type = type;
		this.path = path;
	}

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

}
