package com.bakerbeach.market.catalog.model;


public class FieldOption extends AbstractOption {
	
	public FieldOption(String code, Long count, Boolean selected) {
		this.code = code;
		this.count = count;
		this.selected = selected;		
	}

//	public FieldOption(String value, String translationKey, Long count, Boolean selected) {
//		super(value, translationKey, count, selected);
//	}

}
