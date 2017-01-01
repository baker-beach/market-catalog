package com.bakerbeach.market.catalog.model;

public class PriceRangeFilter extends AbstractFilter {

	private Integer min;

	private Integer max;

	public PriceRangeFilter(String id) {
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
