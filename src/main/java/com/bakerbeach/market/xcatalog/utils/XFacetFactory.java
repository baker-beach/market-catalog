package com.bakerbeach.market.xcatalog.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bakerbeach.market.xcatalog.model.CategoryFacetImpl;
import com.bakerbeach.market.xcatalog.model.Facet;
import com.bakerbeach.market.xcatalog.model.Facets;
import com.bakerbeach.market.xcatalog.model.FacetsImpl;
import com.bakerbeach.market.xcatalog.model.FieldFacetImpl;

public class XFacetFactory {
	private static List<String> fieldFilters = new ArrayList<String>();
	private static List<String> singleSelectFilters = new ArrayList<String>();
	private static List<String> multiValueFilters = new ArrayList<String>();
	private static List<String> urlRelevantFilters = new ArrayList<String>();

	public static Facets newFacetList(Boolean getParamtersOnly) {
		Facets filterList = new FacetsImpl();

		Facet filter = null;
		for (String id : fieldFilters) {
			if ("category".equalsIgnoreCase(id)) {
				filter = new CategoryFacetImpl("category");
			} else {
				filter = new FieldFacetImpl(id);
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
