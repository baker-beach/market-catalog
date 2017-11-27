package com.bakerbeach.market.xcatalog.dao;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import com.bakerbeach.market.xcatalog.model.Asset;
import com.bakerbeach.market.xcatalog.model.AssetImpl;
import com.mongodb.DBObject;

public class StdAssetConverter extends TypeConverter implements SimpleValueConverter {

	public StdAssetConverter() {
		super(Asset.class);
	}
	
	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		return super.encode(value, optionalExtraInfo);
	}

	@Override
	public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
		if (fromDBObject instanceof DBObject) {
			DBObject dbo = (DBObject) fromDBObject;
			Asset asset = new AssetImpl();
			asset.setType((String) dbo.get("type"));
			asset.setPath((String) dbo.get("path"));
			
			return asset;
		}

		return null;
	}

}
