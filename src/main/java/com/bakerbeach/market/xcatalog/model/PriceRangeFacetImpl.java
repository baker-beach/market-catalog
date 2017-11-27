package com.bakerbeach.market.xcatalog.model;

public class PriceRangeFacetImpl extends AbstractFacet {

	private Integer min;

	private Integer max;

	public PriceRangeFacetImpl(String id) {
		super(id);
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

//	@Override
//	public String toUrl(Option currentOption, Locale locale, MessageSource messageSource) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
