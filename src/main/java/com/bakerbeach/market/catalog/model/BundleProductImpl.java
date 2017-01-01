package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.List;

import com.bakerbeach.market.core.api.model.BundleComponent;
import com.bakerbeach.market.core.api.model.BundleProduct;
import com.bakerbeach.market.core.api.model.Type;

public class BundleProductImpl extends AbstractProduct implements BundleProduct {
	private static final long serialVersionUID = 1L;

	private Type type = Type.BUNDLE;
	private List<BundleComponent> elements = new ArrayList<BundleComponent>();

	@Override
	public List<BundleComponent> getComponents() {
		return elements;
	}
	
	@Override
	public Type getType() {
		return type;
	}

}
