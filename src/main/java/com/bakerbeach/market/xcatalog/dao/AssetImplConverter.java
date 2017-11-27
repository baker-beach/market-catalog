package com.bakerbeach.market.xcatalog.dao;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import com.bakerbeach.market.xcatalog.model.AssetImpl;
import com.mongodb.BasicDBObject;

public class AssetImplConverter extends TypeConverter implements SimpleValueConverter {

	public AssetImplConverter() {
		super(AssetImpl.class);
	}
	
	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value instanceof AssetImpl) {
			AssetImpl asset = (AssetImpl) value;
			BasicDBObject dbo = new BasicDBObject();
			dbo.put("type", asset.getType());
			dbo.put("path", asset.getPath());
			return dbo;
		}
		return null;
//		return super.encode(value, optionalExtraInfo);
	}

	@Override
	public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
		AssetImpl asset = new AssetImpl();
		
		return asset;
	}

}
