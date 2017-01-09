package com.bakerbeach.market.catalog.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bakerbeach.market.catalog.model.CategoryFilter;
import com.bakerbeach.market.catalog.model.FacetFilterList;
import com.bakerbeach.market.catalog.model.FieldFilter;
import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.FilterList;

public class FacetFactory {
	private static List<String> fieldFilters = new ArrayList<String>();
	private static List<String> singleSelectFilters = new ArrayList<String>();
	private static List<String> multiValueFilters = new ArrayList<String>();
	private static List<String> urlRelevantFilters = new ArrayList<String>();

	public static FilterList newInstance(Boolean getParamtersOnly) {
		FilterList filterList = new FacetFilterList();

		Filter filter = null;
		for (String id : fieldFilters) {
			if ("category".equalsIgnoreCase(id)) {
				filter = new CategoryFilter("category");
			} else {
				filter = new FieldFilter(id);
			}
			filterList.add(filter);

			if (singleSelectFilters.contains(id))
				filter.setMultiSelect(false);
			else
				filter.setMultiSelect(true);

			if (multiValueFilters.contains(id))
				filter.setMultiValue(true);
			else
				filter.setMultiValue(false);

			if (!getParamtersOnly && urlRelevantFilters.contains(id)) {
				filter.setUrlRelevant(true);
				filter.setMultiSelect(false);
			}
		}

		return filterList;
	}

	public void setFieldFilters(String str) {
		fieldFilters = Arrays.asList(str.split(","));
	}

	public void setSingleSelectFilters(String str) {
		singleSelectFilters = Arrays.asList(str.split(","));
	}

	public void setMultiValueFilters(String str) {
		multiValueFilters = Arrays.asList(str.split(","));
	}

	public static List<String> getUrlRelevantFilters() {
		return urlRelevantFilters;
	}
	
	public void setUrlRelevantFilters(String str) {
		urlRelevantFilters = Arrays.asList(str.split(","));
	}

}
