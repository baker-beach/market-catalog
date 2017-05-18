package com.bakerbeach.market.xcatalog.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class PriceImpl implements Price {
	protected String group;
	protected Date start;
	protected Currency currency;
	protected BigDecimal value;
	protected String tag;

	@Override
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}
	
	@Override
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
