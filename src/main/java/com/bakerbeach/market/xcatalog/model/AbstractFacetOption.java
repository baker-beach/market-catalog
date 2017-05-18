package com.bakerbeach.market.xcatalog.model;

public abstract class AbstractFacetOption implements FacetOption {
	protected Facet facet;
	protected String code;
	protected String value;
	protected String sort;
	protected Long count;
	protected Boolean selected = false;
	protected Boolean translated = false;

	public AbstractFacetOption() {
	}
	
	public AbstractFacetOption(String code, String value, Long count, Boolean selected) {
		this.code = code;
		this.value = value;
		this.count = count;
		this.selected = selected;
	}
	
	@Override
	public boolean equals(Object obj) {
//		if (obj != null && obj instanceof Option) {
//			Option other = (Option) obj;
//			return this.getCode().equals(other.getCode());
//		} else {
//			return false;
//		}
		
		return false;
	}

	public Facet getFacet() {
		return facet;
	}

	public void setFacet(Facet filter) {
		this.facet = filter;
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
