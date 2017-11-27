package com.bakerbeach.market.xcatalog.model;

import java.io.Serializable;

import com.bakerbeach.market.xcatalog.model.Asset;

//@Entity(noClassnameStored = true)
public class AssetImpl implements Asset, Serializable {
//	@Id protected ObjectId id;
	
	private String type;
	private String path;


	public AssetImpl() {
	}
	
	public AssetImpl(String type, String path) {
		super();
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
