package com.bakerbeach.market.catalog.tmp;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "foo", noClassnameStored = true)
public class MyXProductImpl extends XProductImpl implements MyXProduct {

	private String size;
	private String color;

	@Override
	public String getSize() {
		return size;
	}

	@Override
	public void setSize(String size) {
		this.size = size;
	}
	
	@Override
	public String getColor() {
		return color;
	}
	
	@Override
	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return new StringBuilder("id:").append(getGtin()).append(", ").append("size:").append(getSize()).toString();
	}
}
