package com.bakerbeach.market.xcatalog.dao;

import org.mongodb.morphia.Datastore;

import com.bakerbeach.market.xcatalog.model.GroupImpl;
import com.bakerbeach.market.xcatalog.model.ProductImpl;

public class MorphiaProductDaoImpl extends AbstractMorphiaProductDao<GroupImpl, ProductImpl> {

	protected MorphiaProductDaoImpl(Datastore datastore, String productCollection, String groupCollection)
			throws Exception {
		super(GroupImpl.class, ProductImpl.class, datastore, productCollection, groupCollection);
	}

}
