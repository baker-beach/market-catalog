package com.bakerbeach.market.catalog.model;

public class PriceRangeOption extends AbstractOption {

	private Integer min;
	private Integer max;
	private Boolean limitedToSale;

	public PriceRangeOption(String value, Integer min, Integer max, Boolean limitedToSale, String translationKey, Long count, Boolean selected) {
		super(value, translationKey, count, selected);
		
		this.setMin(min);
		this.setMax(max);
        this.setLimitedToSale(limitedToSale);
		this.count = count;
		this.selected = selected;
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

    public Boolean getLimitedToSale() {
        return limitedToSale;
    }

    public void setLimitedToSale(Boolean limitedToSale) {
        this.limitedToSale = limitedToSale;
    }
}
