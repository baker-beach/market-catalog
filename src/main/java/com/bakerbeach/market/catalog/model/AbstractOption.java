package com.bakerbeach.market.catalog.model;

import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.Option;

public abstract class AbstractOption implements Option {
	protected Filter filter;
	protected String code;
	protected String value;
	protected String sort;
	protected Long count;
	protected Boolean selected = false;
	protected Boolean translated = false;

	public AbstractOption() {
	}
	
	public AbstractOption(String code, String value, Long count, Boolean selected) {
		this.code = code;
		this.value = value;
		this.count = count;
		this.selected = selected;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Option) {
			Option other = (Option) obj;
			return this.getCode().equals(other.getCode());
		} else {
			return false;
		}
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
		translated = true;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Boolean getSelected() {
		return selected;
	}
	
	@Override
	public Boolean isSelected() {
		return getSelected();
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
	public Boolean getTranslated() {
		return translated;
	}

	@Override
	public Boolean isTranslated() {
		return getTranslated();
	}
	
}
