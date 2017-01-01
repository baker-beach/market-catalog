package com.bakerbeach.market.catalog.model;

import com.bakerbeach.market.core.api.model.Type;

public class SimpleProductImpl extends AbstractProduct implements SimpleProduct {
	private static final long serialVersionUID = 1L;

	private Type type = Type.SIMPLE;

	@Override
	public Type getType() {
		return type;
	}
	
}
